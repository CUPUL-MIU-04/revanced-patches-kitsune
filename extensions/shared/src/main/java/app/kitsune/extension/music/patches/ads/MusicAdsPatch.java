package app.kitsune.extension.music.patches.ads;

import app.kitsune.extension.music.settings.Settings;

@SuppressWarnings("unused")
public class MusicAdsPatch {

    public static boolean hideMusicAds() {
        return !Settings.HIDE_MUSIC_ADS.get();
    }

    public static boolean hideMusicAds(boolean original) {
        return !Settings.HIDE_MUSIC_ADS.get() && original;
    }
}
