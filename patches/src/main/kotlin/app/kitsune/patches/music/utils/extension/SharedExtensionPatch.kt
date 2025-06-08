package app.kitsune.patches.music.utils.extension

import app.kitsune.patches.music.utils.extension.hooks.applicationInitHook
import app.kitsune.patches.shared.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(
    applicationInitHook,
)
