package app.kitsune.patches.shared.drawable

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.proxy.mutableTypes.MutableMethod
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private lateinit var insertMethod: MutableMethod
private var insertIndex: Int = 0
private var insertRegister: Int = 0
private var offset = 0

val drawableColorHookPatch = bytecodePatch(
    description = "drawableColorHookPatch"
) {
    execute {
        drawableColorFingerprint.methodOrThrow().apply {
            insertMethod = this
            insertIndex = indexOfFirstInstructionReversedOrThrow {
                getReference<MethodReference>()?.name == "setColor"
            }
            insertRegister = getInstruction<FiveRegisterInstruction>(insertIndex).registerD
        }
    }
}

internal fun addDrawableColorHook(
    methodDescriptor: String,
    highPriority: Boolean = false
) {
    insertMethod.addInstructions(
        if (highPriority) insertIndex else insertIndex + offset,
        """
            invoke-static {v$insertRegister}, $methodDescriptor
            move-result v$insertRegister
            """
    )
    offset += 2
}

