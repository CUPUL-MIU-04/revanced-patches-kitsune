package app.kitsune.patches.spotify.misc.fix

import app.kitsune.patcher.fingerprint

internal val getAppSignatureFingerprint = fingerprint { strings("Failed to get the application signatures") }
