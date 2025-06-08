package app.kitsune.patches.youtube.alternative.thumbnails

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.imageurl.addImageUrlErrorCallbackHook
import app.kitsune.patches.shared.imageurl.addImageUrlHook
import app.kitsune.patches.shared.imageurl.addImageUrlSuccessCallbackHook
import app.kitsune.patches.shared.imageurl.cronetImageUrlHookPatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.extension.Constants.ALTERNATIVE_THUMBNAILS_CLASS_DESCRIPTOR
import app.kitsune.patches.youtube.utils.navigation.navigationBarHookPatch
import app.kitsune.patches.youtube.utils.patch.PatchList.ALTERNATIVE_THUMBNAILS
import app.kitsune.patches.youtube.utils.playertype.playerTypeHookPatch
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val alternativeThumbnailsPatch = bytecodePatch(
    ALTERNATIVE_THUMBNAILS.title,
    ALTERNATIVE_THUMBNAILS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        cronetImageUrlHookPatch(true),
        navigationBarHookPatch,
        playerTypeHookPatch,
        settingsPatch,
    )
    execute {

        addImageUrlHook(ALTERNATIVE_THUMBNAILS_CLASS_DESCRIPTOR)
        addImageUrlSuccessCallbackHook(ALTERNATIVE_THUMBNAILS_CLASS_DESCRIPTOR)
        addImageUrlErrorCallbackHook(ALTERNATIVE_THUMBNAILS_CLASS_DESCRIPTOR)

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: ALTERNATIVE_THUMBNAILS",
                "SETTINGS: ALTERNATIVE_THUMBNAILS"
            ),
            ALTERNATIVE_THUMBNAILS
        )

        // endregion

    }
}
