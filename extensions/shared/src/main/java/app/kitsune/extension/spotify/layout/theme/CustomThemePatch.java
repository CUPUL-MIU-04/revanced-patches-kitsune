package app.kitsune.extension.spotify.layout.theme;

import android.graphics.Color;
import app.kitsune.extension.shared.utils.Logger;
import app.kitsune.extension.shared.utils.Utils;

@SuppressWarnings("unused")
public final class CustomThemePatch {

    /**
     * Injection point.
     */
    public static long getThemeColor(String colorString) {
        try {
            return Utils.getColorFromString(colorString);
        } catch (Exception ex) {
            Logger.printException(() -> "Invalid custom color: " + colorString, ex);
            return Color.BLACK;
        }
    }
}
