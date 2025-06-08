package app.kitsune.patches.youtube.misc.debugging

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.patch.PatchList.ENABLE_DEBUG_LOGGING
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val debuggingPatch = bytecodePatch(
    ENABLE_DEBUG_LOGGING.title,
    ENABLE_DEBUG_LOGGING.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {

        // region add settings

        addPreference(
            arrayOf(
                "SETTINGS: ENABLE_DEBUG_LOGGING"
            ),
            ENABLE_DEBUG_LOGGING
        )

        // endregion

    }
}
