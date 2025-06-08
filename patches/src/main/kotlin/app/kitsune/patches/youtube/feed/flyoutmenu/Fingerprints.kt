package app.kitsune.patches.youtube.feed.flyoutmenu

import app.kitsune.patches.youtube.utils.resourceid.posterArtWidthDefault
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val contextualMenuItemBuilderFingerprint = legacyFingerprint(
    name = "contextualMenuItemBuilderFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL or AccessFlags.SYNTHETIC,
    parameters = listOf("L", "L"),
    opcodes = listOf(
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.ADD_INT_2ADDR
    ),
    literals = listOf(posterArtWidthDefault),
)

