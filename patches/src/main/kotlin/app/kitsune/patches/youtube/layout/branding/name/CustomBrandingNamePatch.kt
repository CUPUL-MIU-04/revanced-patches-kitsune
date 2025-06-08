package app.kitsune.patches.youtube.layout.branding.name

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patcher.patch.stringOption
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.patch.PatchList.CUSTOM_BRANDING_NAME_FOR_YOUTUBE
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.removeStringsElements
import app.kitsune.util.valueOrThrow

private const val APP_NAME = "YT"

@Suppress("unused")
val customBrandingNamePatch = resourcePatch(
    CUSTOM_BRANDING_NAME_FOR_YOUTUBE.title,
    CUSTOM_BRANDING_NAME_FOR_YOUTUBE.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    val appNameOption = stringOption(
        key = "appName",
        default = APP_NAME,
        values = mapOf(
            "ReVanced Extended" to "ReVanced Extended",
            "RVX" to "RVX",
            "YouTube RVX" to "YouTube RVX",
            "YouTube" to "YouTube",
            "YT RVX" to "YT RVX",
            "YT KITSUNE" to "YT KITSUNE",
            "YT" to APP_NAME,
        ),
        title = "App name",
        description = "The name of the app.",
        required = true,
    )

    execute {
        // Check patch options first.
        val appName = appNameOption
            .valueOrThrow()

        removeStringsElements(
            arrayOf("application_name")
        )

        document("res/values/strings.xml").use { document ->
            val stringElement = document.createElement("string")

            stringElement.setAttribute("name", "application_name")
            stringElement.textContent = appName

            document.getElementsByTagName("resources").item(0)
                .appendChild(stringElement)
        }

    }
}
