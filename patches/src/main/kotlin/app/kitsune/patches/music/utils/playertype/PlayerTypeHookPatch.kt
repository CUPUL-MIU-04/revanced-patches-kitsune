package app.kitsune.patches.music.utils.playertype

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.extension.Constants.UTILS_PATH
import app.kitsune.util.fingerprint.methodOrThrow

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$UTILS_PATH/PlayerTypeHookPatch;"

@Suppress("unused")
val playerTypeHookPatch = bytecodePatch(
    description = "playerTypeHookPatch"
) {

    execute {

        playerTypeFingerprint.methodOrThrow().addInstruction(
            0,
            "invoke-static {p1}, $EXTENSION_CLASS_DESCRIPTOR->setPlayerType(Ljava/lang/Enum;)V"
        )

    }
}
