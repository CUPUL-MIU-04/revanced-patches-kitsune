package app.kitsune.patches.youtube.video.playback

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.PatchException
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.shared.customspeed.customPlaybackSpeedPatch
import app.kitsune.patches.shared.litho.addLithoFilter
import app.kitsune.patches.shared.litho.lithoFilterPatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.dismiss.dismissPlayerHookPatch
import app.kitsune.patches.youtube.utils.dismiss.hookDismissObserver
import app.kitsune.patches.youtube.utils.extension.Constants.COMPONENTS_PATH
import app.kitsune.patches.youtube.utils.extension.Constants.PATCH_STATUS_CLASS_DESCRIPTOR
import app.kitsune.patches.youtube.utils.extension.Constants.VIDEO_PATH
import app.kitsune.patches.youtube.utils.fix.shortsplayback.shortsPlaybackPatch
import app.kitsune.patches.youtube.utils.flyoutmenu.flyoutMenuHookPatch
import app.kitsune.patches.youtube.utils.patch.PatchList.VIDEO_PLAYBACK
import app.kitsune.patches.youtube.utils.playertype.playerTypeHookPatch
import app.kitsune.patches.youtube.utils.qualityMenuViewInflateFingerprint
import app.kitsune.patches.youtube.utils.recyclerview.recyclerViewTreeObserverHook
import app.kitsune.patches.youtube.utils.recyclerview.recyclerViewTreeObserverPatch
import app.kitsune.patches.youtube.utils.resourceid.sharedResourceIdPatch
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.patches.youtube.video.information.hookBackgroundPlayVideoInformation
import app.kitsune.patches.youtube.video.information.hookVideoInformation
import app.kitsune.patches.youtube.video.information.onCreateHook
import app.kitsune.patches.youtube.video.information.speedSelectionInsertMethod
import app.kitsune.patches.youtube.video.information.videoInformationPatch
import app.kitsune.patches.youtube.video.videoid.hookPlayerResponseVideoId
import app.kitsune.patches.youtube.video.videoid.videoIdPatch
import app.kitsune.util.findMethodOrThrow
import app.kitsune.util.fingerprint.definingClassOrThrow
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.fingerprint.resolvable
import app.kitsune.util.getReference
import app.kitsune.util.getWalkerMethod
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstStringInstructionOrThrow
import app.kitsune.util.updatePatchStatus
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val PLAYBACK_SPEED_MENU_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/PlaybackSpeedMenuFilter;"
private const val VIDEO_QUALITY_MENU_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/VideoQualityMenuFilter;"
private const val EXTENSION_AV1_CODEC_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/AV1CodecPatch;"
private const val EXTENSION_VP9_CODEC_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/VP9CodecPatch;"
private const val EXTENSION_CUSTOM_PLAYBACK_SPEED_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/CustomPlaybackSpeedPatch;"
private const val EXTENSION_HDR_VIDEO_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/HDRVideoPatch;"
private const val EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/PlaybackSpeedPatch;"
private const val EXTENSION_RELOAD_VIDEO_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/ReloadVideoPatch;"
private const val EXTENSION_RESTORE_OLD_VIDEO_QUALITY_MENU_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/RestoreOldVideoQualityMenuPatch;"
private const val EXTENSION_SPOOF_DEVICE_DIMENSIONS_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/SpoofDeviceDimensionsPatch;"
private const val EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR =
    "$VIDEO_PATH/VideoQualityPatch;"

