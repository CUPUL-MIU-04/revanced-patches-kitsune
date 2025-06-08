package app.kitsune.patches.music.utils.fix.parameter

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.music.utils.extension.Constants.MISC_PATH
import app.kitsune.patches.music.utils.patch.PatchList.SPOOF_PLAYER_PARAMETER
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.patches.music.video.information.videoInformationPatch
import app.kitsune.patches.music.video.playerresponse.Hook
import app.kitsune.patches.music.video.playerresponse.addPlayerResponseMethodHook
import app.kitsune.patches.music.video.playerresponse.playerResponseMethodHookPatch
import app.kitsune.util.fingerprint.injectLiteralInstructionBooleanCall
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.fingerprint.resolvable

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$MISC_PATH/SpoofPlayerParameterPatch;"

@Suppress("unused")
val spoofPlayerParameterPatch = bytecodePatch(
    SPOOF_PLAYER_PARAMETER.title,
    SPOOF_PLAYER_PARAMETER.summary
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        videoInformationPatch,
        playerResponseMethodHookPatch,
    )

    execute {

        addPlayerResponseMethodHook(
            Hook.PlayerParameter(
                "$EXTENSION_CLASS_DESCRIPTOR->spoofParameter(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            ),
        )

        // region fix for subtitles position

        subtitleWindowFingerprint.methodOrThrow().addInstructions(
            0,
            """
                invoke-static {p1, p2, p3, p4, p5}, $EXTENSION_CLASS_DESCRIPTOR->fixSubtitleWindowPosition(IIIZZ)[I
                move-result-object v0
                const/4 v1, 0x0
                aget p1, v0, v1     # ap, anchor position
                const/4 v1, 0x1
                aget p2, v0, v1     # ah, horizontal anchor
                const/4 v1, 0x2
                aget p3, v0, v1     # av, vertical anchor
            """
        )

        // endregion

        // region fix for feature flags

        if (ageRestrictedPlaybackFeatureFlagFingerprint.resolvable()) {
            ageRestrictedPlaybackFeatureFlagFingerprint.injectLiteralInstructionBooleanCall(
                AGE_RESTRICTED_PLAYBACK_FEATURE_FLAG,
                "$EXTENSION_CLASS_DESCRIPTOR->forceDisableAgeRestrictedPlaybackFeatureFlag(Z)Z"
            )
        }

        // endregion

        addSwitchPreference(
            CategoryType.MISC,
            "revanced_spoof_player_parameter",
            "true"
        )

        updatePatchStatus(SPOOF_PLAYER_PARAMETER)

    }
}
