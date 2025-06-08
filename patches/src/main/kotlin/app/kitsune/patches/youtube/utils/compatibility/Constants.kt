package app.kitsune.patches.youtube.utils.compatibility

import app.kitsune.patcher.patch.PackageName
import app.kitsune.patcher.patch.VersionName

internal object Constants {
    internal const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube"

    val COMPATIBLE_PACKAGE: Pair<PackageName, Set<VersionName>?> = Pair(
        YOUTUBE_PACKAGE_NAME,
        setOf(
            "19.05.36", // This is the last version with the least YouTube experimental flag.
            "19.16.39", // This is the last version where the 'Restore old seekbar thumbnails' setting works.
            "19.43.41", // This is the latest version where edge-to-edge display is not enforced on Android 15+.
            "19.44.39", // This is the only version that has experimental shortcut icons.
            "19.47.53", // This is the latest version supported by the RVX patch.
            "19.50.41",
            "20.04.40",
            "20.06.38",
            "20.08.38",
            "20.09.41",
            "20.13.36",
            "20.14.41",
            "20.18.34",
            "20.19.37",
        )
    )
}