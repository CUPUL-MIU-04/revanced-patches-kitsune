package app.kitsune.patches.youtube.general.music

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.replaceInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.extension.Constants.GENERAL_PATH
import app.kitsune.patches.youtube.utils.extension.Constants.PATCH_STATUS_CLASS_DESCRIPTOR
import app.kitsune.patches.youtube.utils.patch.PatchList.GMSCORE_SUPPORT
import app.kitsune.patches.youtube.utils.patch.PatchList.HOOK_YOUTUBE_MUSIC_ACTIONS
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.getContext
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.youtubeMusicPackageName
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.addEntryValues
import app.kitsune.util.findMethodOrThrow
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private const val EXTENSION_CLASS_DESCRIPTOR =
    "$GENERAL_PATH/YouTubeMusicActionsPatch;"

@Suppress("unused")
val youtubeMusicActionsPatch = bytecodePatch(
    HOOK_YOUTUBE_MUSIC_ACTIONS.title,
    HOOK_YOUTUBE_MUSIC_ACTIONS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {

        appDeepLinkFingerprint.matchOrThrow().let {
            it.method.apply {
                val packageNameIndex = it.patternMatch!!.startIndex
                val packageNameField =
                    getInstruction<ReferenceInstruction>(packageNameIndex).reference.toString()

                implementation!!.instructions
                    .withIndex()
                    .filter { (_, instruction) ->
                        instruction.opcode == Opcode.IGET_OBJECT &&
                                instruction.getReference<FieldReference>()
                                    ?.toString() == packageNameField
                    }
                    .map { (index, _) -> index }
                    .reversed()
                    .forEach { index ->
                        val register = getInstruction<TwoRegisterInstruction>(index).registerA

                        addInstructions(
                            index + 1, """
                                invoke-static {v$register}, $EXTENSION_CLASS_DESCRIPTOR->overridePackageName(Ljava/lang/String;)Ljava/lang/String;
                                move-result-object v$register
                                """
                        )
                    }
            }
        }

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: GENERAL",
                "SETTINGS: HOOK_BUTTONS",
                "SETTINGS: HOOK_YOUTUBE_MUSIC_ACTIONS"
            ),
            HOOK_YOUTUBE_MUSIC_ACTIONS
        )

        // endregion

    }

    finalize {
        if (GMSCORE_SUPPORT.included == true) {
            getContext().apply {
                addEntryValues(
                    "revanced_third_party_youtube_music_label",
                    "RVX Music"
                )
                addEntryValues(
                    "revanced_third_party_youtube_music_package_name",
                    youtubeMusicPackageName
                )
            }
            findMethodOrThrow(PATCH_STATUS_CLASS_DESCRIPTOR) {
                name == "RVXMusicPackageName"
            }.apply {
                val replaceIndex = indexOfFirstInstructionOrThrow(Opcode.CONST_STRING)
                val replaceRegister =
                    getInstruction<OneRegisterInstruction>(replaceIndex).registerA

                replaceInstruction(
                    replaceIndex,
                    "const-string v$replaceRegister, \"$youtubeMusicPackageName\""
                )
            }
        }
    }
}
