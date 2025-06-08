package app.kitsune.patches.shared.dialog

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.proxy.mutableTypes.MutableMethod
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.getWalkerMethod
import app.kitsune.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

fun baseViewerDiscretionDialogPatch(
    classDescriptor: String,
    isAgeVerified: Boolean = false
) = bytecodePatch(
    description = "baseViewerDiscretionDialogPatch"
) {
    execute {
        createDialogFingerprint
            .methodOrThrow()
            .invoke(classDescriptor, "confirmDialog")

        if (isAgeVerified) {
            ageVerifiedFingerprint.matchOrThrow().let {
                it.getWalkerMethod(it.patternMatch!!.endIndex - 1)
                    .invoke(classDescriptor, "confirmDialogAgeVerified")
            }
        }
    }
}

private fun MutableMethod.invoke(classDescriptor: String, methodName: String) {
    val showDialogIndex = indexOfFirstInstructionOrThrow {
        getReference<MethodReference>()?.name == "show"
    }
    val dialogRegister = getInstruction<FiveRegisterInstruction>(showDialogIndex).registerC

    addInstruction(
        showDialogIndex + 1,
        "invoke-static { v$dialogRegister }, $classDescriptor->$methodName(Landroid/app/AlertDialog;)V"
    )
}

