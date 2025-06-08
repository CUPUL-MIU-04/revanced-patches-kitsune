package app.kitsune.patches.youtube.misc.share

import app.kitsune.patches.youtube.utils.resourceid.bottomSheetRecyclerView
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstruction
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

internal val bottomSheetRecyclerViewFingerprint = legacyFingerprint(
    name = "bottomSheetRecyclerViewFingerprint",
    returnType = "Lj${'$'}/util/Optional;",
    accessFlags = AccessFlags.PROTECTED or AccessFlags.FINAL,
    parameters = emptyList(),
    literals = listOf(bottomSheetRecyclerView),
)

internal val updateShareSheetCommandFingerprint = legacyFingerprint(
    name = "updateShareSheetCommandFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L", "Ljava/util/Map;"),
    opcodes = listOf(
        Opcode.IF_EQZ,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.IGET_OBJECT
    ),
    customFingerprint = { method, _ ->
        method.indexOfFirstInstruction {
            opcode == Opcode.SGET_OBJECT &&
                    getReference<FieldReference>()?.name == "updateShareSheetCommand"
        } >= 0
    }
)
