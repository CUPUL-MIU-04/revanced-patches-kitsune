package app.kitsune.patches.music.misc.share

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.COMPONENTS_PATH
import app.kitsune.patches.music.utils.extension.Constants.MISC_PATH
import app.kitsune.patches.music.utils.patch.PatchList.CHANGE_SHARE_SHEET
import app.kitsune.patches.music.utils.resourceid.bottomSheetRecyclerView
import app.kitsune.patches.music.utils.resourceid.sharedResourceIdPatch
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.litho.addLithoFilter
import app.kitsune.patches.shared.litho.lithoFilterPatch
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstLiteralInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$MISC_PATH/ShareSheetPatch;"

private const val FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/ShareSheetMenuFilter;"

@Suppress("unused")
val shareSheetPatch = bytecodePatch(
    CHANGE_SHARE_SHEET.title,
    CHANGE_SHARE_SHEET.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        lithoFilterPatch,
        sharedResourceIdPatch
    )

    execute {
        bottomSheetRecyclerViewFingerprint.methodOrThrow().apply {
            val constIndex = indexOfFirstLiteralInstructionOrThrow(bottomSheetRecyclerView)
            val targetIndex = indexOfFirstInstructionOrThrow(constIndex, Opcode.CHECK_CAST)
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            addInstruction(
                targetIndex + 1,
                "invoke-static {v$targetRegister}, $EXTENSION_CLASS_DESCRIPTOR->onShareSheetMenuCreate(Landroid/support/v7/widget/RecyclerView;)V"
            )
        }

        addLithoFilter(FILTER_CLASS_DESCRIPTOR)

        addSwitchPreference(
            CategoryType.MISC,
            "revanced_change_share_sheet",
            "false"
        )

        updatePatchStatus(CHANGE_SHARE_SHEET)

    }
}
