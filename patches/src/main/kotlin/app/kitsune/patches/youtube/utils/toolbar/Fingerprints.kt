package app.kitsune.patches.youtube.utils.toolbar

import app.kitsune.patches.youtube.utils.extension.Constants.UTILS_PATH
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val toolBarPatchFingerprint = legacyFingerprint(
    name = "toolBarPatchFingerprint",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.STATIC,
    customFingerprint = { method, _ ->
        method.definingClass == "$UTILS_PATH/ToolBarPatch;"
                && method.name == "hookToolBar"
    }
)


