package app.kitsune.patches.youtube.utils.bottomsheet

import app.kitsune.patches.youtube.utils.resourceid.designBottomSheet
import app.kitsune.util.fingerprint.legacyFingerprint

internal val bottomSheetBehaviorFingerprint = legacyFingerprint(
    name = "bottomSheetBehaviorFingerprint",
    returnType = "V",
    literals = listOf(designBottomSheet),
)
