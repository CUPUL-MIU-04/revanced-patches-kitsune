package app.kitsune.patches.reddit.utils.settings

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.replaceInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patcher.patch.stringOption
import app.kitsune.patcher.util.proxy.mutableTypes.MutableMethod
import app.kitsune.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.reddit.utils.extension.Constants.EXTENSION_PATH
import app.kitsune.patches.reddit.utils.extension.sharedExtensionPatch
import app.kitsune.patches.reddit.utils.patch.PatchList
import app.kitsune.patches.reddit.utils.patch.PatchList.SETTINGS_FOR_REDDIT
import app.kitsune.patches.shared.sharedSettingFingerprint
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import app.kitsune.util.indexOfFirstStringInstructionOrThrow
import app.kitsune.util.valueOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import kotlin.io.path.exists

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$EXTENSION_PATH/settings/ActivityHook;->initialize(Landroid/app/Activity;)V"

private lateinit var acknowledgementsLabelBuilderMethod: MutableMethod
private lateinit var settingsStatusLoadMethod: MutableMethod

var is_2024_26_or_greater = false
    private set
var is_2024_41_or_greater = false
    private set
var is_2025_01_or_greater = false
    private set
var is_2025_05_or_greater = false
    private set
var is_2025_06_or_greater = false
    private set

private val settingsBytecodePatch = bytecodePatch(
    description = "settingsBytecodePatch"
) {

    execute {

        /**
         * Set version info
         */
        redditInternalFeaturesFingerprint.methodOrThrow().apply {
            val versionIndex = indexOfFirstInstructionOrThrow {
                opcode == Opcode.CONST_STRING
                        && (this as? BuilderInstruction21c)?.reference.toString().startsWith("202")
            }

            val versionNumber =
                getInstruction<BuilderInstruction21c>(versionIndex).reference.toString()
                    .replace(".", "").toInt()

            is_2024_26_or_greater = 2024260 <= versionNumber
            is_2024_41_or_greater = 2024410 <= versionNumber
            is_2025_01_or_greater = 2025010 <= versionNumber
            is_2025_05_or_greater = 2025050 <= versionNumber
            is_2025_06_or_greater = 2025060 <= versionNumber
        }

        /**
         * Set SharedPrefCategory
         */
        sharedSettingFingerprint.methodOrThrow().apply {
            val stringIndex = indexOfFirstInstructionOrThrow(Opcode.CONST_STRING)
            val stringRegister = getInstruction<OneRegisterInstruction>(stringIndex).registerA

            replaceInstruction(
                stringIndex,
                "const-string v$stringRegister, \"reddit_revanced\""
            )
        }

        /**
         * Replace settings label
         */
        acknowledgementsLabelBuilderMethod =
            acknowledgementsLabelBuilderFingerprint.methodOrThrow()

        /**
         * Initialize settings activity
         */
        ossLicensesMenuActivityOnCreateFingerprint.matchOrThrow().let {
            it.method.apply {
                val insertIndex = it.patternMatch!!.startIndex + 1

                addInstructions(
                    insertIndex, """
                        invoke-static {p0}, $EXTENSION_METHOD_DESCRIPTOR
                        return-void
                        """
                )
            }
        }

        settingsStatusLoadMethod = settingsStatusLoadFingerprint.methodOrThrow()
    }
}

internal fun updateSettingsLabel(label: String) =
    acknowledgementsLabelBuilderMethod.apply {
        val stringIndex = indexOfFirstStringInstructionOrThrow("onboardingAnalytics")
        val insertIndex = indexOfFirstInstructionReversedOrThrow(stringIndex) {
            opcode == Opcode.INVOKE_VIRTUAL &&
                    getReference<MethodReference>()?.name == "getString"
        } + 2
        val insertRegister =
            getInstruction<OneRegisterInstruction>(insertIndex - 1).registerA

        addInstruction(
            insertIndex,
            "const-string v$insertRegister, \"$label\""
        )
    }

internal fun updatePatchStatus(description: String) =
    settingsStatusLoadMethod.addInstruction(
        0,
        "invoke-static {}, $EXTENSION_PATH/settings/SettingsStatus;->$description()V"
    )

internal fun updatePatchStatus(patch: PatchList) {
    patch.included = true
}

internal fun updatePatchStatus(
    description: String,
    patch: PatchList
) {
    updatePatchStatus(description)
    updatePatchStatus(patch)
}

private const val DEFAULT_LABEL = "RVX"

val settingsPatch = resourcePatch(
    SETTINGS_FOR_REDDIT.title,
    SETTINGS_FOR_REDDIT.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        sharedExtensionPatch,
        settingsBytecodePatch
    )

    val rvxSettingsLabel = stringOption(
        key = "rvxSettingsLabel",
        default = DEFAULT_LABEL,
        values = mapOf(
            "ReVanced Extended" to "ReVanced Extended",
            "RVX" to DEFAULT_LABEL,
        ),
        title = "RVX settings menu name",
        description = "The name of the RVX settings menu.",
        required = true
    )

    execute {
        /**
         * Replace settings icon and label
         */
        val settingsLabel = rvxSettingsLabel
            .valueOrThrow()

        arrayOf(
            "preferences.xml",
            "preferences_logged_in.xml",
            "preferences_logged_in_old.xml",
        ).forEach { targetXML ->
            val resDirectory = get("res")
            val targetXml = resDirectory.resolve("xml").resolve(targetXML).toPath()

            if (targetXml.exists()) {
                val preference = get("res/xml/$targetXML")

                preference.writeText(
                    preference.readText()
                        .replace(
                            "\"@drawable/icon_text_post\" android:title=\"@string/label_acknowledgements\"",
                            "\"@drawable/icon_beta_planet\" android:title=\"$settingsLabel\""
                        )
                )
            }
        }

        updateSettingsLabel(settingsLabel)
        updatePatchStatus(SETTINGS_FOR_REDDIT)
    }
}
