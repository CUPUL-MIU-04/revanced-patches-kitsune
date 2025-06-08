package app.kitsune.patches.youtube.utils.playercontrols

import app.kitsune.patches.youtube.utils.resourceid.bottomUiContainerStub
import app.kitsune.patches.youtube.utils.resourceid.controlsLayoutStub
import app.kitsune.util.fingerprint.legacyFingerprint
import app.kitsune.util.getReference
import app.kitsune.util.indexOfFirstInstructionReversed
import app.kitsune.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val bottomControlsInflateFingerprint = legacyFingerprint(
    name = "bottomControlsInflateFingerprint",
    returnType = "Ljava/lang/Object;",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT
    ),
    literals = listOf(bottomUiContainerStub),
)

internal val controlsLayoutInflateFingerprint = legacyFingerprint(
    name = "controlsLayoutInflateFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.CHECK_CAST,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT
    ),
    literals = listOf(controlsLayoutStub),
)

internal val motionEventFingerprint = legacyFingerprint(
    name = "motionEventFingerprint",
    returnType = "V",
    parameters = listOf("Landroid/view/MotionEvent;"),
    customFingerprint = { method, _ ->
        indexOfTranslationInstruction(method) >= 0
    }
)

internal fun indexOfTranslationInstruction(method: Method) =
    method.indexOfFirstInstructionReversed {
        getReference<MethodReference>()?.name == "setTranslationY"
    }

internal val playerControlsVisibilityEntityModelFingerprint = legacyFingerprint(
    name = "playerControlsVisibilityEntityModelFingerprint",
    accessFlags = AccessFlags.PUBLIC.value,
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.IGET,
        Opcode.INVOKE_STATIC
    ),
    customFingerprint = { method, _ -> method.name == "getPlayerControlsVisibility" }
)

internal val playerControlsVisibilityFingerprint = legacyFingerprint(
    name = "playerControlsVisibilityFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    parameters = listOf("Z", "Z")
)

internal const val PLAYER_TOP_CONTROLS_EXPERIMENTAL_LAYOUT_FEATURE_FLAG = 45629424L

internal val playerTopControlsExperimentalLayoutFeatureFlagFingerprint = legacyFingerprint(
    name = "playerTopControlsExperimentalLayoutFeatureFlagFingerprint",
    returnType = "I",
    literals = listOf(PLAYER_TOP_CONTROLS_EXPERIMENTAL_LAYOUT_FEATURE_FLAG),
)