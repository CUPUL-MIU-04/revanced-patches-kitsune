package app.kitsune.patches.music.layout.playeroverlay

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.HIDE_PLAYER_OVERLAY_FILTER
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.util.removeOverlayBackground

@Suppress("unused")
val playerOverlayFilterPatch = resourcePatch(
    HIDE_PLAYER_OVERLAY_FILTER.title,
    HIDE_PLAYER_OVERLAY_FILTER.summary,
    use = false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    execute {
        removeOverlayBackground(
            arrayOf("music_controls_overlay.xml"),
            arrayOf("player_control_screen")
        )

        updatePatchStatus(HIDE_PLAYER_OVERLAY_FILTER)

    }
}

