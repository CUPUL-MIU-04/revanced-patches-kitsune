package app.kitsune.patches.shared.returnyoutubeusername

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.extension.Constants.PATCHES_PATH
import app.kitsune.patches.shared.textcomponent.hookSpannableString
import app.kitsune.patches.shared.textcomponent.hookTextComponent
import app.kitsune.patches.shared.textcomponent.textComponentPatch

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$PATCHES_PATH/ReturnYouTubeUsernamePatch;"

val baseReturnYouTubeUsernamePatch = bytecodePatch(
    description = "baseReturnYouTubeUsernamePatch"
) {
    dependsOn(textComponentPatch)

    execute {
        hookSpannableString(EXTENSION_CLASS_DESCRIPTOR, "preFetchLithoText")
        hookTextComponent(EXTENSION_CLASS_DESCRIPTOR)
    }
}

