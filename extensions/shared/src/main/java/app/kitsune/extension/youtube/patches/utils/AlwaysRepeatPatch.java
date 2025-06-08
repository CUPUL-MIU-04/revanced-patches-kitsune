package app.kitsune.extension.youtube.patches.utils;

import static app.kitsune.extension.youtube.utils.VideoUtils.pauseMedia;

import app.kitsune.extension.shared.utils.Utils;
import app.kitsune.extension.youtube.settings.Settings;
import app.kitsune.extension.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
public class AlwaysRepeatPatch extends Utils {

    /**
     * Injection point.
     *
     * @return video is repeated.
     */
    public static boolean alwaysRepeat() {
        return alwaysRepeatEnabled() && VideoInformation.overrideVideoTime(0);
    }

    public static boolean alwaysRepeatEnabled() {
        final boolean alwaysRepeat = Settings.ALWAYS_REPEAT.get();
        final boolean alwaysRepeatPause = Settings.ALWAYS_REPEAT_PAUSE.get();

        if (alwaysRepeat && alwaysRepeatPause) pauseMedia();
        return alwaysRepeat;
    }

}
