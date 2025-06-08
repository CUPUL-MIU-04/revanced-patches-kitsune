package app.kitsune.patches.spotify.misc.extension

import app.kitsune.patches.shared.extension.extensionHook

internal val spotifyMainActivityOnCreate = extensionHook {
    custom { method, classDef ->
        classDef.type == "Lcom/spotify/music/SpotifyMainActivity;" &&
                method.name == "onCreate"
    }
}
