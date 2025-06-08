package app.kitsune.patches.reddit.layout.toolbar

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.reddit.utils.extension.Constants.PATCHES_PATH
import app.kitsune.patches.reddit.utils.patch.PatchList.HIDE_TOOLBAR_BUTTON
import app.kitsune.patches.reddit.utils.settings.settingsPatch
import app.kitsune.patches.reddit.utils.settings.updatePatchStatus
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$PATCHES_PATH/ToolBarButtonPatch;->hideToolBarButton(Landroid/view/View;)V"

@Suppress("unused")
@Deprecated("This patch is deprecated until Reddit adds a button like r/place or Reddit recap button to the toolbar.")
val toolBarButtonPatch = bytecodePatch {
    // compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {
        homePagerScreenFingerprint.matchOrThrow().let {
            it.method.apply {
                val stringIndex = it.stringMatches!!.first().index
                val insertIndex = indexOfFirstInstructionOrThrow(stringIndex, Opcode.CHECK_CAST)
                val insertRegister =
                    getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static {v$insertRegister}, $EXTENSION_METHOD_DESCRIPTOR"
                )
            }
        }

        updatePatchStatus(
            "enableToolBarButton",
            HIDE_TOOLBAR_BUTTON
        )
    }
}
