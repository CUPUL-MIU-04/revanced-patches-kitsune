package app.kitsune.extension.music.patches.components;

import app.kitsune.extension.music.settings.Settings;
import app.kitsune.extension.shared.patches.components.Filter;
import app.kitsune.extension.shared.patches.components.StringFilterGroup;

@SuppressWarnings("unused")
public final class PlayerFlyoutMenuFilter extends Filter {

    public PlayerFlyoutMenuFilter() {
        addIdentifierCallbacks(
                new StringFilterGroup(
                        Settings.HIDE_FLYOUT_MENU_3_COLUMN_COMPONENT,
                        "music_highlight_menu_item_carousel.eml",
                        "tile_button_carousel.eml"
                ),
                new StringFilterGroup(
                        Settings.HIDE_FLYOUT_MENU_DOWNLOAD,
                        "list_item.eml"
                )
        );
    }
}
