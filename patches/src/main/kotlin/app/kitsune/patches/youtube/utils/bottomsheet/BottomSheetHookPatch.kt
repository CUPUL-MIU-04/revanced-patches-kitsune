package app.kitsune.patches.youtube.utils.bottomsheet

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.extension.Constants.UTILS_PATH
import app.kitsune.util.findMethodOrThrow
import app.kitsune.util.fingerprint.definingClassOrThrow

private const val EXTENSION_BOTTOM_SHEET_HOOK_CLASS_DESCRIPTOR =
    "$UTILS_PATH/BottomSheetHookPatch;"

val bottomSheetHookPatch = bytecodePatch(
    description = "bottomSheetHookPatch"
) {
    execute {
        val bottomSheetClass =
            bottomSheetBehaviorFingerprint.definingClassOrThrow()

        arrayOf(
            "onAttachedToWindow",
            "onDetachedFromWindow"
        ).forEach { methodName ->
            findMethodOrThrow(bottomSheetClass) {
                name == methodName
            }.addInstruction(
                1,
                "invoke-static {}, $EXTENSION_BOTTOM_SHEET_HOOK_CLASS_DESCRIPTOR->$methodName()V"
            )
        }
    }
}
