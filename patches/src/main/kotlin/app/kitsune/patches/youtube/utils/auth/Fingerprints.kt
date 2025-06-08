package app.kitsune.patches.youtube.utils.auth

import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val accountIdentityFingerprint = legacyFingerprint(
    name = "accountIdentityFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    customFingerprint = { method, _ ->
        method.definingClass.endsWith("${'$'}AutoValue_AccountIdentity;")
    }
)
