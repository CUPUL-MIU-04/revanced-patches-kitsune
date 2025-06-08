package app.kitsune.patches.reddit.layout.communities

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.reddit.utils.extension.Constants.PATCHES_PATH
import app.kitsune.patches.reddit.utils.patch.PatchList.HIDE_RECOMMENDED_COMMUNITIES_SHELF
import app.kitsune.patches.reddit.utils.settings.settingsPatch
import app.kitsune.patches.reddit.utils.settings.updatePatchStatus
import app.kitsune.util.fingerprint.methodOrThrow

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$PATCHES_PATH/RecommendedCommunitiesPatch;->hideRecommendedCommunitiesShelf()Z"

@Suppress("unused")
val recommendedCommunitiesPatch = bytecodePatch(
    HIDE_RECOMMENDED_COMMUNITIES_SHELF.title,
    HIDE_RECOMMENDED_COMMUNITIES_SHELF.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {
        communityRecommendationSectionFingerprint.methodOrThrow(
            communityRecommendationSectionParentFingerprint
        ).apply {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static {}, $EXTENSION_METHOD_DESCRIPTOR
                    move-result v0
                    if-eqz v0, :off
                    return-void
                    """, ExternalLabel("off", getInstruction(0))
            )
        }

        updatePatchStatus(
            "enableRecommendedCommunitiesShelf",
            HIDE_RECOMMENDED_COMMUNITIES_SHELF
        )
    }
}
