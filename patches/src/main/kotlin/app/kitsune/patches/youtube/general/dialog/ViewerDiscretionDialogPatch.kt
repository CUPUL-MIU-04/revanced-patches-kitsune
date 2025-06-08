package app.kitsune.patches.youtube.general.dialog

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.dialog.baseViewerDiscretionDialogPatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.extension.Constants.GENERAL_CLASS_DESCRIPTOR
import app.kitsune.patches.youtube.utils.patch.PatchList.REMOVE_VIEWER_DISCRETION_DIALOG
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val viewerDiscretionDialogPatch = bytecodePatch(
    REMOVE_VIEWER_DISCRETION_DIALOG.title,
    REMOVE_VIEWER_DISCRETION_DIALOG.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        baseViewerDiscretionDialogPatch(
            GENERAL_CLASS_DESCRIPTOR,
            true
        ),
        settingsPatch,
    )

    execute {

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: GENERAL",
                "SETTINGS: REMOVE_VIEWER_DISCRETION_DIALOG"
            ),
            REMOVE_VIEWER_DISCRETION_DIALOG
        )

        // endregion

    }
}
