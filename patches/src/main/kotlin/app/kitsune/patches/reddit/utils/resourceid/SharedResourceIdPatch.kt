package app.kitsune.patches.reddit.utils.resourceid

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.shared.mapping.ResourceType.STRING
import app.kitsune.patches.shared.mapping.getResourceId
import app.kitsune.patches.shared.mapping.resourceMappingPatch

var nsfwDialogTitle = -1L
    private set

internal val sharedResourceIdPatch = resourcePatch(
    description = "sharedResourceIdPatch"
) {
    dependsOn(resourceMappingPatch)

    execute {
        nsfwDialogTitle = getResourceId(STRING, "nsfw_dialog_title")
    }
}