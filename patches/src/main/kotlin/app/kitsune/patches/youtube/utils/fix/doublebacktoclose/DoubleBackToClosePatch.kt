package app.kitsune.patches.youtube.utils.fix.doublebacktoclose

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.proxy.mutableTypes.MutableMethod
import app.kitsune.patches.shared.mainactivity.injectOnBackPressedMethodCall
import app.kitsune.patches.youtube.utils.extension.Constants.UTILS_PATH
import app.kitsune.patches.youtube.utils.scrollTopParentFingerprint
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.getWalkerMethod

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$UTILS_PATH/DoubleBackToClosePatch;"

val doubleBackToClosePatch = bytecodePatch(
    description = "doubleBackToClosePatch"
) {
    execute {
        fun MutableMethod.injectScrollView(
            index: Int,
            descriptor: String
        ) = addInstruction(
            index,
            "invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->$descriptor()V"
        )

        /**
         * Hook onBackPressed method inside MainActivity (WatchWhileActivity)
         */
        injectOnBackPressedMethodCall(
            EXTENSION_CLASS_DESCRIPTOR,
            "closeActivityOnBackPressed"
        )

        /**
         * Inject the methods which start of ScrollView
         */
        scrollPositionFingerprint.matchOrThrow().let {
            val walkerMethod =
                it.getWalkerMethod(it.patternMatch!!.startIndex + 1)
            val insertIndex = walkerMethod.implementation!!.instructions.size - 1 - 1

            walkerMethod.injectScrollView(insertIndex, "onStartScrollView")
        }

        /**
         * Inject the methods which stop of ScrollView
         */
        scrollTopFingerprint.matchOrThrow(scrollTopParentFingerprint).let {
            val insertIndex = it.patternMatch!!.endIndex

            it.method.injectScrollView(insertIndex, "onStopScrollView")
        }
    }
}
