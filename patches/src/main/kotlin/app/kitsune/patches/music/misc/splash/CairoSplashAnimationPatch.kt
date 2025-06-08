package app.kitsune.patches.music.misc.splash

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.music.utils.compatibility.Constants.YOUTUBE_MUSIC_PACKAGE_NAME
import app.kitsune.patches.music.utils.extension.Constants.MISC_PATH
import app.kitsune.patches.music.utils.patch.PatchList.DISABLE_CAIRO_SPLASH_ANIMATION
import app.kitsune.patches.music.utils.playservice.is_7_06_or_greater
import app.kitsune.patches.music.utils.playservice.is_7_20_or_greater
import app.kitsune.patches.music.utils.playservice.versionCheckPatch
import app.kitsune.patches.music.utils.resourceid.mainActivityLaunchAnimation
import app.kitsune.patches.music.utils.resourceid.sharedResourceIdPatch
import app.kitsune.patches.music.utils.settings.CategoryType
import app.kitsune.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.kitsune.patches.music.utils.settings.addSwitchPreference
import app.kitsune.patches.music.utils.settings.settingsPatch
import app.kitsune.util.Utils.printWarn
import app.kitsune.util.fingerprint.injectLiteralInstructionBooleanCall
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import app.kitsune.util.indexOfFirstLiteralInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$MISC_PATH/CairoSplashAnimationPatch;->disableCairoSplashAnimation(Z)Z"

@Suppress("unused")
val cairoSplashAnimationPatch = bytecodePatch(
    DISABLE_CAIRO_SPLASH_ANIMATION.title,
    DISABLE_CAIRO_SPLASH_ANIMATION.summary,
) {
    compatibleWith(
        YOUTUBE_MUSIC_PACKAGE_NAME(
            "7.06.54",
            "7.16.53",
            "7.25.53",
            "8.05.51",
            "8.12.53",
            "8.14.54",
            "8.17.51",
            "8.17.53",
            "8.19.52",
            "8.20.52",
        ),
    )

    dependsOn(
        settingsPatch,
        sharedResourceIdPatch,
        versionCheckPatch,
    )

    execute {
        if (!is_7_06_or_greater) {
            printWarn("\"${DISABLE_CAIRO_SPLASH_ANIMATION.title}\" is not supported in this version. Use YouTube Music 7.06.54 or later.")
            return@execute
        } else if (!is_7_20_or_greater) {
            cairoSplashAnimationConfigFingerprint.injectLiteralInstructionBooleanCall(
                CAIRO_SPLASH_ANIMATION_FEATURE_FLAG,
                EXTENSION_METHOD_DESCRIPTOR
            )
        } else {
            cairoSplashAnimationConfigFingerprint.methodOrThrow().apply {
                val literalIndex = indexOfFirstLiteralInstructionOrThrow(
                    mainActivityLaunchAnimation
                )
                val insertIndex = indexOfFirstInstructionReversedOrThrow(literalIndex) {
                    opcode == Opcode.INVOKE_VIRTUAL &&
                            getReference<MethodReference>()?.name == "setContentView"
                } + 1
                val freeIndex = indexOfFirstInstructionOrThrow(insertIndex, Opcode.CONST)
                val freeRegister =
                    getInstruction<OneRegisterInstruction>(freeIndex).registerA
                val jumpIndex = indexOfFirstInstructionOrThrow(insertIndex) {
                    opcode == Opcode.INVOKE_VIRTUAL &&
                            getReference<MethodReference>()?.parameterTypes?.firstOrNull() == "Ljava/lang/Runnable;"
                } + 1

                addInstructionsWithLabels(
                    insertIndex, """
                        const/4 v$freeRegister, 0x1
                        invoke-static {v$freeRegister}, $EXTENSION_METHOD_DESCRIPTOR
                        move-result v$freeRegister
                        if-eqz v$freeRegister, :skip
                        """, ExternalLabel("skip", getInstruction(jumpIndex))
                )
            }
        }

        addSwitchPreference(
            CategoryType.MISC,
            "revanced_disable_cairo_splash_animation",
            "false"
        )

        updatePatchStatus(DISABLE_CAIRO_SPLASH_ANIMATION)

    }
}
