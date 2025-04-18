package app.revanced.extension.shared.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import app.revanced.extension.shared.innertube.client.YouTubeAppClient;
import app.revanced.extension.shared.innertube.client.YouTubeMusicAppClient;
import app.revanced.extension.shared.patches.ReturnYouTubeUsernamePatch.DisplayFormat;
import app.revanced.extension.shared.patches.WatchHistoryPatch.WatchHistoryType;
import app.revanced.extension.shared.patches.spoof.SpoofStreamingDataPatch.AudioStreamLanguageOverrideAvailability;

/**
 * Settings shared across multiple apps.
 * <p>
 * To ensure this class is loaded when the UI is created, app specific setting bundles should extend
 * or reference this class.
 */
public class BaseSettings {
    public static final BooleanSetting ENABLE_DEBUG_LOGGING = new BooleanSetting("revanced_enable_debug_logging", FALSE);
    /**
     * When enabled, share the debug logs with care.
     * The buffer contains select user data, including the client ip address and information that could identify the end user.
     */
    public static final BooleanSetting ENABLE_DEBUG_BUFFER_LOGGING = new BooleanSetting("revanced_enable_debug_buffer_logging", FALSE);
    public static final BooleanSetting SETTINGS_INITIALIZED = new BooleanSetting("revanced_settings_initialized", FALSE, false, false);

    public static final EnumSetting<AppLanguage> REVANCED_LANGUAGE = new EnumSetting<>("revanced_language", AppLanguage.DEFAULT, true);

    /**
     * These settings are used by YouTube Music.
     * Some patches are in a shared path, so they are declared here.
     */
    public static final BooleanSetting SPOOF_CLIENT = new BooleanSetting("revanced_spoof_client", FALSE, true);
    public static final EnumSetting<YouTubeMusicAppClient.ClientType> SPOOF_CLIENT_TYPE = new EnumSetting<>("revanced_spoof_client_type", YouTubeMusicAppClient.ClientType.IOS_MUSIC_6_21, true);

    /**
     * These settings are used by YouTube.
     * Some patches are in a shared path, so they are declared here.
     */
    public static final BooleanSetting SPOOF_STREAMING_DATA = new BooleanSetting("revanced_spoof_streaming_data", TRUE, true, "revanced_spoof_streaming_data_user_dialog_message");
    public static final EnumSetting<AppLanguage> SPOOF_STREAMING_DATA_LANGUAGE = new EnumSetting<>("revanced_spoof_streaming_data_language", AppLanguage.DEFAULT, new AudioStreamLanguageOverrideAvailability());
    public static final BooleanSetting SPOOF_STREAMING_DATA_IOS_FORCE_AVC = new BooleanSetting("revanced_spoof_streaming_data_ios_force_avc", FALSE, true,
            "revanced_spoof_streaming_data_ios_force_avc_user_dialog_message");
    public static final BooleanSetting SPOOF_STREAMING_DATA_SKIP_RESPONSE_ENCRYPTION = new BooleanSetting("revanced_spoof_streaming_data_skip_response_encryption", TRUE, true);
    public static final BooleanSetting SPOOF_STREAMING_DATA_STATS_FOR_NERDS = new BooleanSetting("revanced_spoof_streaming_data_stats_for_nerds", TRUE);
    public static final BooleanSetting SPOOF_STREAMING_DATA_TYPE_IOS = new BooleanSetting("revanced_spoof_streaming_data_type_ios", FALSE, true, "revanced_spoof_streaming_data_type_ios_user_dialog_message");
    // Client type must be last spoof setting due to cyclic references.
    public static final EnumSetting<YouTubeAppClient.ClientType> SPOOF_STREAMING_DATA_TYPE = new EnumSetting<>("revanced_spoof_streaming_data_type", YouTubeAppClient.ClientType.ANDROID_VR, true);

    /**
     * These settings are used by YouTube and YouTube Music.
     */
    public static final BooleanSetting HIDE_FULLSCREEN_ADS = new BooleanSetting("revanced_hide_fullscreen_ads", FALSE, true);
    public static final BooleanSetting HIDE_PROMOTION_ALERT_BANNER = new BooleanSetting("revanced_hide_promotion_alert_banner", TRUE);

    public static final BooleanSetting DISABLE_AUTO_CAPTIONS = new BooleanSetting("revanced_disable_auto_captions", FALSE, true);
    public static final BooleanSetting DISABLE_QUIC_PROTOCOL = new BooleanSetting("revanced_disable_quic_protocol", FALSE, true);
    public static final BooleanSetting ENABLE_OPUS_CODEC = new BooleanSetting("revanced_enable_opus_codec", FALSE, true);

    public static final BooleanSetting BYPASS_IMAGE_REGION_RESTRICTIONS = new BooleanSetting("revanced_bypass_image_region_restrictions", FALSE, true);
    public static final EnumSetting<WatchHistoryType> WATCH_HISTORY_TYPE = new EnumSetting<>("revanced_watch_history_type", WatchHistoryType.REPLACE);

    public static final BooleanSetting RETURN_YOUTUBE_USERNAME_ENABLED = new BooleanSetting("revanced_return_youtube_username_enabled", FALSE, true);
    public static final EnumSetting<DisplayFormat> RETURN_YOUTUBE_USERNAME_DISPLAY_FORMAT = new EnumSetting<>("revanced_return_youtube_username_display_format", DisplayFormat.USERNAME_ONLY, true);
    public static final StringSetting RETURN_YOUTUBE_USERNAME_YOUTUBE_DATA_API_V3_DEVELOPER_KEY = new StringSetting("revanced_return_youtube_username_youtube_data_api_v3_developer_key", "", true, false);

    /**
     * @noinspection DeprecatedIsStillUsed
     */
    @Deprecated
    // The official ReVanced does not offer this, so it has been removed from the settings only. Users can still access settings through import / export settings.
    public static final StringSetting BYPASS_IMAGE_REGION_RESTRICTIONS_DOMAIN = new StringSetting("revanced_bypass_image_region_restrictions_domain", "yt4.ggpht.com", true);

    public static final BooleanSetting SANITIZE_SHARING_LINKS = new BooleanSetting("revanced_sanitize_sharing_links", TRUE, true);
}
