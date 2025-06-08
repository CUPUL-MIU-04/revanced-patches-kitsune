package app.kitsune.patches.music.misc.tracking

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.SANITIZE_SHARING_LINKS
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.tracking.baseSanitizeUrlQueryPatch

@Suppress("unused")
val sanitizeUrlQueryPatch = bytecodePatch(
    SANITIZE_SHARING_LINKS.title,
    SANITIZE_SHARING_LINKS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        baseSanitizeUrlQueryPatch,
        settingsPatch,
    )

    execute {
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_sanitize_sharing_links",
            "true"
        )

        updatePatchStatus(SANITIZE_SHARING_LINKS)

    }
}