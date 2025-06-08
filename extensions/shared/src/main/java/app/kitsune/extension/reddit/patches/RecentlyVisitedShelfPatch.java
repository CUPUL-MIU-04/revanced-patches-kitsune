package app.kitsune.extension.reddit.patches;

import java.util.Collections;
import java.util.List;

import app.kitsune.extension.reddit.settings.Settings;

@SuppressWarnings("unused")
public final class RecentlyVisitedShelfPatch {

    public static List<?> hideRecentlyVisitedShelf(List<?> list) {
        return Settings.HIDE_RECENTLY_VISITED_SHELF.get() ? Collections.emptyList() : list;
    }
}
