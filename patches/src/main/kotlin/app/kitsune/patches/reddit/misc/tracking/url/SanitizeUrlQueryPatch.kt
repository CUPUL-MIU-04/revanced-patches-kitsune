package app.kitsune.patches.reddit.misc.tracking.url

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.reddit.utils.extension.Constants.PATCHES_PATH
import app.kitsune.patches.reddit.utils.patch.PatchList.SANITIZE_SHARING_LINKS
import app.kitsune.patches.reddit.utils.settings.settingsPatch
import app.kitsune.patches.reddit.utils.settings.updatePatchStatus
import app.kitsune.util.fingerprint.methodOrThrow

private const val SANITIZE_METHOD_DESCRIPTOR =
    "$PATCHES_PATH/SanitizeUrlQueryPatch;->stripQueryParameters()Z"

@Suppress("unused")
val sanitizeUrlQueryPatch = bytecodePatch(
    SANITIZE_SHARING_LINKS.title,
    SANITIZE_SHARING_LINKS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {
        shareLinkFormatterFingerprint.methodOrThrow().apply {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static {}, $SANITIZE_METHOD_DESCRIPTOR
                    move-result v0
                    if-eqz v0, :off
                    return-object p0
                    """, ExternalLabel("off", getInstruction(0))
            )
        }

        updatePatchStatus(
            "enableSanitizeUrlQuery",
            SANITIZE_SHARING_LINKS
        )
    }
}
