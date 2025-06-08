package app.kitsune.patches.shared.customspeed

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.replaceInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstLiteralInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private var patchIncluded = false

fun customPlaybackSpeedPatch(
    descriptor: String,
    maxSpeed: Float
) = bytecodePatch(
    description = "customPlaybackSpeedPatch"
) {
    execute {
        if (patchIncluded) {
            return@execute
        }

        arrayGeneratorFingerprint.matchOrThrow().let {
            it.method.apply {
                val targetIndex = it.patternMatch!!.startIndex
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1, """
                        invoke-static {v$targetRegister}, $descriptor->getLength(I)I
                        move-result v$targetRegister
                        """
                )

                val sizeIndex = indexOfFirstInstructionOrThrow {
                    getReference<MethodReference>()?.name == "size"
                } + 1
                val sizeRegister = getInstruction<OneRegisterInstruction>(sizeIndex).registerA

                addInstructions(
                    sizeIndex + 1, """
                        invoke-static {v$sizeRegister}, $descriptor->getSize(I)I
                        move-result v$sizeRegister
                        """
                )

                val arrayIndex = indexOfFirstInstructionOrThrow {
                    getReference<FieldReference>()?.type == "[F"
                }
                val arrayRegister = getInstruction<OneRegisterInstruction>(arrayIndex).registerA

                addInstructions(
                    arrayIndex + 1, """
                        invoke-static {v$arrayRegister}, $descriptor->getArray([F)[F
                        move-result-object v$arrayRegister
                        """
                )
            }
        }

        setOf(
            limiterFallBackFingerprint.methodOrThrow(),
            limiterFingerprint.methodOrThrow(limiterFallBackFingerprint)
        ).forEach { method ->
            method.apply {
                val limitMinIndex =
                    indexOfFirstLiteralInstructionOrThrow(0.25f.toRawBits().toLong())
                val limitMaxIndex =
                    indexOfFirstInstructionOrThrow(limitMinIndex + 1, Opcode.CONST_HIGH16)

                val limitMinRegister =
                    getInstruction<OneRegisterInstruction>(limitMinIndex).registerA
                val limitMaxRegister =
                    getInstruction<OneRegisterInstruction>(limitMaxIndex).registerA

                replaceInstruction(
                    limitMinIndex,
                    "const/high16 v$limitMinRegister, 0x0"
                )
                replaceInstruction(
                    limitMaxIndex,
                    "const/high16 v$limitMaxRegister, ${maxSpeed.toRawBits()}"
                )
            }
        }

        patchIncluded = true

    }
}

