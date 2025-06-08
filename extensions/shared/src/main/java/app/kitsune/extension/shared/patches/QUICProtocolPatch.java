package app.kitsune.extension.shared.patches;

import app.kitsune.extension.shared.settings.BaseSettings;

@SuppressWarnings("unused")
public class QUICProtocolPatch {

    public static boolean disableQUICProtocol(boolean original) {
        return !BaseSettings.DISABLE_QUIC_PROTOCOL.get() && original;
    }
}
