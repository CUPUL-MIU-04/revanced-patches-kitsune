package app.kitsune.patches.youtube.feed.components

import app.kitsune.patcher.Fingerprint
import app.kitsune.patcher.extensions.InstructionExtensions.addInstruction
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructions
import app.kitsune.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.kitsune.patcher.extensions.InstructionExtensions.getInstruction
import app.kitsune.patcher.patch.PatchException
import app.kitsune.patcher.patch.bytecodePatch
import app.kitsune.patcher.util.smali.ExternalLabel
import app.kitsune.patches.shared.litho.addLithoFilter
import app.kitsune.patches.shared.litho.emptyComponentLabel
import app.kitsune.patches.shared.mainactivity.onCreateMethod
import app.kitsune.patches.youtube.utils.bottomsheet.bottomSheetHookPatch
import app.kitsune.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.kitsune.patches.youtube.utils.engagement.engagementPanelHookPatch
import app.kitsune.patches.youtube.utils.extension.Constants.COMPONENTS_PATH
import app.kitsune.patches.youtube.utils.extension.Constants.FEED_CLASS_DESCRIPTOR
import app.kitsune.patches.youtube.utils.extension.Constants.FEED_PATH
import app.kitsune.patches.youtube.utils.mainactivity.mainActivityResolvePatch
import app.kitsune.patches.youtube.utils.navigation.navigationBarHookPatch
import app.kitsune.patches.youtube.utils.patch.PatchList.HIDE_FEED_COMPONENTS
import app.kitsune.patches.youtube.utils.playertype.playerTypeHookPatch
import app.kitsune.patches.youtube.utils.playservice.is_19_46_or_greater
import app.kitsune.patches.youtube.utils.playservice.is_20_02_or_greater
import app.kitsune.patches.youtube.utils.playservice.versionCheckPatch
import app.kitsune.patches.youtube.utils.resourceid.bar
import app.kitsune.patches.youtube.utils.resourceid.captionToggleContainer
import app.kitsune.patches.youtube.utils.resourceid.channelListSubMenu
import app.kitsune.patches.youtube.utils.resourceid.contentPill
import app.kitsune.patches.youtube.utils.resourceid.horizontalCardList
import app.kitsune.patches.youtube.utils.resourceid.sharedResourceIdPatch
import app.kitsune.patches.youtube.utils.scrollTopParentFingerprint
import app.kitsune.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.kitsune.patches.youtube.utils.settings.settingsPatch
import app.kitsune.util.REGISTER_TEMPLATE_REPLACEMENT
import app.kitsune.util.fingerprint.injectLiteralInstructionViewCall
import app.kitsune.util.fingerprint.matchOrThrow
import app.kitsune.util.fingerprint.methodOrThrow
import app.kitsune.util.fingerprint.mutableClassOrThrow
import app.kitsune.util.getReference
import app.kitsune.util.getWalkerMethod
import app.kitsune.util.indexOfFirstInstructionOrThrow
import app.kitsune.util.indexOfFirstInstructionReversedOrThrow
import app.kitsune.util.indexOfFirstLiteralInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference

private const val CAROUSEL_SHELF_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/CarouselShelfFilter;"
private const val FEED_COMPONENTS_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/FeedComponentsFilter;"
private const val FEED_VIDEO_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/FeedVideoFilter;"
private const val FEED_VIDEO_VIEWS_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/FeedVideoViewsFilter;"
private const val KEYWORD_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/KeywordContentFilter;"
private const val RELATED_VIDEO_CLASS_DESCRIPTOR =
    "$FEED_PATH/RelatedVideoPatch;"

