package app.kitsune.patches.youtube.general.audiotracks

import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val streamingModelBuilderFingerprint = legacyFingerprint(
    name = "streamingModelBuilderFingerprint",
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf("vprng")
)

