package app.kitsune.patches.music.misc.watchhistory

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.WATCH_HISTORY
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.addPreferenceWithIntent
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.trackingurlhook.hookWatchHistory
import app.kitsune.patches.shared.trackingurlhook.trackingUrlHookPatch

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

        addPreferenceWithIntent(
            CategoryType.MISC,
            "revanced_watch_history_type"
        )
    }

}