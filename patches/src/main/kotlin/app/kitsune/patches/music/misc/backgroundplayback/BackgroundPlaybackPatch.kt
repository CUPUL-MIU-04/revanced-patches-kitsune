package app.kitsune.patches.music.misc.backgroundplayback

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.REMOVE_BACKGROUND_PLAYBACK_RESTRICTIONS
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.fingerprint.resolvable
import app.kitsune.util.getReference
import app.kitsune.util.getWalkerMethod
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstStringInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val backgroundPlaybackPatch = bytecodePatch(
    REMOVE_BACKGROUND_PLAYBACK_RESTRICTIONS.title,
    REMOVE_BACKGROUND_PLAYBACK_RESTRICTIONS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {
        // region patch for background play

        backgroundPlaybackManagerFingerprint.methodOrThrow().addInstructions(
            0, """
                const/4 v0, 0x1
                return v0
                """
        )

        // endregion

        // region patch for exclusive audio playback

        // don't play music video
        musicBrowserServiceFingerprint.matchOrThrow().let {
            it.method.apply {
                val stringIndex = it.stringMatches!!.first().index
                val targetIndex = indexOfFirstInstructionOrThrow(stringIndex) {
                    val reference = getReference<MethodReference>()
                    opcode == Opcode.INVOKE_VIRTUAL &&
                            reference?.returnType == "Z" &&
                            reference.parameterTypes.size == 0
                }

                getWalkerMethod(targetIndex).addInstructions(
                    0, """
                        const/4 v0, 0x1
                        return v0
                        """
                )
            }
        }

        // don't play podcast videos
        // enable by default from YouTube Music 7.05.52+

        if (podCastConfigFingerprint.resolvable() &&
            dataSavingSettingsFragmentFingerprint.resolvable()
        ) {
            podCastConfigFingerprint.methodOrThrow().apply {
                val insertIndex = implementation!!.instructions.size - 1
                val targetRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex,
                    "const/4 v$targetRegister, 0x1"
                )
            }

            dataSavingSettingsFragmentFingerprint.methodOrThrow().apply {
                val insertIndex =
                    indexOfFirstStringInstructionOrThrow("pref_key_dont_play_nma_video") + 4
                val targetRegister = getInstruction<FiveRegisterInstruction>(insertIndex).registerD

                addInstruction(
                    insertIndex,
                    "const/4 v$targetRegister, 0x1"
                )
            }
        }

        // endregion

        // region patch for minimized playback

        kidsBackgroundPlaybackPolicyControllerFingerprint.methodOrThrow().addInstruction(
            0, "return-void"
        )

        // endregion

        updatePatchStatus(REMOVE_BACKGROUND_PLAYBACK_RESTRICTIONS)

    }
}
