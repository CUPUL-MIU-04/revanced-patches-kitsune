package app.kitsune.patches.spotify.navbar

import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.spotify.misc.unlockPremiumPatch

@Deprecated("Superseded by unlockPremiumPatch", ReplaceWith("unlockPremiumPatch"))
@Suppress("unused")
val premiumNavbarTabPatch = bytecodePatch(
    description = "Hides the premium tab from the navigation bar.",
) {
    dependsOn(unlockPremiumPatch)
}
