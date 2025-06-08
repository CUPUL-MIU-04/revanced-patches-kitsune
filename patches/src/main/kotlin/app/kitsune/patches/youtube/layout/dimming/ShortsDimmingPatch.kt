package app.kitsune.patches.youtube.layout.dimming

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.patch.PatchList.HIDE_SHORTS_DIMMING
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.removeOverlayBackground

@Suppress("unused")
val shortsDimmingPatch = resourcePatch(
    HIDE_SHORTS_DIMMING.title,
    HIDE_SHORTS_DIMMING.summary,
    false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {

        removeOverlayBackground(
            arrayOf("reel_player_overlay_scrims.xml"),
            arrayOf("reel_player_overlay_v2_scrims_vertical")
        )
        removeOverlayBackground(
            arrayOf("reel_watch_fragment.xml"),
            arrayOf("reel_scrim_shorts_while_top")
        )

        addPreference(HIDE_SHORTS_DIMMING)

    }
}
