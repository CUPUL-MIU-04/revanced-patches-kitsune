package app.kitsune.patches.music.misc.debugging

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.ENABLE_DEBUG_LOGGING
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch

@Suppress("unused")
val debuggingPatch = resourcePatch(
    ENABLE_DEBUG_LOGGING.title,
    ENABLE_DEBUG_LOGGING.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_enable_debug_logging",
            "false"
        )
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_enable_debug_buffer_logging",
            "false",
            "revanced_enable_debug_logging"
        )

        updatePatchStatus(ENABLE_DEBUG_LOGGING)

    }
}
