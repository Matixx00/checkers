package com.cyberbot.checkers.ui.view

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.withTranslation
import com.cyberbot.checkers.R
import com.cyberbot.checkers.fx.Sound
import com.cyberbot.checkers.fx.SoundType
import com.cyberbot.checkers.game.logic.*
import com.cyberbot.checkers.ui.animator.*
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.roundToInt
import kotlin.math.sqrt


/**
 * The main view used to display the [Grid]. It handles all user interactions and animations
 * related to the [Grid]. It always attempts to be measured as a square.
 */
class CheckersGridView(
    context: Context,
    attrs: AttributeSet?
) : View(context, attrs) {

    // <editor-fold defaultstate="collapsed" desc="Colors and paint">
    var gridColorMoveAllowed: Int = 0
        set(value) {
            field = value
            paintGridColorMoveAllowed.color = value
            invalidate()
        }

    var gridColorMoveAllowedHint: Int = 0
        set(value) {
            field = value
            paintGridColorMoveAllowedHint.color = value
            invalidate()
        }

    var gridColorCaptureAllowedHint: Int = 0
        set(value) {
            field = value
            paintGridColorCaptureAllowedHint.color = value
            invalidate()
        }

    var gridColorMoveForbidden: Int = 0
        set(value) {
            field = value
            paintGridColorMoveForbidden.color = value
            invalidate()
        }

    var gridColorMoveSource: Int = 0
        set(value) {
            field = value
            paintGridColorMoveSource.color = value
            invalidate()
        }

    var gridColorLegal: Int = 0
        set(value) {
            field = value
            paintGridColorLegal.color = value
            invalidate()
        }

    var girdColorIllegal: Int = 0
        set(value) {
            field = value
            paintGridColorIllegal.color = value
            invalidate()
        }

    var playerColor1: Int = 0
        set(value) {
            field = value
            paintPlayerColor1.color = value
            invalidate()
        }

    var playerColor2: Int = 0
        set(value) {
            field = value
            paintPlayerColor2.color = value
            invalidate()
        }

    var playerOutlineColor1: Int = 0
        set(value) {
            field = value
            paintPlayerOutlineColor1.color = value
            invalidate()
        }

    var playerOutlineColor2: Int = 0
        set(value) {
            field = value
            paintPlayerOutlineColor2.color = value
            invalidate()
        }

    var captureHintLineColor: Int = 0
        set(value) {
            field = value
            paintCaptureHintLine.color = value
            invalidate()
        }

    var captureHintDestroyedColor: Int = 0
        set(value) {
            field = value
            paintCaptureHintDestroyed.color = value
            invalidate()
        }

    private val paintGridColorMoveAllowed = Paint(0).apply {
        color = gridColorMoveAllowed
        style = Paint.Style.FILL
    }

    private val paintGridColorMoveAllowedHint = Paint(0).apply {
        color = gridColorMoveAllowedHint
        style = Paint.Style.FILL
    }

    private val paintGridColorCaptureAllowedHint = Paint(0).apply {
        color = gridColorCaptureAllowedHint
        style = Paint.Style.FILL
    }

    private val paintGridColorMoveForbidden = Paint(0).apply {
        color = gridColorMoveForbidden
        style = Paint.Style.FILL
    }

    private val paintGridColorMoveSource = Paint(0).apply {
        color = gridColorMoveSource
        style = Paint.Style.FILL
    }

    private val paintGridColorLegal = Paint(0).apply {
        color = gridColorLegal
        style = Paint.Style.FILL
    }

    private val paintGridColorIllegal = Paint(0).apply {
        color = girdColorIllegal
        style = Paint.Style.FILL
    }

    private val paintPlayerColor1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerColor1
        style = Paint.Style.FILL
    }

    private val paintPlayerColor2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerColor2
        style = Paint.Style.FILL
    }


    private val paintPlayerOutlineColor1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerOutlineColor1
        style = Paint.Style.FILL
    }

    private val paintPlayerOutlineColor2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = playerOutlineColor2
        style = Paint.Style.FILL
    }


    private val paintCaptureHintLine = Paint(0).apply {
        color = captureHintLineColor
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = hintLineWidthCalculated
    }

    private val paintCaptureHintDestroyed = Paint(0).apply {
        color = captureHintLineColor
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = hintLineWidthCalculated
    }
    //</editor-fold>

    private var viewMeasureType: Int = 0
    private var viewWidth: Int = 0
    private var singleCellSize: Float = 0F
    private var playerRadius: Float = 0F
    private var playerRadiusOutline: Float = 0F
    private var playerRadiusIcon: Float = 0F
    private var hintDestroyedRadius: Float = 0F
    private var hintLineWidthCalculated: Float = 0F
        set(value) {
            field = value
            paintCaptureHintLine.strokeWidth = value
        }
    private var hintLineWidthDestroyedCalculated: Float = 0F
        set(value) {
            field = value
            paintCaptureHintDestroyed.strokeWidth = value
        }
    private var userInteractionEnabled = true

    var soundFxEnabled = true
    var playerTurn = PlayerNum.NOPLAYER

    var moveAttemptListener: MoveAttemptListener? = null

    var riseAnimationDuration = 500L
    var artificialAnimationDuration = 100L
    var returnAnimationDuration = 500L
    var playerScaleMoving: Float = 1.35F

    /**
     * This is the main player size, represented as a fraction of a single cell's width.
     *
     * @see [playerColor1]
     * @see [playerColor2]
     */
    var playerSize: Float = 0.65F
        set(value) {
            field = value
            invalidate()
        }

    /**
     * This is the player's outline size, represented as a fraction of a the player's radius.
     * Values smaller then 1 will result in the outline not being showed, since it's drawn under
     * the player.
     *
     * @see [playerOutlineColor1]
     * @see [playerOutlineColor2]
     */
    var playerOutlineSize: Float = 1.175F
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Width of the player's icon if it exists,  represented as a fraction of a the player's radius.
     * Values greater then [playerOutlineSize] are not recommended since they will appear to
     * stick outside of the player. The icon is drawn in the same color as the outline.
     */
    var playerIconSize: Float = 0.75F
        set(value) {
            field = value
            invalidate()
        }

    var hintLineWidth: Float = 0.1F
    var hintLineDestroyedWidth: Float = 0.075F
    var hintLineRadius: Float = 0.6F
    var captureHints = false
    var greyedOutOpacity = 0.6F

    /**
     * Main data source for displaying and interacting with.
     */
    var gridData = Grid(8, 3)
        set(value) {
            field = value

            updateDimensions()
            invalidate()
        }

    private var userInteracting = false
    private var movingEntry: GridEntry? = null
    private var moveOffsetX = 0F
    private var moveOffsetY = 0F
    private var moveX = 0F
    private var moveY = 0F

    private var canvasOffsetX = 0F
    private var canvasOffsetY = 0F

    private var currentPieceAnimator: PieceAnimator? = null
    private var currentAnimator: Animator? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CheckersGridView,
            0, 0
        ).apply {
            try {
                gridColorMoveAllowed =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_allowed,
                        context.getColor(R.color.game_color_grid_move_allowed)
                    )
                gridColorMoveAllowedHint =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_allowed_hint,
                        context.getColor(R.color.game_color_grid_move_allowed_hint)
                    )
                gridColorCaptureAllowedHint =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_capture_allowed_hint,
                        context.getColor(R.color.game_color_grid_capture_allowed_hint)
                    )
                gridColorMoveForbidden =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_forbidden,
                        context.getColor(R.color.game_color_grid_move_forbidden)
                    )
                gridColorMoveSource =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_move_source,
                        context.getColor(R.color.game_color_grid_move_source)
                    )
                gridColorLegal =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_legal,
                        context.getColor(R.color.game_color_grid_default_legal)
                    )
                girdColorIllegal =
                    getColor(
                        R.styleable.CheckersGridView_grid_color_illegal,
                        context.getColor(R.color.game_color_grid_default_illegal)
                    )
                playerColor1 = getColor(
                    R.styleable.CheckersGridView_player_color1,
                    context.getColor(R.color.game_color_player1)
                )
                playerColor2 = getColor(
                    R.styleable.CheckersGridView_player_color2,
                    context.getColor(R.color.game_color_player2)
                )
                playerOutlineColor1 =
                    getColor(
                        R.styleable.CheckersGridView_player_outline_color1,
                        context.getColor(R.color.game_color_player_outline1)
                    )
                playerOutlineColor2 =
                    getColor(
                        R.styleable.CheckersGridView_player_outline_color1,
                        context.getColor(R.color.game_color_player_outline2)
                    )
                captureHintLineColor = getColor(
                    R.styleable.CheckersGridView_capture_hint_color,
                    context.getColor(R.color.game_color_capture_hint_line)
                )
                captureHintDestroyedColor = getColor(
                    R.styleable.CheckersGridView_capture_hint_destroyed_color,
                    context.getColor(R.color.game_color_capture_destroyed_hint_line)
                )
                viewMeasureType =
                    getInteger(R.styleable.CheckersGridView_view_size, 0)
            } finally {
                recycle()
            }
        }
    }

    private fun getGreyedOutPaint(paint: Paint): Paint {
        return Paint(paint).apply {
            alpha = (greyedOutOpacity * 255).roundToInt()
        }
    }

    private fun drawGridEntry(
        canvas: Canvas, entry: GridEntry,
        paint: Paint = if (entry.legal()) paintGridColorLegal else paintGridColorIllegal
    ) {
        val left = entry.x * singleCellSize
        val top = entry.y * singleCellSize

        canvas.drawRect(
            left,
            top,
            left + singleCellSize,
            top + singleCellSize,
            paint
        )
    }

    private fun drawPlayer(
        canvas: Canvas,
        entry: GridEntry,
        cx: Float,
        cy: Float,
        scale: Float = 1F,
        toBeDestroyed: Boolean = false,
        greyedOut: Boolean = false
    ) {
        canvas.apply {
            var playerPaint: Paint
            var outlinePaint: Paint
            when (entry.player) {
                PlayerNum.FIRST -> {
                    playerPaint = paintPlayerColor1
                    outlinePaint = paintPlayerOutlineColor1
                }
                PlayerNum.SECOND -> {
                    playerPaint = paintPlayerColor2
                    outlinePaint = paintPlayerOutlineColor2
                }
                else -> return
            }

            if (greyedOut) {
                playerPaint = getGreyedOutPaint(playerPaint)
                outlinePaint = getGreyedOutPaint(outlinePaint)
            }

            drawCircle(cx, cy, playerRadiusOutline * scale, outlinePaint)
            drawCircle(cx, cy, playerRadius * scale, playerPaint)

            if (entry.pieceType == PieceType.KING) {
                val d = resources.getDrawable(R.drawable.ic_king, null)
                when (entry.player) {
                    PlayerNum.FIRST -> d.setTint(playerOutlineColor1)
                    PlayerNum.SECOND -> d.setTint(playerOutlineColor2)
                    else -> return
                }

                d.setBounds(
                    (cx - playerRadiusIcon * scale).roundToInt(),
                    (cy - playerRadiusIcon * scale).roundToInt(),
                    (cx + playerRadiusIcon * scale).roundToInt(),
                    (cy + playerRadiusIcon * scale).roundToInt()
                )

                d.draw(canvas)
            }

            if (toBeDestroyed) {
                drawLine(
                    cx + hintDestroyedRadius,
                    cy + hintDestroyedRadius,
                    cx - hintDestroyedRadius,
                    cy - hintDestroyedRadius,
                    paintCaptureHintDestroyed
                )
                drawLine(
                    cx + hintDestroyedRadius,
                    cy - hintDestroyedRadius,
                    cx - hintDestroyedRadius,
                    cy + hintDestroyedRadius,
                    paintCaptureHintDestroyed
                )
            }
        }
    }

    /**
     * Attempts to properly animate a move from [srcEntry] to [dstEntry] including
     * any captures along the way.
     *
     * When the animation begins it will call [MoveAttemptListener.onForcedMoveStart] on its
     * [moveAttemptListener] and [MoveAttemptListener.onForcedMoveEnd] when it finishes.
     * The method does not modify the [Grid] contents, so a [MoveAttemptListener] should be
     * registered to call [Grid.attemptMove] after the animation has finished.
     *
     * @param srcEntry The entry to animate from.
     * @param dstEntry The entry to animate to.
     *
     * @return true if this move was allowed and was executed successfully, false otherwise.
     *
     * @see [Grid.destinationAllowed]
     */
    fun animateMove(srcEntry: GridEntry, dstEntry: GridEntry): Boolean {
        if (!gridData.destinationAllowed(srcEntry, dstEntry)) {
            return false
        }

        val destination = gridData.getDestination(srcEntry, dstEntry)
        if (destination === null) {
            throw RuntimeException("Should not be null, since already checked with destinationAllowed")
        }

        userInteractionEnabled = false

        if (destination.isCapture) {
            currentPieceAnimator = CaptureExplosionAnimator(singleCellSize).apply {
                destination.capturedPieces?.forEach { addTargetPiece(it) }
                setDestroyerPiece(
                    srcEntry,
                    playerScaleMoving,
                    destination.destinationEntry
                )

                pieceTypeRemovedListener = {
                    gridData.removeGridEntry(it)
                }

                gridVibrationListener = { x, y ->
                    canvasOffsetX = x * viewWidth
                    canvasOffsetY = y * viewWidth
                }

                soundEffectListener = {
                    Sound.playSound(context, it)
                }

                addUpdateListener { _, _ ->
                    invalidate()
                }

                currentAnimator = createAnimator().apply {
                    doOnEnd {
                        currentPieceAnimator = null
                        currentAnimator = null
                        userInteractionEnabled = true

                        moveAttemptListener?.onForcedMoveEnd(gridData, srcEntry, dstEntry)

                        canvasOffsetX = 0F
                        canvasOffsetY = 0F
                        invalidate()
                    }

                    start()
                }
            }
        } else {
            currentPieceAnimator = FullMoveAnimator(singleCellSize).apply {
                addPiece(srcEntry, dstEntry, 1F, playerScaleMoving)
                addUpdateListener { _, _ ->
                    invalidate()
                }

                currentAnimator = createAnimator().apply {
                    doOnStart {
                        if (soundFxEnabled) Sound.playSound(context, SoundType.MOVE)
                        moveAttemptListener?.onForcedMoveStart(gridData, srcEntry, dstEntry)
                    }

                    doOnEnd {
                        currentPieceAnimator = null
                        currentAnimator = null
                        userInteractionEnabled = true

                        moveAttemptListener?.onForcedMoveEnd(gridData, srcEntry, dstEntry)
                    }

                    duration = artificialAnimationDuration
                    start()
                }
            }
        }

        return true
    }

    private fun playerMoveAllowed(player: PlayerNum): Boolean {
        return player != PlayerNum.NOPLAYER && player == playerTurn
    }

    private fun updateDimensions() {
        singleCellSize = viewWidth.toFloat() / gridData.size
        playerRadius = singleCellSize * playerSize * 0.5F
        playerRadiusOutline = singleCellSize * playerSize * playerOutlineSize * 0.5F
        playerRadiusIcon = singleCellSize * playerSize * playerIconSize * 0.5F
        hintDestroyedRadius = (singleCellSize * playerSize * hintLineRadius * 0.5F) / sqrt(2F)
        hintLineWidthCalculated = singleCellSize * hintLineWidth
        hintLineWidthDestroyedCalculated = singleCellSize * hintLineDestroyedWidth
    }

    private fun handleMove(srcEntry: GridEntry, dstEntry: GridEntry) {
        val destination = gridData.getDestination(srcEntry, dstEntry)
            ?: throw java.lang.RuntimeException("Move here is not allowed")

        movingEntry = null

        if (destination.isCapture) {
            handleMoveCapture(srcEntry, destination)
        } else {
            handleMoveNormal(srcEntry, destination)
        }

        moveY = 0F
        moveX = 0F
    }

    private fun handleMoveNormal(srcEntry: GridEntry, destination: Destination) {
        val dstEntry = destination.destinationEntry

        currentPieceAnimator = MoveScaleAnimator(singleCellSize).apply {
            addPiece(srcEntry, moveX, moveY, dstEntry, playerScaleMoving)
            addUpdateListener { _, _ ->
                invalidate()
            }

            currentAnimator = createAnimator().apply {
                doOnEnd {
                    currentAnimator = null
                    currentPieceAnimator = null
                    userInteractionEnabled = true

                    moveAttemptListener?.onUserMoveEnd(gridData, srcEntry, dstEntry)
                    invalidate()
                }

                duration = returnAnimationDuration
                start()
            }
        }
    }

    private fun handleMoveCapture(srcEntry: GridEntry, destination: Destination) {
        currentPieceAnimator = CaptureExplosionAnimator(singleCellSize).apply {
            destination.capturedPieces?.forEach { addTargetPiece(it) }
            setDestroyerPiece(
                srcEntry,
                moveX,
                moveY,
                playerScaleMoving,
                destination.destinationEntry
            )

            pieceTypeRemovedListener = {
                gridData.removeGridEntry(it)
            }

            gridVibrationListener = { x, y ->
                canvasOffsetX = x * viewWidth
                canvasOffsetY = y * viewWidth
            }

            soundEffectListener = {
                Sound.playSound(context, it)
            }

            addUpdateListener { _, _ ->
                invalidate()
            }

            currentAnimator = createAnimator().apply {
                doOnEnd {
                    currentAnimator = null
                    currentPieceAnimator = null
                    userInteractionEnabled = true

                    moveAttemptListener?.onUserMoveEnd(
                        gridData,
                        srcEntry,
                        destination.destinationEntry
                    )

                    canvasOffsetX = 0F
                    canvasOffsetY = 0F
                    invalidate()
                }

                start()
            }
        }
    }

    private fun drawWhenMoving(canvas: Canvas, srcEntry: GridEntry) {
        val targetY = (moveX / singleCellSize).toInt()
        val targetX = (moveY / singleCellSize).toInt()
        val dstEntry = gridData.getEntryByCoords(targetY, targetX)

        val allowedEntries = gridData.getMovableEntries(playerTurn)
        var destination: Destination? = null
        allowedEntries[movingEntry]?.forEach {
            if (it.destinationEntry == dstEntry) {
                destination = it
            }

            drawGridEntry(
                canvas, it.destinationEntry,
                if (it.isCapture) paintGridColorCaptureAllowedHint
                else paintGridColorMoveAllowedHint
            )
        }

        if (dstEntry != movingEntry) {
            drawGridEntry(
                canvas, dstEntry,
                if (gridData.destinationAllowed(srcEntry, dstEntry))
                    paintGridColorMoveAllowed else paintGridColorMoveForbidden
            )
        }

        drawGridEntry(canvas, srcEntry, paintGridColorMoveSource)

        val dstX = (dstEntry.x + 0.5F) * singleCellSize
        val dstY = (dstEntry.y + 0.5F) * singleCellSize

        // Draw capture hint lines
        destination?.apply {
            if (captureHints && isCapture) {
                var pCx = (srcEntry.x + 0.5F) * singleCellSize
                var pCy = (srcEntry.y + 0.5F) * singleCellSize

                intermediateSteps?.forEach {
                    val cx = (it.x + 0.5F) * singleCellSize
                    val cy = (it.y + 0.5F) * singleCellSize

                    canvas.drawLine(pCx, pCy, cx, cy, paintCaptureHintLine)

                    pCx = cx
                    pCy = cy
                }

                canvas.drawLine(pCx, pCy, dstX, dstY, paintCaptureHintLine)
            }
        }

        val movingEntryValues = currentPieceAnimator?.getValuesForEntry(srcEntry)

        gridData.forEach {
            val cx = (it.x + 0.5F) * singleCellSize
            val cy = (it.y + 0.5F) * singleCellSize

            if (it != movingEntry) {
                drawPlayer(
                    canvas, it, cx, cy,
                    toBeDestroyed = captureHints && destination?.capturedPieces?.contains(it) ?: false,
                    greyedOut = it.player == playerTurn && !allowedEntries.containsKey(it)
                )
            } else if (movingEntryValues == null) {
                drawPlayer(canvas, it, moveX, moveY, playerScaleMoving)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withTranslation(canvasOffsetX, canvasOffsetY) {
            gridData.forEach {
                drawGridEntry(this, it)
            }

            movingEntry?.let { srcEntry ->
                drawWhenMoving(this, srcEntry)
            }

            val allowedEntries =
                if (playerTurn != PlayerNum.NOPLAYER) {
                    gridData.getMovableEntries(playerTurn)
                } else {
                    null
                }

            // Draw all pieces that are not being animated
            // But only if the user is not moving an entry
            // In that case these are drawn by `drawWhenMoving`
            if (movingEntry == null) {
                gridData.forEach {
                    currentPieceAnimator.let { animator ->
                        if (animator == null || !animator.containsEntry(it)) {
                            val cx = (it.x + 0.5F) * singleCellSize
                            val cy = (it.y + 0.5F) * singleCellSize
                            drawPlayer(
                                this, it, cx, cy,
                                greyedOut = it.player == playerTurn &&
                                        !(allowedEntries?.containsKey(it) ?: false)
                            )
                        }
                    }
                }
            }

            // Draw all pieces that are being animated
            currentPieceAnimator?.let { animator ->
                animator.forEach { (e, v) ->
                    // If moving entry is present draw it where the user moves it despite the animations
                    if (e == movingEntry) {
                        drawPlayer(this, e, moveX, moveY, v.scale)
                    } else {
                        drawPlayer(this, e, v.x, v.y, v.scale)
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)

        if (!userInteractionEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                userInteracting = true
                currentAnimator?.let {
                    it.cancel()
                    currentPieceAnimator = null
                    currentAnimator = null
                }

                val x = (event.x / singleCellSize).toInt()
                val y = (event.y / singleCellSize).toInt()

                val entry = gridData.getEntryByCoords(x, y)
                if (!playerMoveAllowed(entry.player) ||
                    !gridData.getMovableEntries(playerTurn).containsKey(entry)
                ) {
                    return true
                }

                moveAttemptListener?.onUserMoveStart(gridData, entry)

                val cx = (entry.x + 0.5F) * singleCellSize
                val cy = (entry.y + 0.5F) * singleCellSize

                moveOffsetX = event.x - cx
                moveOffsetY = event.y - cy
                moveX = cx
                moveY = cy

                if (movingEntry == null) {
                    currentPieceAnimator = ScaleAnimator(singleCellSize).apply {
                        addPiece(entry, 1F, playerScaleMoving)
                        addUpdateListener { _, _ ->
                            invalidate()
                        }

                        currentAnimator = createAnimator().apply {
                            doOnEnd {
                                currentAnimator = null
                            }

                            duration = riseAnimationDuration
                            start()
                        }
                    }
                }

                movingEntry = entry
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                userInteracting = true
                moveX = max(min(event.x - moveOffsetX, viewWidth.toFloat() - 1), 0F)
                moveY = max(min(event.y - moveOffsetY, viewWidth.toFloat() - 1), 0F)

                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val x = (moveX / singleCellSize).toInt()
                val y = (moveY / singleCellSize).toInt()
                val dstEntry = gridData.getEntryByCoords(x, y)

                moveOffsetX = 0F
                moveOffsetY = 0F

                userInteracting = false

                if (movingEntry == null) {
                    return false
                }

                userInteractionEnabled = false

                movingEntry?.let { srcEntry ->
                    val entry = if (gridData.destinationAllowed(srcEntry, dstEntry)) {
                        dstEntry
                    } else {
                        srcEntry
                    }

                    handleMove(srcEntry, entry)

                }
                return true
            }
        }

        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w == 0 || h == 0) {
            return
        }

        viewWidth = w
        updateDimensions()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = when (viewMeasureType) {
            0 -> {
                if (width > height) height else width
            }
            1 -> width
            2 -> height
            else -> throw RuntimeException("Invalid view_size attribute")
        }
        setMeasuredDimension(size, size)
    }
}

/**
 * Interface definition for a callback to be invoked when a move animation or user interaction
 * is started or finished.
 */
interface MoveAttemptListener {
    /**
     * Called when the user begins to interact with the view.
     *
     * @param grid The grid that is the main data source for the view.
     * @param srcEntry The entry that the user is interacting with
     */
    fun onUserMoveStart(grid: Grid, srcEntry: GridEntry)

    /**
     * Called when the user ends th interaction with the view.
     *
     * @param grid The grid that is the main data source for the view.
     * @param srcEntry The entry that the user began the interaction with
     * @param dstEntry The entry that was the target of user interaction
     */
    fun onUserMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry)

    /**
     * Called when an artificial move animation has been started/
     *
     * @param grid The grid that is the main data source for the view.
     * @param srcEntry The entry that the was the source of the animation
     * @param srcEntry The entry that the was the destination of the animation.
     */
    fun onForcedMoveStart(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry)

    /**
     * Called when an artificial move animation has ended.
     *
     * @param grid The grid that is the main data source for the view.
     * @param srcEntry The entry that the was the source of the animation.
     * @param srcEntry The entry that the was the destination of the animation.
     */
    fun onForcedMoveEnd(grid: Grid, srcEntry: GridEntry, dstEntry: GridEntry)
}
