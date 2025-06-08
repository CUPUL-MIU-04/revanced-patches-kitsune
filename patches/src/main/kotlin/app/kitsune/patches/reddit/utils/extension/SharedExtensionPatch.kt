package app.kitsune.patches.reddit.utils.extension

import app.kitsune.patches.reddit.utils.extension.hooks.applicationInitHook
import app.kitsune.patches.shared.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(applicationInitHook)
