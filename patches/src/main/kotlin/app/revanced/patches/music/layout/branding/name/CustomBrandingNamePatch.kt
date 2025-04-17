package app.revanced.patches.music.layout.branding.name

import app.revanced.patcher.patch.resourcePatch
import app.revanced.patcher.patch.stringOption
import app.revanced.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.music.utils.patch.PatchList.CUSTOM_BRANDING_NAME_FOR_YOUTUBE_MUSIC
import app.revanced.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.patches.music.utils.settings.settingsPatch
import app.revanced.util.removeStringsElements
import app.revanced.util.valueOrThrow

private const val APP_NAME_NOTIFICATION = "YTM KITSUNE"
private const val APP_NAME_LAUNCHER = "YTM KITSUNE"

@Suppress("unused")
val customBrandingNamePatch = resourcePatch(
    CUSTOM_BRANDING_NAME_FOR_YOUTUBE_MUSIC.title,
    CUSTOM_BRANDING_NAME_FOR_YOUTUBE_MUSIC.summary,
    false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    val appNameNotificationOption = stringOption(
        key = "appNameNotification",
        default = APP_NAME_LAUNCHER,
        values = mapOf(
            "YTM KITSUNE" to APP_NAME_NOTIFICATION,
            "YTM KITSUNE" to APP_NAME_LAUNCHER,
            "YouTube Music" to "YouTube Music",
            "YT Music" to "YT Music",
            "YTM RVX" to "YTM RVX",
            "YTM KITSUNE" to "YTM KITSUNE",
        ),
        title = "App name in notification panel",
        description = "The name of the app as it appears in the notification panel.",
        required = true
    )

    val appNameLauncherOption = stringOption(
        key = "appNameLauncher",
        default = APP_NAME_LAUNCHER,
        values = mapOf(
            "YTM KITSUNE" to APP_NAME_NOTIFICATION,
            "YTM KITSUNE" to APP_NAME_LAUNCHER,
            "YouTube Music" to "YouTube Music",
            "YT Music" to "YT Music",
            "YTM RVX" to "YTM RVX",
            "YTM KITSUNE" to "YTM KITSUNE",
        ),
        title = "App name in launcher",
        description = "The name of the app as it appears in the launcher.",
        required = true
    )

    execute {
        // Check patch options first.
        val notificationName = appNameNotificationOption
            .valueOrThrow()
        val launcherName = appNameLauncherOption
            .valueOrThrow()

        removeStringsElements(
            arrayOf("app_launcher_name", "app_name")
        )

        document("res/values/strings.xml").use { document ->
            mapOf(
                "app_name" to notificationName,
                "app_launcher_name" to launcherName
            ).forEach { (k, v) ->
                val stringElement = document.createElement("string")

                stringElement.setAttribute("name", k)
                stringElement.textContent = v

                document.getElementsByTagName("resources").item(0)
                    .appendChild(stringElement)
            }
        }

        updatePatchStatus(CUSTOM_BRANDING_NAME_FOR_YOUTUBE_MUSIC)

    }
}
