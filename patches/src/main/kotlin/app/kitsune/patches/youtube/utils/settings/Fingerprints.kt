package app.kitsune.patches.youtube.utils.settings

import app.kitsune.patches.youtube.utils.resourceid.appearance
import app.kitsune.util.fingerprint.legacyFingerprint

internal val themeSetterSystemFingerprint = legacyFingerprint(
    name = "themeSetterSystemFingerprint",
    returnType = "L",
    literals = listOf(appearance),
)
