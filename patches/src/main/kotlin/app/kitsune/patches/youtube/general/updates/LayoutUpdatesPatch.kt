package app.kitsune.patches.youtube.general.updates

import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.extension.Constants.GENERAL_CLASS_DESCRIPTOR
import app.kitsune.patches.youtube.utils.patch.PatchList.DISABLE_LAYOUT_UPDATES
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.fingerprint.matchOrThrow

@Suppress("unused")
val layoutUpdatesPatch = bytecodePatch(
    DISABLE_LAYOUT_UPDATES.title,
    DISABLE_LAYOUT_UPDATES.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {

        cronetHeaderFingerprint.matchOrThrow().let {
            it.method.apply {
                val index = it.stringMatches!!.first().index

                addInstructions(
                    index, """
                        invoke-static {p1, p2}, $GENERAL_CLASS_DESCRIPTOR->disableLayoutUpdates(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p2
                        """
                )
            }
        }

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: GENERAL",
                "PREFERENCE_CATEGORY: GENERAL_EXPERIMENTAL_FLAGS",
                "SETTINGS: DISABLE_LAYOUT_UPDATES"
            ),
            DISABLE_LAYOUT_UPDATES
        )

        // endregion

    }
}
