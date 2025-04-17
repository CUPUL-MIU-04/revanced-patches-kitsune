package app.revanced.extension.youtube.swipecontrols.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import app.revanced.extension.shared.utils.ResourceUtils.ResourceType
import app.revanced.extension.shared.utils.ResourceUtils.getIdentifier
import app.revanced.extension.shared.utils.StringRef.str
import app.revanced.extension.youtube.swipecontrols.SwipeControlsConfigurationProvider
import app.revanced.extension.youtube.swipecontrols.misc.SwipeControlsOverlay
import app.revanced.extension.youtube.swipecontrols.misc.applyDimension
import kotlin.math.min
import kotlin.math.round

/**
 * main overlay layout for volume and brightness swipe controls
 *
 * @param context context to create in
 */
class SwipeControlsOverlayLayout(
    context: Context,
    private val config: SwipeControlsConfigurationProvider,
) : RelativeLayout(context), SwipeControlsOverlay {
    /**
     * DO NOT use this, for tools only
     */
    constructor(context: Context) : this(context, SwipeControlsConfigurationProvider(context))

    private val autoBrightnessIcon: Drawable
    private val lowBrightnessIcon: Drawable = getDrawable("revanced_ic_sc_brightness_low")
    private val mediumBrightnessIcon: Drawable = getDrawable("revanced_ic_sc_brightness_medium")
    private val highBrightnessIcon: Drawable = getDrawable("revanced_ic_sc_brightness_high")
    private val fullBrightnessIcon: Drawable = getDrawable("revanced_ic_sc_brightness_full")
    private val manualBrightnessIcon: Drawable
    private val mutedVolumeIcon: Drawable
    private val lowVolumeIcon: Drawable = getDrawable("revanced_ic_sc_volume_low")
    private val normalVolumeIcon: Drawable
    private val feedbackTextView: TextView
    private val fullVolumeIcon: Drawable = getDrawable("revanced_ic_sc_volume_high")

    private val circularProgressView: CircularProgressView = CircularProgressView(
        context,
        config.overlayBackgroundOpacity,
        config.overlayShowOverlayMinimalStyle,
        config.overlayProgressColor,
        config.overlayFillBackgroundPaint,
        config.overlayTextColor
    ).apply {
        layoutParams = LayoutParams(300, 300).apply {
            addRule(CENTER_IN_PARENT, TRUE)
        }
        visibility = GONE // Initially hidden
    }
    private val horizontalProgressView: HorizontalProgressView

    private val isAlternativeUI: Boolean = config.isAlternativeUI

    private fun getDrawable(name: String, width: Int? = null, height: Int? = null): Drawable {
        val drawable = resources.getDrawable(
            getIdentifier(name, ResourceType.DRAWABLE, context),
            context.theme,
        )

        if (width != null && height != null) {
            drawable.setTint(config.overlayForegroundColor)
            drawable.setBounds(
                0,
                0,
                width,
                height,
            )
        } else {
            drawable.setTint(config.overlayTextColor)
        }
        return drawable
    }

    init {
        // Initialize horizontal progress bar
        val screenWidth = resources.displayMetrics.widthPixels
        val layoutWidth = (screenWidth * 2 / 3).toInt() // 2/3 of screen width
        horizontalProgressView = HorizontalProgressView(
            context,
            config.overlayBackgroundOpacity,
            config.overlayShowOverlayMinimalStyle,
            config.overlayProgressColor,
            config.overlayFillBackgroundPaint,
            config.overlayTextColor
        ).apply {
            layoutParams = LayoutParams(layoutWidth, 100).apply {
                addRule(CENTER_HORIZONTAL)
                topMargin = 40 // Top margin
            }
            visibility = GONE // Initially hidden
        }

        // init views
        val feedbackYTextViewPadding = 5.applyDimension(context, TypedValue.COMPLEX_UNIT_DIP)
        val feedbackXTextViewPadding = 12.applyDimension(context, TypedValue.COMPLEX_UNIT_DIP)
        val compoundIconPadding = 4.applyDimension(context, TypedValue.COMPLEX_UNIT_DIP)
        feedbackTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            ).apply {
                addRule(CENTER_IN_PARENT, TRUE)
                setPadding(
                    feedbackXTextViewPadding,
                    feedbackYTextViewPadding,
                    feedbackXTextViewPadding,
                    feedbackYTextViewPadding
                )
            }
            background = GradientDrawable().apply {
                cornerRadius = 30f
                setColor(config.overlayTextBackgroundColor)
            }
            setTextColor(config.overlayForegroundColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, config.overlayTextSize.toFloat())
            compoundDrawablePadding = compoundIconPadding
            visibility = GONE
        }

        if (isAlternativeUI) {
            addView(circularProgressView)
            addView(horizontalProgressView)

            autoBrightnessIcon = getDrawable("revanced_ic_sc_brightness_auto")
            manualBrightnessIcon = getDrawable("revanced_ic_sc_brightness_manual")
            mutedVolumeIcon = getDrawable("revanced_ic_sc_volume_mute")
            normalVolumeIcon = getDrawable("revanced_ic_sc_volume_normal")
        } else {
            addView(feedbackTextView)
            // get icons scaled, assuming square icons
            val iconHeight = round(feedbackTextView.lineHeight * .8).toInt()
            autoBrightnessIcon =
                getDrawable("revanced_ic_sc_brightness_auto", iconHeight, iconHeight)
            manualBrightnessIcon =
                getDrawable("revanced_ic_sc_brightness_manual", iconHeight, iconHeight)
            mutedVolumeIcon = getDrawable("revanced_ic_sc_volume_mute", iconHeight, iconHeight)
            normalVolumeIcon = getDrawable("revanced_ic_sc_volume_normal", iconHeight, iconHeight)
        }
    }

    private val feedbackHideHandler = Handler(Looper.getMainLooper())
    private val feedbackHideCallback = Runnable {
        if (isAlternativeUI) {
            circularProgressView.visibility = GONE
            horizontalProgressView.visibility = GONE
        } else {
            feedbackTextView.visibility = GONE
        }
    }

    /**
     * show the feedback view for a given time
     *
     * @param message the message to show
     * @param icon the icon to use
     */
    private fun showFeedbackView(message: String, icon: Drawable) {
        feedbackHideHandler.removeCallbacks(feedbackHideCallback)
        feedbackHideHandler.postDelayed(feedbackHideCallback, config.overlayShowTimeoutMillis)
        feedbackTextView.apply {
            text = message
            setCompoundDrawablesRelative(
                icon,
                null,
                null,
                null,
            )
            visibility = VISIBLE
        }
    }

    /**
     * Displays the progress bar with the appropriate value, icon, and type (brightness or volume).
     */
    private fun showFeedbackView(
        value: String,
        progress: Int,
        max: Int,
        icon: Drawable,
        isBrightness: Boolean
    ) {
        feedbackHideHandler.removeCallbacks(feedbackHideCallback)
        feedbackHideHandler.postDelayed(feedbackHideCallback, config.overlayShowTimeoutMillis)

        val viewToShow =
            if (config.isCircularProgressBar) circularProgressView else horizontalProgressView
        viewToShow.apply {
            setProgress(progress, max, value, isBrightness)
            this.icon = icon
            visibility = VISIBLE
        }
    }

    override fun onVolumeChanged(newVolume: Int, maximumVolume: Int) {
        if (isAlternativeUI) {
            val volumePercentage = (newVolume.toFloat() / maximumVolume) * 100
            val icon = when {
                newVolume == 0 -> mutedVolumeIcon
                volumePercentage < 33 -> lowVolumeIcon
                volumePercentage < 66 -> normalVolumeIcon
                else -> fullVolumeIcon
            }
            showFeedbackView("$newVolume", newVolume, maximumVolume, icon, isBrightness = false)
        } else {
            showFeedbackView(
                "$newVolume",
                if (newVolume > 0) normalVolumeIcon else mutedVolumeIcon,
            )
        }
    }

    override fun onBrightnessChanged(brightness: Double) {
        if (config.shouldLowestValueEnableAutoBrightness && brightness <= 0) {
            if (isAlternativeUI) {
                showFeedbackView(
                    str("revanced_swipe_lowest_value_auto_brightness_overlay_text"),
                    0,
                    100,
                    autoBrightnessIcon,
                    isBrightness = true,
                )
            } else {
                showFeedbackView(
                    str("revanced_swipe_lowest_value_auto_brightness_overlay_text"),
                    autoBrightnessIcon,
                )
            }
        } else if (brightness >= 0) {
            if (isAlternativeUI) {
                val brightnessValue = round(brightness).toInt()
                val icon = when {
                    brightnessValue < 25 -> lowBrightnessIcon
                    brightnessValue < 50 -> mediumBrightnessIcon
                    brightnessValue < 75 -> highBrightnessIcon
                    else -> fullBrightnessIcon
                }
                showFeedbackView(
                    "$brightnessValue%",
                    brightnessValue,
                    100,
                    icon,
                    isBrightness = true
                )
            } else {
                showFeedbackView("${round(brightness).toInt()}%", manualBrightnessIcon)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onEnterSwipeSession() {
        if (config.shouldEnableHapticFeedback) {
            performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING,
            )
        }
    }
}

/**
 * Abstract base class for progress views.
 */
abstract class AbstractProgressView(
    context: Context,
    overlayBackgroundOpacity: Int,
    protected val overlayShowOverlayMinimalStyle: Boolean,
    overlayProgressColor: Int,
    overlayFillBackgroundPaint: Int,
    protected val overlayTextColor: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Combined paint creation function for both fill and stroke styles
    private fun createPaint(
        color: Int,
        style: Paint.Style = Paint.Style.FILL,
        strokeCap: Paint.Cap = Paint.Cap.BUTT,
        strokeWidth: Float = 0f
    ) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = style
        this.color = color
        this.strokeCap = strokeCap
        this.strokeWidth = strokeWidth
    }

    // Initialize paints
    val backgroundPaint = createPaint(overlayBackgroundOpacity, style = Paint.Style.FILL)
    val progressPaint = createPaint(
        overlayProgressColor,
        style = Paint.Style.STROKE,
        strokeCap = Paint.Cap.ROUND,
        strokeWidth = 20f
    )
    val fillBackgroundPaint = createPaint(overlayFillBackgroundPaint, style = Paint.Style.FILL)
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = overlayTextColor
        textAlign = Paint.Align.CENTER
        textSize = 40f // Can adjust based on need
    }

    protected var progress = 0
    protected var maxProgress = 100
    protected var displayText: String = "0"
    protected var isBrightness = true
    var icon: Drawable? = null

    fun setProgress(value: Int, max: Int, text: String, isBrightnessMode: Boolean) {
        progress = value
        maxProgress = max
        displayText = text
        isBrightness = isBrightnessMode
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        // Base class implementation can be empty
    }
}

