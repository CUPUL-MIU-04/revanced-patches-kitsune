package app.kitsune.patches.youtube.layout.theme

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.shared.materialyou.baseMaterialYou
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.patch.PatchList.MATERIALYOU
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.updatePatchStatusTheme
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.copyXmlNode

@Suppress("unused")
val materialYouPatch = resourcePatch(
    MATERIALYOU.title,
    MATERIALYOU.summary,
    false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        sharedThemePatch,
        settingsPatch,
    )

    execute {
        baseMaterialYou()

        copyXmlNode("youtube/materialyou/host", "values-v31/colors.xml", "resources")

        updatePatchStatusTheme("MaterialYou")

        addPreference(MATERIALYOU)

    }
}
