package app.kitsune.patches.shared.viewgroup

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.extension.Constants.EXTENSION_UTILS_CLASS_DESCRIPTOR
import app.kitsune.util.findMethodOrThrow
import app.kitsune.util.fingerprint.methodOrThrow

val viewGroupMarginLayoutParamsHookPatch = bytecodePatch(
    description = "viewGroupMarginLayoutParamsHookPatch"
) {
    execute {
        val setViewGroupMarginCall = with(
            viewGroupMarginFingerprint.methodOrThrow(viewGroupMarginParentFingerprint)
        ) {
            "$definingClass->$name(Landroid/view/View;II)V"
        }

        findMethodOrThrow(EXTENSION_UTILS_CLASS_DESCRIPTOR) {
            name == "hideViewGroupByMarginLayoutParams"
        }.addInstructions(
            0, """
                const/4 v0, 0x0
                invoke-static {p0, v0, v0}, $setViewGroupMarginCall
                """
        )
    }
}

