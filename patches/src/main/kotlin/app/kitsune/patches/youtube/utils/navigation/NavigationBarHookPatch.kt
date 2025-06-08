package app.kitsune.patches.youtube.utils.navigation

import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.instructions
import app.kitsune.patcher.patch.PatchException
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.proxy.mutableTypes.MutableMethod
import app.kitsune.patches.shared.mainactivity.injectOnBackPressedMethodCall
import app.kitsune.patches.youtube.utils.extension.Constants.SHARED_PATH
import app.kitsune.patches.youtube.utils.extension.sharedExtensionPatch
import app.kitsune.patches.youtube.utils.mainactivity.mainActivityResolvePatch
import app.kitsune.patches.youtube.utils.playertype.playerTypeHookPatch
import app.kitsune.patches.youtube.utils.resourceid.sharedResourceIdPatch
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.fingerprint.mutableClassOrThrow
import app.kitsune.util.getReference
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.util.MethodUtil

internal const val EXTENSION_CLASS_DESCRIPTOR =
    "$SHARED_PATH/NavigationBar;"
internal const val EXTENSION_NAVIGATION_BUTTON_DESCRIPTOR =
    "$SHARED_PATH/NavigationBar\$NavigationButton;"

private lateinit var bottomBarContainerMethod: MutableMethod
private var bottomBarContainerOffset = 0

lateinit var hookNavigationButtonCreated: (String) -> Unit

val navigationBarHookPatch = bytecodePatch(
    description = "navigationBarHookPatch",
) {
    dependsOn(
        sharedExtensionPatch,
        mainActivityResolvePatch,
        playerTypeHookPatch,
        sharedResourceIdPatch,
    )

    execute {
        fun MutableMethod.addHook(hook: Hook, insertPredicate: Instruction.() -> Boolean) {
            val filtered = instructions.filter(insertPredicate)
            if (filtered.isEmpty()) throw PatchException("Could not find insert indexes")
            filtered.forEach {
                val insertIndex = it.location.index + 2
                val register = getInstruction<OneRegisterInstruction>(insertIndex - 1).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static { v$register }, " +
                            "$EXTENSION_CLASS_DESCRIPTOR->${hook.methodName}(${hook.parameters})V",
                )
            }
        }

        initializeButtonsFingerprint.methodOrThrow(pivotBarConstructorFingerprint).apply {
            // Hook the current navigation bar enum value. Note, the 'You' tab does not have an enum value.
            val navigationEnumClassName = navigationEnumFingerprint.mutableClassOrThrow().type
            addHook(Hook.SET_LAST_APP_NAVIGATION_ENUM) {
                opcode == Opcode.INVOKE_STATIC &&
                        getReference<MethodReference>()?.definingClass == navigationEnumClassName
            }

            // Hook the creation of navigation tab views.
            val drawableTabMethod =
                pivotBarButtonsCreateDrawableViewFingerprint.methodOrThrow()
            addHook(Hook.NAVIGATION_TAB_LOADED) predicate@{
                MethodUtil.methodSignaturesMatch(
                    getReference<MethodReference>() ?: return@predicate false,
                    drawableTabMethod,
                )
            }

            val imageResourceTabMethod =
                pivotBarButtonsCreateResourceViewFingerprint.methodOrThrow()
            addHook(Hook.NAVIGATION_IMAGE_RESOURCE_TAB_LOADED) predicate@{
                MethodUtil.methodSignaturesMatch(
                    getReference<MethodReference>() ?: return@predicate false,
                    imageResourceTabMethod,
                )
            }
        }

        pivotBarButtonsViewSetSelectedFingerprint.methodOrThrow().apply {
            val index = indexOfSetViewSelectedInstruction(this)
            val instruction = getInstruction<FiveRegisterInstruction>(index)
            val viewRegister = instruction.registerC
            val isSelectedRegister = instruction.registerD

            addInstruction(
                index + 1,
                "invoke-static { v$viewRegister, v$isSelectedRegister }, " +
                        "$EXTENSION_CLASS_DESCRIPTOR->navigationTabSelected(Landroid/view/View;Z)V",
            )
        }

        injectOnBackPressedMethodCall(
            EXTENSION_CLASS_DESCRIPTOR,
            "onBackPressed"
        )

        bottomBarContainerMethod = initializeBottomBarContainerFingerprint.methodOrThrow()

        hookNavigationButtonCreated = { extensionClassDescriptor ->
            navigationBarHookCallbackFingerprint.methodOrThrow().addInstruction(
                0,
                "invoke-static { p0, p1 }, $extensionClassDescriptor->navigationTabCreated" +
                        "(${EXTENSION_NAVIGATION_BUTTON_DESCRIPTOR}Landroid/view/View;)V",
            )
        }
    }
}

fun addBottomBarContainerHook(descriptor: String) {
    bottomBarContainerMethod.apply {
        val layoutChangeListenerIndex = indexOfLayoutChangeListenerInstruction(this)
        val bottomBarContainerRegister =
            getInstruction<FiveRegisterInstruction>(layoutChangeListenerIndex).registerC

        addInstruction(
            layoutChangeListenerIndex + bottomBarContainerOffset--,
            "invoke-static { v$bottomBarContainerRegister }, $descriptor"
        )
    }
}

private enum class Hook(val methodName: String, val parameters: String) {
    SET_LAST_APP_NAVIGATION_ENUM("setLastAppNavigationEnum", "Ljava/lang/Enum;"),
    NAVIGATION_TAB_LOADED("navigationTabLoaded", "Landroid/view/View;"),
    NAVIGATION_IMAGE_RESOURCE_TAB_LOADED(
        "navigationImageResourceTabLoaded",
        "Landroid/view/View;"
    ),
}
