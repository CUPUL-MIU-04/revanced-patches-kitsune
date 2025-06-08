package app.kitsune.patches.music.misc.drc

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.MISC_PATH
import app.kitsune.patches.music.utils.patch.PatchList.DISABLE_DRC_AUDIO
import app.kitsune.patches.music.utils.playservice.is_7_13_or_greater
import app.kitsune.patches.music.utils.playservice.versionCheckPatch
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.formatStreamModelConstructorFingerprint
import app.kitsune.util.fingerprint.matchOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$MISC_PATH/DrcAudioPatch;"

@Suppress("unused")
val DrcAudioPatch = bytecodePatch(
    DISABLE_DRC_AUDIO.title,
    DISABLE_DRC_AUDIO.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        versionCheckPatch,
    )

    execute {
        val fingerprint = if (is_7_13_or_greater) {
            compressionRatioFingerprint
        } else {
            compressionRatioLegacyFingerprint
        }

        fingerprint.matchOrThrow(formatStreamModelConstructorFingerprint).let {
            it.method.apply {
                val insertIndex = it.patternMatch!!.endIndex
                val insertRegister =
                    getInstruction<TwoRegisterInstruction>(insertIndex - 1).registerA

                addInstructions(
                    insertIndex,
                    """
                        invoke-static {v$insertRegister}, $EXTENSION_CLASS_DESCRIPTOR->disableDrcAudio(F)F
                        move-result v$insertRegister
                        """
                )
            }
        }

        addSwitchPreference(
            CategoryType.MISC,
            "revanced_disable_drc_audio",
            "false"
        )

        updatePatchStatus(DISABLE_DRC_AUDIO)

    }
}
