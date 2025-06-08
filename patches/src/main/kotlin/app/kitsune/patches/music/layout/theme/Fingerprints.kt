package app.kitsune.patches.music.layout.theme

import app.kitsune.patches.music.utils.resourceid.elementsContainer
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val elementsContainerFingerprint = legacyFingerprint(
    name = "elementsContainerFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    opcodes = listOf(Opcode.INVOKE_DIRECT_RANGE),
    literals = listOf(elementsContainer)
)
