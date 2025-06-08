package app.kitsune.patches.youtube.utils.fix.swiperefresh

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.util.fingerprint.matchOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

val swipeRefreshPatch = bytecodePatch(
    description = "swipeRefreshPatch"
) {
    execute {

        swipeRefreshLayoutFingerprint.matchOrThrow().let {
            it.method.apply {
                val insertIndex = it.patternMatch!!.endIndex
                val register = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex,
                    "const/4 v$register, 0x0"
                )
            }
        }

    }
}