/**
 * Custom view for rendering a circular progress indicator with icons and text.
 */
@SuppressLint("ViewConstructor")
class CircularProgressView(
    context: Context,
    overlayBackgroundOpacity: Int,
    overlayShowOverlayMinimalStyle: Boolean,
    overlayProgressColor: Int,
    overlayFillBackgroundPaint: Int,
    overlayTextColor: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractProgressView(
    context,
    overlayBackgroundOpacity,
    overlayShowOverlayMinimalStyle,
    overlayProgressColor,
    overlayFillBackgroundPaint,
    overlayTextColor,
    attrs,
    defStyleAttr
) {
    private val rectF = RectF()

    init {
        textPaint.textSize = 40f // Override default text size for circular view
        progressPaint.strokeWidth = 20f
        fillBackgroundPaint.strokeWidth = 20f
        progressPaint.strokeCap = Paint.Cap.ROUND
        fillBackgroundPaint.strokeCap = Paint.Cap.BUTT
        progressPaint.style = Paint.Style.STROKE
        fillBackgroundPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val size = min(width, height).toFloat()
        rectF.set(20f, 20f, size - 20f, size - 20f)

        canvas.drawOval(rectF, fillBackgroundPaint) // Draw the outer ring.
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            size / 3,
            backgroundPaint
        ) // Draw the inner circle.

        // Select the paint for drawing based on whether it's brightness or volume.
        val sweepAngle = (progress.toFloat() / maxProgress) * 360
        canvas.drawArc(rectF, -90f, sweepAngle, false, progressPaint) // Draw the progress arc.

        // Draw the icon in the center.
        icon?.let {
            val iconSize = if (overlayShowOverlayMinimalStyle) 100 else 80
            val iconX = (width - iconSize) / 2
            val iconY = (height / 2) - if (overlayShowOverlayMinimalStyle) 50 else 80
            it.setBounds(iconX, iconY, iconX + iconSize, iconY + iconSize)
            it.draw(canvas)
        }

        // If not a minimal style mode, draw the text inside the ring.
        if (!overlayShowOverlayMinimalStyle) {
            canvas.drawText(displayText, width / 2f, height / 2f + 60f, textPaint)
        }
    }
}

