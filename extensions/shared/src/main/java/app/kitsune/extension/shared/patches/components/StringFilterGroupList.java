package app.kitsune.extension.shared.patches.components;

import app.kitsune.extension.shared.utils.StringTrieSearch;

public final class StringFilterGroupList extends FilterGroupList<String, StringFilterGroup> {
    protected StringTrieSearch createSearchGraph() {
        return new StringTrieSearch();
    }
}
