package app.kitsune.patches.music.video.information

import app.kitsune.patches.music.utils.resourceid.qualityAuto
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val playerControllerSetTimeReferenceFingerprint = legacyFingerprint(
    name = "playerControllerSetTimeReferenceFingerprint",
    returnType = "V",
    opcodes = listOf(
        Opcode.INVOKE_DIRECT_RANGE,
        Opcode.IGET_OBJECT
    ),
    strings = listOf("Media progress reported outside media playback: ")
)

internal val videoEndFingerprint = legacyFingerprint(
    name = "videoEndFingerprint",
    strings = listOf("Attempting to seek during an ad")
)

internal val videoIdFingerprint = legacyFingerprint(
    name = "videoIdFingerprint",
    returnType = "V",
    parameters = listOf("L", "Ljava/lang/String;"),
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf("Null initialPlayabilityStatus")
)

internal val videoQualityListFingerprint = legacyFingerprint(
    name = "videoQualityListFingerprint",
    returnType = "V",
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.INVOKE_INTERFACE,
        Opcode.RETURN_VOID
    ),
    literals = listOf(qualityAuto)
)

internal val videoQualityTextFingerprint = legacyFingerprint(
    name = "videoQualityTextFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("[L", "I", "Z"),
    opcodes = listOf(
        Opcode.IF_EQZ,
        Opcode.IF_LTZ,
        Opcode.ARRAY_LENGTH,
        Opcode.IF_GE,
        Opcode.AGET_OBJECT,
        Opcode.IGET_OBJECT
    )
)