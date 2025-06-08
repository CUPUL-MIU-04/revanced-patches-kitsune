package app.kitsune.patches.music.general.startpage

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.GENERAL_PATH
import app.kitsune.patches.music.utils.patch.PatchList.CHANGE_START_PAGE
import app.kitsune.patches.music.utils.playservice.is_6_27_or_greater
import app.kitsune.patches.music.utils.playservice.versionCheckPatch
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addPreferenceWithIntent
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.util.addEntryValues
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import app.kitsune.util.indexOfFirstStringInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$GENERAL_PATH/ChangeStartPagePatch;"

private val changeStartPageResourcePatch = resourcePatch(
    description = "changeStartPageResourcePatch"
) {
    dependsOn(
        settingsPatch,
        versionCheckPatch,
    )

    execute {
        fun appendStartPage(startPage: String) {
            addEntryValues(
                "revanced_change_start_page_entries",
                "@string/revanced_change_start_page_entry_$startPage",
            )
            addEntryValues(
                "revanced_change_start_page_entry_values",
                startPage.uppercase(),
            )
        }

        if (is_6_27_or_greater) {
            appendStartPage("search")
        }
        appendStartPage("subscriptions")
    }
}

@Suppress("unused")
val changeStartPagePatch = bytecodePatch(
    CHANGE_START_PAGE.title,
    CHANGE_START_PAGE.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        changeStartPageResourcePatch,
        settingsPatch,
    )

    execute {

        coldStartIntentFingerprint.methodOrThrow().addInstruction(
            0,
            "invoke-static {p1}, $EXTENSION_CLASS_DESCRIPTOR->overrideIntent(Landroid/content/Intent;)V"
        )

        coldStartUpFingerprint.methodOrThrow().apply {
            val defaultBrowseIdIndex = indexOfFirstStringInstructionOrThrow(DEFAULT_BROWSE_ID)
            val browseIdIndex = indexOfFirstInstructionReversedOrThrow(defaultBrowseIdIndex) {
                opcode == Opcode.IGET_OBJECT &&
                        getReference<FieldReference>()?.type == "Ljava/lang/String;"
            }
            val browseIdRegister = getInstruction<TwoRegisterInstruction>(browseIdIndex).registerA

            addInstructions(
                browseIdIndex + 1, """
                    invoke-static {v$browseIdRegister}, $EXTENSION_CLASS_DESCRIPTOR->overrideBrowseId(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$browseIdRegister
                    """
            )
        }

        addPreferenceWithIntent(
            CategoryType.GENERAL,
            "revanced_change_start_page"
        )

        updatePatchStatus(CHANGE_START_PAGE)

    }
}
