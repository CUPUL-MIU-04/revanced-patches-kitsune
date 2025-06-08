package app.kitsune.patches.youtube.misc.stablevolume

import app.kitsune.patcher.data.BytecodeContext
import app.kitsune.patcher.patch.BytecodePatch
import app.kitsune.patcher.patch.annotation.Patch
import app.kitsune.patches.youtube.utils.integrations.Constants.INTEGRATIONS_CLASS_DESCRIPTOR

@Patch(
    name = "Stable Volume",
    description = "Adds option to lock volume at a fixed level",
    dependencies = [],
    isOptional = true
)
class StableVolumePatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {
        // 1. Inyectar nuestra clase de integración
        context.injectClass("$INTEGRATIONS_CLASS_DESCRIPTOR/patches/volume/StableVolumePatch;")

        // 2. Modificar el controlador de volumen
        val volumeControllerClass = context.findClass("Lcom/google/android/apps/youtube/app/player/overlay/VolumeController;")
            ?: throw Exception("VolumeController class not found!")

        volumeControllerClass.mutableClass.methods
            .first { it.name == "onVolumeChanged" }
            .addInstructions(
                0, """
                    invoke-static {p0}, $INTEGRATIONS_CLASS_DESCRIPTOR/patches/volume/StableVolumePatch;->isStableVolumeEnabled(Landroid/content/Context;)Z
                    move-result v0
                    if-nez v0, :original
                    
                    invoke-static {p0}, $INTEGRATIONS_CLASS_DESCRIPTOR/patches/volume/StableVolumePatch;->getFixedVolumeLevel(Landroid/content/Context;)I
                    move-result v0
                    return v0
                    
                    :original
                """
            )
    }
}