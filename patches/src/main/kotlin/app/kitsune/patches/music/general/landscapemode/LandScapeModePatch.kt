package app.kitsune.patches.music.general.landscapemode

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.GENERAL_CLASS_DESCRIPTOR
import app.kitsune.patches.music.utils.patch.PatchList.ENABLE_LANDSCAPE_MODE
import app.kitsune.patches.music.utils.resourceid.sharedResourceIdPatch
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.util.fingerprint.matchOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val landScapeModePatch = bytecodePatch(
    ENABLE_LANDSCAPE_MODE.title,
    ENABLE_LANDSCAPE_MODE.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        sharedResourceIdPatch,
        settingsPatch,
    )

    execute {
        tabletIdentifierFingerprint.matchOrThrow().let {
            it.method.apply {
                val targetIndex = it.patternMatch!!.endIndex
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1, """
                        invoke-static {v$targetRegister}, $GENERAL_CLASS_DESCRIPTOR->enableLandScapeMode(Z)Z
                        move-result v$targetRegister
                        """
                )
            }
        }

        addSwitchPreference(
            CategoryType.GENERAL,
            "revanced_enable_landscape_mode",
            "false"
        )

        updatePatchStatus(ENABLE_LANDSCAPE_MODE)

    }
}
