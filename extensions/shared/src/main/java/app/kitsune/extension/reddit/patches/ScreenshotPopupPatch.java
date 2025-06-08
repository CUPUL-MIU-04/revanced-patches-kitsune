package app.kitsune.extension.reddit.patches;

import app.kitsune.extension.reddit.settings.Settings;

@SuppressWarnings("unused")
public class ScreenshotPopupPatch {

    public static Boolean disableScreenshotPopup(Boolean original) {
        return Settings.DISABLE_SCREENSHOT_POPUP.get() ? Boolean.FALSE : original;
    }
}
