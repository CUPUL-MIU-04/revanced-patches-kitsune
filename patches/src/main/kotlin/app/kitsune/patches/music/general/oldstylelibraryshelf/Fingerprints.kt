package app.kitsune.patches.music.general.oldstylelibraryshelf

import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val browseIdFingerprint = legacyFingerprint(
    name = "browseIdFingerprint",
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L"),
    strings = listOf("FEmusic_offline"),
    literals = listOf(45358178L),
)

