package app.kitsune.patches.music.utils.gms

import app.kitsune.patcher.patch.Option
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.compatibility.Constants.YOUTUBE_MUSIC_PACKAGE_NAME
import app.kitsune.patches.music.utils.extension.sharedExtensionPatch
import app.kitsune.patches.music.utils.fix.fileprovider.fileProviderPatch
import app.kitsune.patches.music.utils.mainactivity.mainActivityFingerprint
import app.kitsune.patches.music.utils.patch.PatchList.GMSCORE_SUPPORT
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePackageName
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.shared.gms.gmsCoreSupportPatch
import app.kitsune.patches.shared.spoof.useragent.baseSpoofUserAgentPatch
import app.kitsune.util.valueOrThrow

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = YOUTUBE_MUSIC_PACKAGE_NAME,
    mainActivityOnCreateFingerprint = mainActivityFingerprint.second,
    extensionPatch = sharedExtensionPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    compatibleWith(COMPATIBLE_PACKAGE)
}

private fun gmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
    packageNameYouTubeOption: Option<String>,
    packageNameYouTubeMusicOption: Option<String>,
) = app.kitsune.patches.shared.gms.gmsCoreSupportResourcePatch(
    fromPackageName = YOUTUBE_MUSIC_PACKAGE_NAME,
    spoofedPackageSignature = "afb0fed5eeaebdd86f56a97742f4b6b33ef59875",
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
    packageNameYouTubeOption = packageNameYouTubeOption,
    packageNameYouTubeMusicOption = packageNameYouTubeMusicOption,
    executeBlock = {
        updatePackageName(
            gmsCoreVendorGroupIdOption.valueOrThrow() + ".android.gms",
            packageNameYouTubeMusicOption.valueOrThrow()
        )

        updatePatchStatus(GMSCORE_SUPPORT)

    },
) {
    dependsOn(
        baseSpoofUserAgentPatch(YOUTUBE_MUSIC_PACKAGE_NAME),
        settingsPatch,
        fileProviderPatch(
            packageNameYouTubeOption.valueOrThrow(),
            packageNameYouTubeMusicOption.valueOrThrow()
        ),
    )
}
