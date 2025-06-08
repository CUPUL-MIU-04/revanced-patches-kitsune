package app.kitsune.patches.youtube.utils.extension

import app.kitsune.patches.shared.extension.sharedExtensionPatch
import app.kitsune.patches.youtube.utils.extension.hooks.applicationInitHook

// TODO: Move this to a "Hook.kt" file. Same for other extension hook patches.
val sharedExtensionPatch = sharedExtensionPatch(
    applicationInitHook,
)
