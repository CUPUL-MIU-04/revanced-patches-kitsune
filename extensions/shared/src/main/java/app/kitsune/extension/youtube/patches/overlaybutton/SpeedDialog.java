package app.kitsune.extension.youtube.patches.overlaybutton;

import static app.kitsune.extension.shared.utils.StringRef.str;
import static app.kitsune.extension.shared.utils.Utils.showToastShort;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import app.kitsune.extension.shared.utils.Logger;
import app.kitsune.extension.youtube.settings.Settings;
import app.kitsune.extension.youtube.shared.VideoInformation;
import app.kitsune.extension.youtube.utils.VideoUtils;

@SuppressWarnings("unused")
public class SpeedDialog extends BottomControlButton {
    @Nullable
    private static SpeedDialog instance;

    public SpeedDialog(ViewGroup bottomControlsViewGroup) {
        super(
                bottomControlsViewGroup,
                "speed_dialog_button",
                Settings.OVERLAY_BUTTON_SPEED_DIALOG,
                view -> VideoUtils.showPlaybackSpeedDialog(view.getContext()),
                view -> {
                    if (!Settings.REMEMBER_PLAYBACK_SPEED_LAST_SELECTED.get() ||
                            VideoInformation.getPlaybackSpeed() == Settings.DEFAULT_PLAYBACK_SPEED.get()) {
                        VideoInformation.overridePlaybackSpeed(1.0f);
                        showToastShort(str("revanced_overlay_button_speed_dialog_reset", "1.0"));
                    } else {
                        float defaultSpeed = Settings.DEFAULT_PLAYBACK_SPEED.get();
                        VideoInformation.overridePlaybackSpeed(defaultSpeed);
                        showToastShort(str("revanced_overlay_button_speed_dialog_reset", defaultSpeed));
                    }

                    return true;
                }
        );
    }

    /**
     * Injection point.
     */
    public static void initialize(View bottomControlsViewGroup) {
        try {
            if (bottomControlsViewGroup instanceof ViewGroup viewGroup) {
                instance = new SpeedDialog(viewGroup);
            }
        } catch (Exception ex) {
            Logger.printException(() -> "initialize failure", ex);
        }
    }

    /**
     * Injection point.
     */
    public static void changeVisibility(boolean showing, boolean animation) {
        if (instance != null) instance.setVisibility(showing, animation);
    }

    public static void changeVisibilityNegatedImmediate() {
        if (instance != null) instance.setVisibilityNegatedImmediate();
    }


}