package app.kitsune.patches.music.utils.playertype

import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val playerTypeFingerprint = legacyFingerprint(
    name = "playerTypeFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IGET_BOOLEAN,
        Opcode.IF_NEZ,
        Opcode.IPUT_OBJECT,
        Opcode.RETURN_VOID
    ),
    customFingerprint = { method, _ ->
        method.definingClass.endsWith("/MppWatchWhileLayout;")
    }
)
