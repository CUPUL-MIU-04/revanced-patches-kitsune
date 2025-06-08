package app.kitsune.extension.youtube.patches.utils;

import androidx.annotation.Nullable;

import app.kitsune.extension.youtube.shared.PlayerControlsVisibility;

@SuppressWarnings("unused")
public class PlayerControlsVisibilityHookPatch {
    /**
     * Injection point.
     */
    public static void setPlayerControlsVisibility(@Nullable Enum<?> youTubePlayerControlsVisibility) {
        if (youTubePlayerControlsVisibility == null) return;

        PlayerControlsVisibility.setFromString(youTubePlayerControlsVisibility.name());
    }
}

