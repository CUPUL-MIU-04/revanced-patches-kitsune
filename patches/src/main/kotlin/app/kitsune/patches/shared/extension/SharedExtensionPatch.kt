package app.kitsune.patches.shared.extension

import app.kitsune.patcher.Fingerprint
import app.kitsune.patcher.FingerprintBuilder
import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.fingerprint
import app.kitsune.patcher.patch.BytecodePatchContext
import app.kitsune.patcher.patch.PatchException
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.extension.Constants.EXTENSION_UTILS_CLASS_DESCRIPTOR
import com.android.tools.smali.dexlib2.iface.Method

fun sharedExtensionPatch(
    vararg hooks: ExtensionHook,
) = bytecodePatch(
    description = "sharedExtensionPatch"
) {
    extendWith("extensions/shared.rve")

    execute {
        if (classes.none { EXTENSION_UTILS_CLASS_DESCRIPTOR == it.type }) {
            throw PatchException(
                "Shared extension has not been merged yet. This patch can not succeed without merging it.",
            )
        }
        hooks.forEach { hook -> hook(EXTENSION_UTILS_CLASS_DESCRIPTOR) }
    }
}

@Suppress("CONTEXT_RECEIVERS_DEPRECATED")
class ExtensionHook internal constructor(
    val fingerprint: Fingerprint,
    private val insertIndexResolver: ((Method) -> Int),
    private val contextRegisterResolver: (Method) -> String,
) {
    context(BytecodePatchContext)
    operator fun invoke(extensionClassDescriptor: String) {
        val insertIndex = insertIndexResolver(fingerprint.method)
        val contextRegister = contextRegisterResolver(fingerprint.method)

        fingerprint.method.addInstruction(
            insertIndex,
            "invoke-static/range { $contextRegister .. $contextRegister }, " +
                    "$extensionClassDescriptor->setContext(Landroid/content/Context;)V",
        )
    }
}

fun extensionHook(
    insertIndexResolver: ((Method) -> Int) = { 0 },
    contextRegisterResolver: (Method) -> String = { "p0" },
    fingerprintBuilderBlock: FingerprintBuilder.() -> Unit,
) = ExtensionHook(
    fingerprint(block = fingerprintBuilderBlock),
    insertIndexResolver,
    contextRegisterResolver
)
