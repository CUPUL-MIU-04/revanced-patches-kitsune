package app.kitsune.patches.music.misc.thumbnails

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.BYPASS_IMAGE_REGION_RESTRICTIONS
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.imageurl.addImageUrlHook
import app.kitsune.patches.shared.imageurl.cronetImageUrlHookPatch

@Suppress("unused")
val bypassImageRegionRestrictionsPatch = bytecodePatch(
    BYPASS_IMAGE_REGION_RESTRICTIONS.title,
    BYPASS_IMAGE_REGION_RESTRICTIONS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        cronetImageUrlHookPatch(false)
    )

    execute {
        addImageUrlHook()

        addSwitchPreference(
            CategoryType.MISC,
            "revanced_bypass_image_region_restrictions",
            "false"
        )

        updatePatchStatus(BYPASS_IMAGE_REGION_RESTRICTIONS)

    }
}