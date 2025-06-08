package app.kitsune.patches.reddit.misc.tracking.url

import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val shareLinkFormatterFingerprint = legacyFingerprint(
    name = "shareLinkFormatterFingerprint",
    returnType = "Ljava/lang/String;",
    parameters = listOf("Ljava/lang/String;", "Ljava/util/Map;"),
    customFingerprint = { method, _ ->
        indexOfClearQueryInstruction(method) >= 0
    }
)

fun indexOfClearQueryInstruction(method: Method) =
    method.indexOfFirstInstruction {
        getReference<MethodReference>()?.toString() == "Landroid/net/Uri${'$'}Builder;->clearQuery()Landroid/net/Uri${'$'}Builder;"
    }