@Suppress("unused")
val feedComponentsPatch = bytecodePatch(
    HIDE_FEED_COMPONENTS.title,
    HIDE_FEED_COMPONENTS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        mainActivityResolvePatch,
        navigationBarHookPatch,
        playerTypeHookPatch,
        sharedResourceIdPatch,
        settingsPatch,
        bottomSheetHookPatch,
        engagementPanelHookPatch,
        versionCheckPatch,
    )
    execute {

        // region patch for hide carousel shelf, subscriptions channel section, latest videos button

        listOf(
            // carousel shelf, only used to tablet layout.
            Triple(
                breakingNewsFingerprint,
                "hideBreakingNewsShelf",
                horizontalCardList
            ),
            // subscriptions channel section.
            Triple(
                channelListSubMenuFingerprint,
                "hideSubscriptionsChannelSection",
                channelListSubMenu
            ),
            // latest videos button
            Triple(
                contentPillFingerprint,
                "hideLatestVideosButton",
                contentPill
            ),
            Triple(
                latestVideosButtonFingerprint,
                "hideLatestVideosButton",
                bar
            ),
        ).forEach { (fingerprint, methodName, literal) ->
            val smaliInstruction = """
                invoke-static {v$REGISTER_TEMPLATE_REPLACEMENT}, $FEED_CLASS_DESCRIPTOR->$methodName(Landroid/view/View;)V
                """
            fingerprint.injectLiteralInstructionViewCall(literal, smaliInstruction)
        }

        // endregion

        // region patch for hide caption button

        captionsButtonFingerprint.methodOrThrow().apply {
            val constIndex = indexOfFirstLiteralInstructionOrThrow(captionToggleContainer)
            val insertIndex = indexOfFirstInstructionReversedOrThrow(constIndex, Opcode.IF_EQZ)
            val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

            addInstructions(
                insertIndex, """
                    invoke-static {v$insertRegister}, $FEED_CLASS_DESCRIPTOR->hideCaptionsButton(Landroid/view/View;)Landroid/view/View;
                    move-result-object v$insertRegister
                    """
            )
        }

        captionsButtonSyntheticFingerprint.methodOrThrow().apply {
            val constIndex = indexOfFirstLiteralInstructionOrThrow(captionToggleContainer)
            val targetIndex = indexOfFirstInstructionOrThrow(constIndex, Opcode.MOVE_RESULT_OBJECT)
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            addInstruction(
                targetIndex + 1,
                "invoke-static {v$targetRegister}, $FEED_CLASS_DESCRIPTOR->hideCaptionsButtonContainer(Landroid/view/View;)V"
            )
        }

        // endregion

        // region patch for hide floating button

        onCreateMethod.apply {
            val stringIndex = indexOfFirstInstructionOrThrow {
                opcode == Opcode.CONST_STRING &&
                        getReference<StringReference>()?.string == "fab"
            }
            val stringRegister = getInstruction<OneRegisterInstruction>(stringIndex).registerA
            val insertIndex = indexOfFirstInstructionOrThrow(stringIndex) {
                opcode == Opcode.INVOKE_DIRECT &&
                        getReference<MethodReference>()?.name == "<init>"
            }
            val jumpIndex = indexOfFirstInstructionOrThrow(insertIndex, Opcode.CONST_STRING)

            addInstructionsWithLabels(
                insertIndex, """
                    invoke-static {v$stringRegister}, $FEED_CLASS_DESCRIPTOR->hideFloatingButton(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$stringRegister
                    if-eqz v$stringRegister, :hide
                    """, ExternalLabel("hide", getInstruction(jumpIndex))
            )
        }

        // endregion

        // region patch for hide relative video

        linearLayoutManagerItemCountsFingerprint.matchOrThrow().let {
            val methodWalker =
                it.getWalkerMethod(it.patternMatch!!.endIndex)
            methodWalker.apply {
                val index = indexOfFirstInstructionOrThrow(Opcode.MOVE_RESULT)
                val register = getInstruction<OneRegisterInstruction>(index).registerA

                addInstructions(
                    index + 1, """
                        invoke-static {v$register}, $RELATED_VIDEO_CLASS_DESCRIPTOR->overrideItemCounts(I)I
                        move-result v$register
                        """
                )
            }
        }

        // endregion

        // region patch for hide subscriptions channel section for tablet

        // Integrated as a litho component since YouTube 20.02.
        if (!is_20_02_or_greater) {
            arrayOf(
                channelListSubMenuTabletFingerprint,
                channelListSubMenuTabletSyntheticFingerprint
            ).forEach { fingerprint ->
                fingerprint.methodOrThrow().apply {
                    addInstructionsWithLabels(
                        0, """
                            invoke-static {}, $FEED_CLASS_DESCRIPTOR->hideSubscriptionsChannelSection()Z
                            move-result v0
                            if-eqz v0, :show
                            return-void
                            """, ExternalLabel("show", getInstruction(0))
                    )
                }
            }
        }

        // endregion

        // region patch for hide category bar

        fun <RegisterInstruction : OneRegisterInstruction> Pair<String, Fingerprint>.patch(
            insertIndexOffset: Int = 0,
            hookRegisterOffset: Int = 0,
            instructions: (Int) -> String
        ) =
            matchOrThrow().let {
                it.method.apply {
                    val endIndex = it.patternMatch!!.endIndex

                    val insertIndex = endIndex + insertIndexOffset
                    val register =
                        getInstruction<RegisterInstruction>(endIndex + hookRegisterOffset).registerA

                    addInstructions(insertIndex, instructions(register))
                }
            }

        filterBarHeightFingerprint.patch<TwoRegisterInstruction> { register ->
            """
                invoke-static { v$register }, $FEED_CLASS_DESCRIPTOR->hideCategoryBarInFeed(I)I
                move-result v$register
            """
        }

        relatedChipCloudFingerprint.patch<OneRegisterInstruction>(1) { register ->
            "invoke-static { v$register }, " +
                    "$FEED_CLASS_DESCRIPTOR->hideCategoryBarInRelatedVideos(Landroid/view/View;)V"
        }

        searchResultsChipBarFingerprint.patch<OneRegisterInstruction>(-1, -2) { register ->
            """
                invoke-static { v$register }, $FEED_CLASS_DESCRIPTOR->hideCategoryBarInSearch(I)I
                move-result v$register
            """
        }

        // endregion

        // region patch for hide mix playlists

        elementParserFingerprint.matchOrThrow(elementParserParentFingerprint).let {
            it.method.apply {
                val freeRegister = implementation!!.registerCount - parameters.size - 2
                val insertIndex = indexOfBufferParserInstruction(this)

                if (is_19_46_or_greater) {
                    val objectIndex =
                        indexOfFirstInstructionReversedOrThrow(insertIndex, Opcode.IGET_OBJECT)
                    val objectRegister =
                        getInstruction<TwoRegisterInstruction>(objectIndex).registerA

                    addInstructionsWithLabels(
                        insertIndex, """
                            invoke-static {v$objectRegister, p3}, $FEED_COMPONENTS_FILTER_CLASS_DESCRIPTOR->filterMixPlaylists(Ljava/lang/Object;[B)Z
                            move-result v$freeRegister
                            if-eqz v$freeRegister, :ignore
                            """ + emptyComponentLabel,
                        ExternalLabel("ignore", getInstruction(insertIndex))
                    )
                } else {
                    val objectIndex = indexOfFirstInstructionOrThrow(Opcode.MOVE_OBJECT)
                    val objectRegister =
                        getInstruction<TwoRegisterInstruction>(objectIndex).registerA
                    val jumpIndex = it.patternMatch!!.startIndex

                    addInstructionsWithLabels(
                        insertIndex, """
                            invoke-static {v$objectRegister, v$freeRegister}, $FEED_COMPONENTS_FILTER_CLASS_DESCRIPTOR->filterMixPlaylists(Ljava/lang/Object;[B)Z
                            move-result v$freeRegister
                            if-nez v$freeRegister, :filter
                            """, ExternalLabel("filter", getInstruction(jumpIndex))
                    )
                    addInstruction(
                        0,
                        "move-object/from16 v$freeRegister, p3"
                    )
                }
            }
        }

        // endregion

        // region patch for hide show more button

        showMoreButtonFingerprint.mutableClassOrThrow().let {
            val getViewMethod =
                it.methods.find { method ->
                    method.parameters.isEmpty() &&
                            method.returnType == "Landroid/view/View;"
                }

            getViewMethod?.apply {
                val targetIndex = implementation!!.instructions.size - 1
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstruction(
                    targetIndex,
                    "invoke-static {v$targetRegister}, $FEED_CLASS_DESCRIPTOR->hideShowMoreButton(Landroid/view/View;)V"
                )
            } ?: throw PatchException("Failed to find getView method")
        }

        // endregion

        // region patch for hide channel tab

        val channelTabBuilderMethod =
            channelTabBuilderFingerprint.methodOrThrow(scrollTopParentFingerprint)

        channelTabRendererFingerprint.matchOrThrow().let {
            it.method.apply {
                val iteratorIndex = indexOfFirstInstructionOrThrow {
                    getReference<MethodReference>()?.name == "hasNext"
                }
                val iteratorRegister =
                    getInstruction<FiveRegisterInstruction>(iteratorIndex).registerC

                val targetIndex = indexOfFirstInstructionOrThrow {
                    val reference = ((this as? ReferenceInstruction)?.reference as? MethodReference)

                    opcode == Opcode.INVOKE_INTERFACE &&
                            reference?.returnType == channelTabBuilderMethod.returnType &&
                            reference.parameterTypes == channelTabBuilderMethod.parameterTypes
                }

                val objectIndex =
                    indexOfFirstInstructionReversedOrThrow(targetIndex, Opcode.IGET_OBJECT)
                val objectInstruction = getInstruction<TwoRegisterInstruction>(objectIndex)
                val objectReference = getInstruction<ReferenceInstruction>(objectIndex).reference

                addInstructionsWithLabels(
                    objectIndex + 1, """
                        invoke-static {v${objectInstruction.registerA}}, $FEED_CLASS_DESCRIPTOR->hideChannelTab(Ljava/lang/String;)Z
                        move-result v${objectInstruction.registerA}
                        if-eqz v${objectInstruction.registerA}, :ignore
                        invoke-interface {v$iteratorRegister}, Ljava/util/Iterator;->remove()V
                        goto :next_iterator
                        :ignore
                        iget-object v${objectInstruction.registerA}, v${objectInstruction.registerB}, $objectReference
                        """, ExternalLabel("next_iterator", getInstruction(iteratorIndex))
                )
            }
        }

        // endregion

        addLithoFilter(CAROUSEL_SHELF_FILTER_CLASS_DESCRIPTOR)
        addLithoFilter(FEED_COMPONENTS_FILTER_CLASS_DESCRIPTOR)
        addLithoFilter(FEED_VIDEO_FILTER_CLASS_DESCRIPTOR)
        addLithoFilter(FEED_VIDEO_VIEWS_FILTER_CLASS_DESCRIPTOR)
        addLithoFilter(KEYWORD_FILTER_CLASS_DESCRIPTOR)

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: FEED",
                "SETTINGS: HIDE_FEED_COMPONENTS"
            ),
            HIDE_FEED_COMPONENTS
        )

        // endregion

    }
}
