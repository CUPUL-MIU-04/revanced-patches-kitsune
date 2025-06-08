package app.kitsune.patches.reddit.misc.openlink

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.proxy.mutableTypes.MutableMethod
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getWalkerMethod

lateinit var screenNavigatorMethod: MutableMethod

val screenNavigatorMethodResolverPatch = bytecodePatch(
    description = "screenNavigatorMethodResolverPatch"
) {
    execute {
        screenNavigatorMethod =
                // ~ Reddit 2024.25.3
            screenNavigatorFingerprint.second.methodOrNull
                    // Reddit 2024.26.1 ~
                ?: with(customReportsFingerprint.methodOrThrow()) {
                    getWalkerMethod(indexOfScreenNavigatorInstruction(this))
                }
    }
}
