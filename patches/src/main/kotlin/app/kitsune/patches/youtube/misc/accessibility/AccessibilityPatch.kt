package app.kitsune.patches.youtube.misc.accessibility

import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.patch.PatchList.HIDE_ACCESSIBILITY_CONTROLS_DIALOG
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.findMethodOrThrow
import app.kitsune.util.fingerprint.mutableClassOrThrow
import app.kitsune.util.indexOfFirstInstruction
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import app.kitsune.util.or
import app.kitsune.util.returnEarly
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

@Suppress("unused")
val accessibilityPatch = bytecodePatch(
    HIDE_ACCESSIBILITY_CONTROLS_DIALOG.title,
    HIDE_ACCESSIBILITY_CONTROLS_DIALOG.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {

        playerAccessibilitySettingsEduControllerParentFingerprint
            .mutableClassOrThrow()
            .methods
            .first { method -> method.name == "<init>" }
            .apply {
                val lifecycleObserverIndex =
                    indexOfFirstInstructionReversedOrThrow(Opcode.NEW_INSTANCE)
                val lifecycleObserverClass =
                    getInstruction<ReferenceInstruction>(lifecycleObserverIndex).reference.toString()

                findMethodOrThrow(lifecycleObserverClass) {
                    accessFlags == AccessFlags.PUBLIC or AccessFlags.FINAL &&
                            parameterTypes.size == 1 &&
                            indexOfFirstInstruction(Opcode.INVOKE_DIRECT) >= 0
                }.returnEarly()
            }

        addPreference(HIDE_ACCESSIBILITY_CONTROLS_DIALOG)

    }
}