/**
 * Custom view for rendering a rectangular progress bar with icons and text.
 */
@SuppressLint("ViewConstructor")
class HorizontalProgressView(
    context: Context,
    overlayBackgroundOpacity: Int,
    overlayShowOverlayMinimalStyle: Boolean,
    overlayProgressColor: Int,
    overlayFillBackgroundPaint: Int,
    overlayTextColor: Int,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractProgressView(
    context,
    overlayBackgroundOpacity,
    overlayShowOverlayMinimalStyle,
    overlayProgressColor,
    overlayFillBackgroundPaint,
    overlayTextColor,
    attrs,
    defStyleAttr
) {

    private val iconSize = 60f
    private val padding = 40f

    init {
        textPaint.textSize = 36f // Override default text size for horizontal view
        progressPaint.strokeWidth = 0f
        progressPaint.strokeCap = Paint.Cap.BUTT
        progressPaint.style = Paint.Style.FILL
        fillBackgroundPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Radius for rounded corners
        val cornerRadius = min(width, height) / 2

        // Calculate the total width for the elements
        val minimalElementWidth = 5 * padding + iconSize

        // Calculate the starting point (X) to center the elements
        val minimalStartX = (width - minimalElementWidth) / 2

        // Draw the background
        if (!overlayShowOverlayMinimalStyle) {
            canvas.drawRoundRect(0f, 0f, width, height, cornerRadius, cornerRadius, backgroundPaint)
        } else {
            canvas.drawRoundRect(
                minimalStartX,
                0f,
                minimalStartX + minimalElementWidth,
                height,
                cornerRadius,
                cornerRadius,
                backgroundPaint
            )
        }

        if (!overlayShowOverlayMinimalStyle) {
            // Draw the fill background
            val startX = 2 * padding + iconSize
            val endX = width - 4 * padding
            val fillWidth = endX - startX

            canvas.drawRoundRect(
                startX,
                height / 2 - 5f,
                endX,
                height / 2 + 5f,
                10f, 10f,
                fillBackgroundPaint
            )

            // Draw the progress
            val progressWidth = (progress.toFloat() / maxProgress) * fillWidth
            canvas.drawRoundRect(
                startX,
                height / 2 - 5f,
                startX + progressWidth,
                height / 2 + 5f,
                10f, 10f,
                progressPaint
            )
        }

        // Draw the icon
        icon?.let {
            val iconX = if (!overlayShowOverlayMinimalStyle) {
                padding
            } else {
                padding + minimalStartX
            }
            val iconY = height / 2 - iconSize / 2
            it.setBounds(
                iconX.toInt(),
                iconY.toInt(),
                (iconX + iconSize).toInt(),
                (iconY + iconSize).toInt()
            )
            it.draw(canvas)
        }

        // Draw the text on the right
        val textX = if (!overlayShowOverlayMinimalStyle) {
            width - 2 * padding
        } else {
            minimalStartX + minimalElementWidth - 2 * padding
        }
        val textY = height / 2 + textPaint.textSize / 3

        // Draw the text
        canvas.drawText(displayText, textX, textY, textPaint)
    }
}
