package app.kitsune.patches.music.navigation.components

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.PatchException
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.music.general.startpage.changeStartPagePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.NAVIGATION_CLASS_DESCRIPTOR
import app.kitsune.patches.music.utils.patch.PatchList.NAVIGATION_BAR_COMPONENTS
import app.kitsune.patches.music.utils.playservice.is_6_27_or_greater
import app.kitsune.patches.music.utils.playservice.versionCheckPatch
import app.kitsune.patches.music.utils.resourceid.colorGrey
import app.kitsune.patches.music.utils.resourceid.sharedResourceIdPatch
import app.kitsune.patches.music.utils.resourceid.text1
import app.kitsune.patches.music.utils.resourceid.ytFillSamples
import app.kitsune.patches.music.utils.resourceid.ytFillYouTubeMusic
import app.kitsune.patches.music.utils.resourceid.ytOutlineSamples
import app.kitsune.patches.music.utils.resourceid.ytOutlineYouTubeMusic
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addPreferenceWithIntent
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.util.REGISTER_TEMPLATE_REPLACEMENT
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import app.kitsune.util.indexOfFirstLiteralInstructionOrThrow
import app.kitsune.util.replaceLiteralInstructionCall
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val FLAG = "android:layout_weight"
private const val RESOURCE_FILE_PATH = "res/layout/image_with_text_tab.xml"

private val navigationBarComponentsResourcePatch = resourcePatch(
    description = "navigationBarComponentsResourcePatch"
) {
    execute {
        document(RESOURCE_FILE_PATH).use { document ->
            with(document.getElementsByTagName("ImageView").item(0)) {
                if (attributes.getNamedItem(FLAG) != null)
                    return@with

                document.createAttribute(FLAG)
                    .apply { value = "0.5" }
                    .let(attributes::setNamedItem)
            }
        }
    }
}

