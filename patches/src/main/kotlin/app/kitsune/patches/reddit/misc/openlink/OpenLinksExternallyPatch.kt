package app.kitsune.patches.reddit.misc.openlink

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.reddit.utils.extension.Constants.PATCHES_PATH
import app.kitsune.patches.reddit.utils.patch.PatchList.OPEN_LINKS_EXTERNALLY
import app.kitsune.patches.reddit.utils.settings.settingsPatch
import app.kitsune.patches.reddit.utils.settings.updatePatchStatus
import app.kitsune.util.indexOfFirstStringInstructionOrThrow

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$PATCHES_PATH/OpenLinksExternallyPatch;" +
            "->" +
            "openLinksExternally(Landroid/app/Activity;Landroid/net/Uri;)Z"

@Suppress("unused")
val openLinksExternallyPatch = bytecodePatch(
    OPEN_LINKS_EXTERNALLY.title,
    OPEN_LINKS_EXTERNALLY.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        screenNavigatorMethodResolverPatch
    )

    execute {
        screenNavigatorMethod.apply {
            val insertIndex = indexOfFirstStringInstructionOrThrow("uri") + 2

            addInstructionsWithLabels(
                insertIndex, """
                    invoke-static {p1, p2}, $EXTENSION_METHOD_DESCRIPTOR
                    move-result v0
                    if-eqz v0, :dismiss
                    return-void
                    """, ExternalLabel("dismiss", getInstruction(insertIndex))
            )
        }

        updatePatchStatus(
            "enableOpenLinksExternally",
            OPEN_LINKS_EXTERNALLY
        )
    }
}
