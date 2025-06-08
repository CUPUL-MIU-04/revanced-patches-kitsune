package app.kitsune.patches.youtube.utils.auth

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.extension.Constants.EXTENSION_PATH
import app.kitsune.patches.youtube.utils.extension.sharedExtensionPatch
import app.kitsune.patches.youtube.utils.request.buildRequestPatch
import app.kitsune.patches.youtube.utils.request.hookBuildRequest
import app.kitsune.util.fingerprint.methodOrThrow

private const val EXTENSION_AUTH_UTILS_CLASS_DESCRIPTOR =
    "$EXTENSION_PATH/utils/AuthUtils;"

val authHookPatch = bytecodePatch(
    description = "authHookPatch"
) {
    dependsOn(
        sharedExtensionPatch,
        buildRequestPatch,
    )

    execute {
        // Get incognito status and data sync id.
        accountIdentityFingerprint.methodOrThrow().addInstructions(
            1, """
                sput-object p3, $EXTENSION_AUTH_UTILS_CLASS_DESCRIPTOR->dataSyncId:Ljava/lang/String;
                sput-boolean p4, $EXTENSION_AUTH_UTILS_CLASS_DESCRIPTOR->isIncognito:Z
                """
        )

        // Get the header to use the auth token.
        hookBuildRequest("$EXTENSION_AUTH_UTILS_CLASS_DESCRIPTOR->setRequestHeaders(Ljava/lang/String;Ljava/util/Map;)V")
    }
}