package app.kitsune.patches.youtube.misc.watchhistory

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.trackingurlhook.hookWatchHistory
import app.kitsune.patches.shared.trackingurlhook.trackingUrlHookPatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.patch.PatchList.WATCH_HISTORY
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val watchHistoryPatch = bytecodePatch(
    WATCH_HISTORY.title,
    WATCH_HISTORY.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        trackingUrlHookPatch,
    )

    execute {

        hookWatchHistory()

        // region add settings

        addPreference(
            arrayOf(
                "SETTINGS: WATCH_HISTORY"
            ),
            WATCH_HISTORY
        )

        // endregion

    }
}
