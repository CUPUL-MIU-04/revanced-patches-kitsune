package app.kitsune.extension.youtube.patches.player;

import app.kitsune.extension.shared.utils.Logger;
import app.kitsune.extension.shared.utils.Utils;
import app.kitsune.extension.youtube.settings.Settings;
import app.kitsune.extension.youtube.shared.PlayerType;
import app.kitsune.extension.youtube.utils.VideoUtils;

@SuppressWarnings("unused")
public class ExitFullscreenPatch {

    public enum FullscreenMode {
        DISABLED,
        PORTRAIT,
        LANDSCAPE,
        PORTRAIT_LANDSCAPE,
    }

    /**
     * Injection point.
     */
    public static void endOfVideoReached() {
        try {
            FullscreenMode mode = Settings.EXIT_FULLSCREEN.get();
            if (mode == FullscreenMode.DISABLED) {
                return;
            }

            if (PlayerType.getCurrent() == PlayerType.WATCH_WHILE_FULLSCREEN) {
                if (mode != FullscreenMode.PORTRAIT_LANDSCAPE) {
                    if (Utils.isLandscapeOrientation()) {
                        if (mode == FullscreenMode.PORTRAIT) {
                            return;
                        }
                    } else if (mode == FullscreenMode.LANDSCAPE) {
                        return;
                    }
                }

                Utils.runOnMainThread(VideoUtils::exitFullscreenMode);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "endOfVideoReached failure", ex);
        }
    }
}
