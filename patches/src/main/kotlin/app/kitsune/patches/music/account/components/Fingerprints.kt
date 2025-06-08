package app.kitsune.patches.music.account.components

import app.kitsune.patches.music.utils.resourceid.accountSwitcherAccessibility
import app.kitsune.patches.music.utils.resourceid.channelHandle
import app.kitsune.patches.music.utils.resourceid.menuEntry
import app.kitsune.patches.music.utils.resourceid.namesInactiveAccountThumbnailSize
import app.kitsune.patches.music.utils.resourceid.tosFooter
import app.kitsune.util.fingerprint.legacyFingerprint

internal val accountSwitcherAccessibilityLabelFingerprint = legacyFingerprint(
    name = "accountSwitcherAccessibilityLabelFingerprint",
    returnType = "V",
    parameters = listOf("L", "Ljava/lang/Object;"),
    literals = listOf(accountSwitcherAccessibility)
)

internal val channelHandleFingerprint = legacyFingerprint(
    name = "channelHandleFingerprint",
    returnType = "V",
    literals = listOf(channelHandle),
)

internal val menuEntryFingerprint = legacyFingerprint(
    name = "menuEntryFingerprint",
    returnType = "V",
    literals = listOf(menuEntry)
)

internal val namesInactiveAccountThumbnailSizeFingerprint = legacyFingerprint(
    name = "namesInactiveAccountThumbnailSizeFingerprint",
    returnType = "V",
    parameters = listOf("L", "Ljava/lang/Object;"),
    literals = listOf(namesInactiveAccountThumbnailSize)
)

internal val termsOfServiceFingerprint = legacyFingerprint(
    name = "termsOfServiceFingerprint",
    returnType = "Landroid/view/View;",
    literals = listOf(tosFooter)
)
