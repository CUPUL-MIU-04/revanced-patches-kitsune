package app.kitsune.patches.music.video.playback

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.PatchException
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.VIDEO_PATH
import app.kitsune.patches.music.utils.patch.PatchList.VIDEO_PLAYBACK
import app.kitsune.patches.music.utils.playbackSpeedBottomSheetFingerprint
import app.kitsune.patches.music.utils.playbackSpeedFingerprint
import app.kitsune.patches.music.utils.playbackSpeedParentFingerprint
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addPreferenceWithIntent
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.music.video.information.videoIdHook
import app.kitsune.patches.music.video.information.videoInformationPatch
import app.kitsune.patches.shared.customspeed.customPlaybackSpeedPatch
import app.kitsune.util.findMethodOrThrow
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.mutableClassOrThrow
import app.kitsune.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

private const val EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/PlaybackSpeedPatch;"
private const val EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/VideoQualityPatch;"

@Suppress("unused")
val videoPlaybackPatch = bytecodePatch(
    VIDEO_PLAYBACK.title,
    VIDEO_PLAYBACK.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        customPlaybackSpeedPatch(
            "$VIDEO_PATH/CustomPlaybackSpeedPatch;",
            5.0f
        ),
        settingsPatch,
        videoInformationPatch,
    )

    execute {
        // region patch for default playback speed

        playbackSpeedBottomSheetFingerprint.mutableClassOrThrow().let {
            val onItemClickMethod =
                it.methods.find { method -> method.name == "onItemClick" }
                    ?: throw PatchException("Failed to find onItemClick method")

            onItemClickMethod.apply {
                val targetIndex = indexOfFirstInstructionOrThrow(Opcode.IGET)
                val targetRegister =
                    getInstruction<TwoRegisterInstruction>(targetIndex).registerA

                addInstruction(
                    targetIndex + 1,
                    "invoke-static {v$targetRegister}, $EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->userSelectedPlaybackSpeed(F)V"
                )
            }
        }

        playbackSpeedFingerprint.matchOrThrow(playbackSpeedParentFingerprint).let {
            it.method.apply {
                val startIndex = it.patternMatch!!.startIndex
                val speedRegister =
                    getInstruction<OneRegisterInstruction>(startIndex + 1).registerA

                addInstructions(
                    startIndex + 2, """
                        invoke-static {v$speedRegister}, $EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->getPlaybackSpeed(F)F
                        move-result v$speedRegister
                        """
                )
            }
        }

        // endregion

        // region patch for default video quality

        userQualityChangeFingerprint.matchOrThrow().let {
            it.method.apply {
                val endIndex = it.patternMatch!!.endIndex
                val qualityChangedClass =
                    getInstruction<ReferenceInstruction>(endIndex).reference.toString()

                findMethodOrThrow(qualityChangedClass) {
                    name == "onItemClick"
                }.addInstruction(
                    0,
                    "invoke-static {}, $EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR->userSelectedVideoQuality()V"
                )
            }
        }

        videoIdHook("$EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;)V")

        // endregion

        addPreferenceWithIntent(
            CategoryType.VIDEO,
            "revanced_custom_playback_speeds"
        )
        addSwitchPreference(
            CategoryType.VIDEO,
            "revanced_remember_playback_speed_last_selected",
            "true"
        )
        addSwitchPreference(
            CategoryType.VIDEO,
            "revanced_remember_playback_speed_last_selected_toast",
            "true",
            "revanced_remember_playback_speed_last_selected"
        )
        addSwitchPreference(
            CategoryType.VIDEO,
            "revanced_remember_video_quality_last_selected",
            "true"
        )
        addSwitchPreference(
            CategoryType.VIDEO,
            "revanced_remember_video_quality_last_selected_toast",
            "true",
            "revanced_remember_video_quality_last_selected"
        )

        updatePatchStatus(VIDEO_PLAYBACK)

    }
}
