package app.kitsune.patches.shared.spoof.appversion

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.createPlayerRequestBodyWithModelFingerprint
import app.kitsune.patches.shared.indexOfReleaseInstruction
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

fun baseSpoofAppVersionPatch(
    descriptor: String,
) = bytecodePatch(
    description = "baseSpoofAppVersionPatch"
) {
    execute {
        createPlayerRequestBodyWithModelFingerprint.methodOrThrow().apply {
            val versionIndex = indexOfReleaseInstruction(this) + 1
            val insertIndex =
                indexOfFirstInstructionReversedOrThrow(versionIndex, Opcode.IPUT_OBJECT)
            val insertRegister = getInstruction<TwoRegisterInstruction>(insertIndex).registerA

            addInstructions(
                insertIndex, """
                    invoke-static {v$insertRegister}, $descriptor
                    move-result-object v$insertRegister
                    """
            )
        }
    }
}
