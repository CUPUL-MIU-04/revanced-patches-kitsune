package app.kitsune.patches.music.utils.flyoutmenu

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.extension.Constants.EXTENSION_PATH
import app.kitsune.patches.music.utils.extension.sharedExtensionPatch
import app.kitsune.patches.music.utils.playbackRateBottomSheetClassFingerprint
import app.kitsune.patches.music.utils.resourceid.sharedResourceIdPatch
import app.kitsune.util.addStaticFieldToExtension
import app.kitsune.util.fingerprint.methodOrThrow

private const val EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR =
    "$EXTENSION_PATH/utils/VideoUtils;"

val flyoutMenuHookPatch = bytecodePatch(
    description = "flyoutMenuHookPatch",
) {
    dependsOn(
        sharedExtensionPatch,
        sharedResourceIdPatch,
    )

    execute {

        playbackRateBottomSheetClassFingerprint.methodOrThrow().apply {
            val smaliInstructions =
                """
                    if-eqz v0, :ignore
                    invoke-virtual {v0}, $definingClass->$name()V
                    :ignore
                    return-void
                    """

            addStaticFieldToExtension(
                EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR,
                "showPlaybackSpeedFlyoutMenu",
                "playbackRateBottomSheetClass",
                definingClass,
                smaliInstructions
            )
        }

    }
}
