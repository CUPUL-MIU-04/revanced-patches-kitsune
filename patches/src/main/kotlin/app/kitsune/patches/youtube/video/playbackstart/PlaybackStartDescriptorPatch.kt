package app.kitsune.patches.youtube.video.playbackstart

import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patches.youtube.utils.extension.sharedExtensionPatch
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.Reference

internal lateinit var playbackStartVideoIdReference: Reference

val playbackStartDescriptorPatch = bytecodePatch(
    description = "playbackStartDescriptorPatch"
) {
    dependsOn(sharedExtensionPatch)

    execute {
        // Find the obfuscated method name for PlaybackStartDescriptor.videoId()
        playbackStartFeatureFlagFingerprint.methodOrThrow().apply {
            val stringMethodIndex = indexOfFirstInstructionOrThrow {
                val reference = getReference<MethodReference>()
                reference?.definingClass == PLAYBACK_START_DESCRIPTOR_CLASS_DESCRIPTOR
                        && reference.returnType == "Ljava/lang/String;"
            }

            playbackStartVideoIdReference =
                getInstruction<ReferenceInstruction>(stringMethodIndex).reference
        }
    }
}

