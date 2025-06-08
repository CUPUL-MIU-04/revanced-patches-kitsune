package app.kitsune.patches.youtube.general.snackbar

import app.kitsune.patches.youtube.utils.resourceid.insetElementsWrapper
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionReversed
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val BOTTOM_UI_CONTAINER_CLASS_DESCRIPTOR =
    "Lcom/google/android/apps/youtube/app/common/ui/bottomui/BottomUiContainer;"

internal val bottomUiContainerFingerprint = legacyFingerprint(
    name = "bottomUiContainerFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L", "L"),
    customFingerprint = { _, classDef ->
        classDef.type == BOTTOM_UI_CONTAINER_CLASS_DESCRIPTOR
    }
)

internal val bottomUiContainerPreFingerprint = legacyFingerprint(
    name = "bottomUiContainerPreFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L", "L", "L"),
    opcodes = listOf(
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.RETURN_VOID
    ),
    customFingerprint = { _, classDef ->
        classDef.type == BOTTOM_UI_CONTAINER_CLASS_DESCRIPTOR
    }
)

internal val bottomUiContainerThemeFingerprint = legacyFingerprint(
    name = "bottomUiContainerThemeFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf(BOTTOM_UI_CONTAINER_CLASS_DESCRIPTOR),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.SGET_OBJECT,
        Opcode.IF_NE,
        Opcode.CONST,
    ),
)

internal val lithoSnackBarFingerprint = legacyFingerprint(
    name = "lithoSnackBarFingerprint",
    returnType = "Landroid/view/View;",
    literals = listOf(insetElementsWrapper),
    customFingerprint = { method, _ ->
        indexOfBackGroundColor(method) >= 0
    }
)

internal fun indexOfBackGroundColor(method: Method) =
    method.indexOfFirstInstructionReversed {
        opcode == Opcode.INVOKE_VIRTUAL &&
                getReference<MethodReference>()?.name == "setBackgroundColor"
    }