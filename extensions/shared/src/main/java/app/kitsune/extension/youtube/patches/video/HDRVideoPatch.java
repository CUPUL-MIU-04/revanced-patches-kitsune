package app.kitsune.extension.youtube.patches.video;

import app.kitsune.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class HDRVideoPatch {

    public static boolean disableHDRVideo() {
        return !Settings.DISABLE_HDR_VIDEO.get();
    }
}
