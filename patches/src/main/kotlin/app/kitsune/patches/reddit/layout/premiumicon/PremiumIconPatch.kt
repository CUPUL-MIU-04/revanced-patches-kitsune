package app.kitsune.patches.reddit.layout.premiumicon

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.reddit.utils.patch.PatchList.PREMIUM_ICON
import app.kitsune.patches.reddit.utils.settings.updatePatchStatus
import app.kitsune.util.fingerprint.methodOrThrow

@Suppress("unused")
val premiumIconPatch = bytecodePatch(
    PREMIUM_ICON.title,
    PREMIUM_ICON.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    execute {
        premiumIconFingerprint.methodOrThrow().addInstructions(
            0, """
                const/4 v0, 0x1
                return v0
                """
        )

        updatePatchStatus(PREMIUM_ICON)
    }
}