@Suppress("unused")
val videoPlaybackPatch = bytecodePatch(
    VIDEO_PLAYBACK.title,
    VIDEO_PLAYBACK.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        customPlaybackSpeedPatch(
            "$VIDEO_PATH/CustomPlaybackSpeedPatch;",
            8.0f
        ),
        flyoutMenuHookPatch,
        lithoFilterPatch,
        dismissPlayerHookPatch,
        playerTypeHookPatch,
        recyclerViewTreeObserverPatch,
        shortsPlaybackPatch,
        videoIdPatch,
        videoInformationPatch,
        sharedResourceIdPatch,
    )

    execute {

        var settingArray = arrayOf(
            "PREFERENCE_SCREEN: VIDEO"
        )

        // region patch for custom playback speed

        recyclerViewTreeObserverHook("$EXTENSION_CUSTOM_PLAYBACK_SPEED_CLASS_DESCRIPTOR->onFlyoutMenuCreate(Landroid/support/v7/widget/RecyclerView;)V")
        addLithoFilter(PLAYBACK_SPEED_MENU_FILTER_CLASS_DESCRIPTOR)

        // endregion

        // region patch for disable HDR video

        hdrCapabilityFingerprint.methodOrThrow().apply {
            val stringIndex =
                indexOfFirstStringInstructionOrThrow("av1_profile_main_10_hdr_10_plus_supported")
            val walkerIndex = indexOfFirstInstructionOrThrow(stringIndex) {
                val reference = getReference<MethodReference>()
                reference?.parameterTypes == listOf("I", "Landroid/view/Display;") &&
                        reference.returnType == "Z"
            }

            val walkerMethod = getWalkerMethod(walkerIndex)
            walkerMethod.apply {
                addInstructionsWithLabels(
                    0, """
                        invoke-static {}, $EXTENSION_HDR_VIDEO_CLASS_DESCRIPTOR->disableHDRVideo()Z
                        move-result v0
                        if-nez v0, :default
                        return v0
                        """, ExternalLabel("default", getInstruction(0))
                )
            }
        }

        // endregion

        // region patch for default playback speed

        val newMethod =
            playbackSpeedChangedFromRecyclerViewFingerprint.methodOrThrow(
                qualityChangedFromRecyclerViewFingerprint
            )

        arrayOf(
            newMethod,
            speedSelectionInsertMethod
        ).forEach {
            it.apply {
                val speedSelectionValueInstructionIndex =
                    indexOfFirstInstructionOrThrow(Opcode.IGET)
                val speedSelectionValueRegister =
                    getInstruction<TwoRegisterInstruction>(speedSelectionValueInstructionIndex).registerA

                addInstruction(
                    speedSelectionValueInstructionIndex + 1,
                    "invoke-static {v$speedSelectionValueRegister}, " +
                            "$EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->userSelectedPlaybackSpeed(F)V"
                )
            }
        }

        loadVideoParamsFingerprint.matchOrThrow(loadVideoParamsParentFingerprint).let {
            it.method.apply {
                val targetIndex = it.patternMatch!!.endIndex
                val targetReference =
                    getInstruction<ReferenceInstruction>(targetIndex).reference as MethodReference

                findMethodOrThrow(definingClass) {
                    name == targetReference.name
                }.apply {
                    val insertIndex = implementation!!.instructions.lastIndex
                    val insertRegister =
                        getInstruction<OneRegisterInstruction>(insertIndex).registerA

                    addInstructions(
                        insertIndex, """
                            invoke-static {v$insertRegister}, $EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->getPlaybackSpeed(F)F
                            move-result v$insertRegister
                            """
                    )
                }
            }
        }

        hookBackgroundPlayVideoInformation("$EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZ)V")
        hookVideoInformation("$EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZ)V")
        hookPlayerResponseVideoId("$EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->fetchMusicRequest(Ljava/lang/String;Z)V")
        hookDismissObserver("$EXTENSION_PLAYBACK_SPEED_CLASS_DESCRIPTOR->onDismiss()V")

        updatePatchStatus(PATCH_STATUS_CLASS_DESCRIPTOR, "RememberPlaybackSpeed")

        // endregion

        // region patch for default video quality

        qualityChangedFromRecyclerViewFingerprint.matchOrThrow().let {
            it.method.apply {
                val index = it.patternMatch!!.startIndex

                addInstruction(
                    index + 1,
                    "invoke-static {}, $EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR->userSelectedVideoQuality()V"
                )

            }
        }

        qualitySetterFingerprint.matchOrThrow().let {
            val onItemClickMethod =
                it.classDef.methods.find { method -> method.name == "onItemClick" }

            onItemClickMethod?.apply {
                addInstruction(
                    0,
                    "invoke-static {}, $EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR->userSelectedVideoQuality()V"
                )
            } ?: throw PatchException("Failed to find onItemClick method")
        }

        hookBackgroundPlayVideoInformation("$EXTENSION_RELOAD_VIDEO_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZ)V")
        hookVideoInformation("$EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZ)V")
        onCreateHook(
            EXTENSION_VIDEO_QUALITY_CLASS_DESCRIPTOR,
            "newVideoStarted"
        )

        // endregion

        // region patch for restore old video quality menu

        qualityMenuViewInflateFingerprint.matchOrThrow().let {
            it.method.apply {
                val insertIndex = indexOfFirstInstructionOrThrow(Opcode.CHECK_CAST)
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex + 1,
                    "invoke-static { v$insertRegister }, " +
                            "$EXTENSION_RESTORE_OLD_VIDEO_QUALITY_MENU_CLASS_DESCRIPTOR->restoreOldVideoQualityMenu(Landroid/widget/ListView;)V"
                )
            }
            val onItemClickMethod =
                it.classDef.methods.find { method -> method.name == "onItemClick" }

            onItemClickMethod?.apply {
                val insertIndex = indexOfFirstInstructionOrThrow(Opcode.IGET_OBJECT)
                val insertRegister = getInstruction<TwoRegisterInstruction>(insertIndex).registerA

                val jumpIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.IGET_OBJECT
                            && this.getReference<FieldReference>()?.type == qualitySetterFingerprint.definingClassOrThrow()
                }

                addInstructionsWithLabels(
                    insertIndex, """
                        invoke-static {}, $EXTENSION_RESTORE_OLD_VIDEO_QUALITY_MENU_CLASS_DESCRIPTOR->restoreOldVideoQualityMenu()Z
                        move-result v$insertRegister
                        if-nez v$insertRegister, :show
                        """, ExternalLabel("show", getInstruction(jumpIndex))
                )
            } ?: throw PatchException("Failed to find onItemClick method")
        }

        recyclerViewTreeObserverHook("$EXTENSION_RESTORE_OLD_VIDEO_QUALITY_MENU_CLASS_DESCRIPTOR->onFlyoutMenuCreate(Landroid/support/v7/widget/RecyclerView;)V")
        addLithoFilter(VIDEO_QUALITY_MENU_FILTER_CLASS_DESCRIPTOR)

        // endregion

        // region patch for spoof device dimensions

        findMethodOrThrow(
            deviceDimensionsModelToStringFingerprint.definingClassOrThrow()
        ).addInstructions(
            1, // Add after super call.
            mapOf(
                1 to "MinHeightOrWidth", // p1 = min height
                2 to "MaxHeightOrWidth", // p2 = max height
                3 to "MinHeightOrWidth", // p3 = min width
                4 to "MaxHeightOrWidth"  // p4 = max width
            ).map { (parameter, method) ->
                """
                    invoke-static { p$parameter }, $EXTENSION_SPOOF_DEVICE_DIMENSIONS_CLASS_DESCRIPTOR->get$method(I)I
                    move-result p$parameter
                    """
            }.joinToString("\n") { it }
        )

        // endregion

        // region patch for disable AV1 codec

        // replace av1 codec

        if (av1CodecFingerprint.resolvable()) {
            av1CodecFingerprint.methodOrThrow().apply {
                val insertIndex = indexOfFirstStringInstructionOrThrow("video/av01")
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstructions(
                    insertIndex + 1, """
                        invoke-static/range {v$insertRegister .. v$insertRegister}, $EXTENSION_AV1_CODEC_CLASS_DESCRIPTOR->replaceCodec(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$insertRegister
                        """
                )
            }
            settingArray += "SETTINGS: REPLACE_AV1_CODEC"
        }

        // region patch for disable VP9 codec

        vp9CapabilityFingerprint.methodOrThrow().apply {
            addInstructionsWithLabels(
                0, """
                    invoke-static {}, $EXTENSION_VP9_CODEC_CLASS_DESCRIPTOR->disableVP9Codec()Z
                    move-result v0
                    if-nez v0, :default
                    return v0
                    """, ExternalLabel("default", getInstruction(0))
            )
        }

        // endregion

        // region add settings

        addPreference(settingArray, VIDEO_PLAYBACK)

        // endregion
    }
}
