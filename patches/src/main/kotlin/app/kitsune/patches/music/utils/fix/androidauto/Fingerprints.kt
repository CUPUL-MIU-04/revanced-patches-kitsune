package app.kitsune.patches.music.utils.fix.androidauto

import app.kitsune.util.fingerprint.legacyFingerprint

internal val certificateCheckFingerprint = legacyFingerprint(
    name = "certificateCheckFingerprint",
    returnType = "Z",
    parameters = listOf("L"),
    strings = listOf("X509")
)