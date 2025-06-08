package app.kitsune.patches.shared.opus

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.shared.extension.Constants.PATCHES_PATH
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$PATCHES_PATH/OpusCodecPatch;"

fun baseOpusCodecsPatch() = bytecodePatch(
    description = "baseOpusCodecsPatch"
) {
    execute {
        val opusCodecReference = with(codecReferenceFingerprint.methodOrThrow()) {
            val codecIndex = indexOfFirstInstructionOrThrow {
                opcode == Opcode.INVOKE_STATIC &&
                        getReference<MethodReference>()?.returnType == "Ljava/util/Set;"
            }
            getInstruction<ReferenceInstruction>(codecIndex).reference
        }

        codecSelectorFingerprint.matchOrThrow().let {
            it.method.apply {
                val freeRegister = implementation!!.registerCount - parameters.size - 2
                val targetIndex = it.patternMatch!!.endIndex
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructionsWithLabels(
                    targetIndex + 1, """
                        invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->enableOpusCodec()Z
                        move-result v$freeRegister
                        if-eqz v$freeRegister, :mp4a
                        invoke-static {}, $opusCodecReference
                        move-result-object v$targetRegister
                        """, ExternalLabel("mp4a", getInstruction(targetIndex + 1))
                )
            }
        }
    }
}