@Suppress("unused")
val navigationBarComponentsPatch = bytecodePatch(
    NAVIGATION_BAR_COMPONENTS.title,
    NAVIGATION_BAR_COMPONENTS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        changeStartPagePatch,
        navigationBarComponentsResourcePatch,
        sharedResourceIdPatch,
        settingsPatch,
        versionCheckPatch,
    )

    execute {
        /**
         * Enable custom navigation bar color
         */
        tabLayoutFingerprint.methodOrThrow().apply {
            val constIndex = indexOfFirstLiteralInstructionOrThrow(colorGrey)
            val insertIndex = indexOfFirstInstructionOrThrow(constIndex) {
                opcode == Opcode.INVOKE_VIRTUAL
                        && getReference<MethodReference>()?.name == "setBackgroundColor"
            }
            val insertRegister = getInstruction<FiveRegisterInstruction>(insertIndex).registerD

            addInstructions(
                insertIndex, """
                    invoke-static {}, $NAVIGATION_CLASS_DESCRIPTOR->enableCustomNavigationBarColor()I
                    move-result v$insertRegister
                    """
            )
        }

        /**
         * Hide navigation labels
         */
        tabLayoutTextFingerprint.methodOrThrow().apply {
            val constIndex =
                indexOfFirstLiteralInstructionOrThrow(text1)
            val targetIndex = indexOfFirstInstructionOrThrow(constIndex, Opcode.CHECK_CAST)
            val targetParameter = getInstruction<ReferenceInstruction>(targetIndex).reference
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            if (!targetParameter.toString().endsWith("Landroid/widget/TextView;"))
                throw PatchException("Method signature parameter did not match: $targetParameter")

            addInstruction(
                targetIndex + 1,
                "invoke-static {v$targetRegister}, $NAVIGATION_CLASS_DESCRIPTOR->hideNavigationLabel(Landroid/widget/TextView;)V"
            )
        }

        /**
         * Hide navigation bar & buttons
         */
        tabLayoutTextFingerprint.matchOrThrow().let {
            it.method.apply {
                val stringIndex = it.stringMatches!!.first().index
                val browseIdIndex = indexOfFirstInstructionReversedOrThrow(stringIndex) {
                    opcode == Opcode.IGET_OBJECT &&
                            getReference<FieldReference>()?.type == "Ljava/lang/String;"
                }
                val browseIdReference =
                    getInstruction<ReferenceInstruction>(browseIdIndex).reference as FieldReference
                val fieldName = browseIdReference.name
                val componentIndex = indexOfFirstInstructionOrThrow(stringIndex) {
                    opcode == Opcode.IGET_OBJECT &&
                            getReference<FieldReference>()?.toString() == browseIdReference.toString()
                }
                val browseIdRegister =
                    getInstruction<TwoRegisterInstruction>(componentIndex).registerA
                val componentRegister =
                    getInstruction<TwoRegisterInstruction>(componentIndex).registerB

                val enumIndex = it.patternMatch!!.startIndex + 3
                val enumRegister = getInstruction<OneRegisterInstruction>(enumIndex).registerA
                val insertEnumIndex = indexOfFirstInstructionOrThrow(Opcode.AND_INT_LIT8) - 2

                val pivotTabIndex = indexOfGetVisibilityInstruction(this)
                val pivotTabRegister =
                    getInstruction<FiveRegisterInstruction>(pivotTabIndex).registerC

                val spannedIndex = indexOfSetTextInstruction(this)
                val spannedRegister =
                    getInstruction<FiveRegisterInstruction>(spannedIndex).registerD

                addInstruction(
                    pivotTabIndex,
                    "invoke-static {v$pivotTabRegister}, $NAVIGATION_CLASS_DESCRIPTOR->hideNavigationButton(Landroid/view/View;)V"
                )

                addInstructions(
                    componentIndex + 1, """
                        const-string v$enumRegister, "$fieldName"
                        invoke-static {v$componentRegister, v$browseIdRegister, v$enumRegister}, $NAVIGATION_CLASS_DESCRIPTOR->replaceBrowseId(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$browseIdRegister
                        """
                )

                addInstructions(
                    spannedIndex, """
                        invoke-static {v$spannedRegister}, $NAVIGATION_CLASS_DESCRIPTOR->replaceNavigationLabel(Landroid/text/Spanned;)Landroid/text/Spanned;
                        move-result-object v$spannedRegister
                        """
                )

                addInstruction(
                    insertEnumIndex,
                    "invoke-static {v$enumRegister}, $NAVIGATION_CLASS_DESCRIPTOR->setLastAppNavigationEnum(Ljava/lang/Enum;)V"
                )
            }
        }

        val smaliInstruction = """
            invoke-static {v$REGISTER_TEMPLATE_REPLACEMENT}, $NAVIGATION_CLASS_DESCRIPTOR->replaceNavigationIcon(I)I
            move-result v$REGISTER_TEMPLATE_REPLACEMENT
            """

        arrayOf(
            ytFillSamples,
            ytFillYouTubeMusic,
            ytOutlineSamples,
            ytOutlineYouTubeMusic,
        ).forEach { literal ->
            replaceLiteralInstructionCall(literal, smaliInstruction)
        }

        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_enable_custom_navigation_bar_color",
            "false"
        )
        addPreferenceWithIntent(
            CategoryType.NAVIGATION,
            "revanced_custom_navigation_bar_color_value",
            "revanced_enable_custom_navigation_bar_color"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_home_button",
            "false"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_samples_button",
            "false"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_explore_button",
            "false"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_library_button",
            "false"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_upgrade_button",
            "true"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_bar",
            "false"
        )
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_hide_navigation_label",
            "false"
        )
        if (is_6_27_or_greater) {
            addSwitchPreference(
                CategoryType.NAVIGATION,
                "revanced_replace_navigation_samples_button",
                "false"
            )
        }
        addSwitchPreference(
            CategoryType.NAVIGATION,
            "revanced_replace_navigation_upgrade_button",
            "false"
        )
        addPreferenceWithIntent(
            CategoryType.NAVIGATION,
            "revanced_replace_navigation_button_about"
        )

        updatePatchStatus(NAVIGATION_BAR_COMPONENTS)

    }
}
