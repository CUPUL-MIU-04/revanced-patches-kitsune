package app.kitsune.patches.spotify.extended

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.fingerprint
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.shared.mapping.ResourceType.STRING
import app.kitsune.patches.shared.mapping.getResourceId
import app.kitsune.patches.shared.mapping.resourceMappingPatch
import app.kitsune.util.containsLiteralInstruction

internal val addCreateTabMethodFingerprint = fingerprint {
    returns("V")
    custom { method, _ ->
        method.containsLiteralInstruction(getResourceId(STRING, "bottom_navigation_bar_create_tab_title"))
    }
}

@Suppress("unused")
val removeCreateTabPatch = bytecodePatch(
    name = "Remove Create tab",
    description = "Removes the 'Create' (Plus) tab from the bottom navigation bar.",
) {
    compatibleWith("com.spotify.music")
    dependsOn(resourceMappingPatch)

    execute {
        addCreateTabMethodFingerprint.method.addInstruction(0, "return-void")
    }
}
