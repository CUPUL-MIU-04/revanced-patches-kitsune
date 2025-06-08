package app.kitsune.patches.music.misc.codecs

import app.kitsune.patcher.patch.resourcePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.patch.PatchList.ENABLE_OPUS_CODEC
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.opus.baseOpusCodecsPatch

@Suppress("unused")
val opusCodecPatch = resourcePatch(
    ENABLE_OPUS_CODEC.title,
    ENABLE_OPUS_CODEC.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        baseOpusCodecsPatch(),
    )

    execute {
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_enable_opus_codec",
            "false"
        )
        updatePatchStatus(ENABLE_OPUS_CODEC)
    }
}
