package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Color.parseColor
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.content.ContextCompat.getColor
import ru.skillbranch.devintensive.R
import kotlin.math.min


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ImageView(context, attrs, defStyle) {

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2

        private const val COLOR_DRAWABLE_DIMENSION = 2
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888

        private val SCALE_TYPE = ScaleType.CENTER_CROP
    }

    private var mColorFilter: ColorFilter? = null
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = (DEFAULT_BORDER_WIDTH * resources.displayMetrics.density).toInt()

    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()

    private var mBitmapShader: BitmapShader? = null
    private var mBitmap: Bitmap? = null
    private var mBitmapWidth: Int = 0
    private var mBitmapHeight: Int = 0

    private var mDrawableRadius: Float = 0f
    private var mBorderRadius: Float = 0f

    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mReady: Boolean = false
    private var mSetupPending: Boolean = false
    private var mBorderOverlay: Boolean = false


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.CircleImageView,
                defStyle,
                0
            )

            borderWidth =
                a.getDimensionPixelSize(
                    R.styleable.CircleImageView_cv_borderWidth,
                    (DEFAULT_BORDER_WIDTH * resources.displayMetrics.density).toInt()
                )
            borderColor =
                a.getColor(
                    R.styleable.CircleImageView_cv_borderColor,
                    DEFAULT_BORDER_COLOR
                )

            a.recycle()

            init()
        }
    }

    @Dimension(unit = DP)
    fun getBorderWidth(): Int = (borderWidth / resources.displayMetrics.density).toInt()

    fun setBorderWidth(@Dimension(unit = DP) dp: Int) {
        borderWidth = (dp * resources.displayMetrics.density).toInt()
        setup()
    }

    fun getBorderColor() = borderColor

    fun setBorderColor(hex: String) {
        borderColor = parseColor(hex)
        mBorderPaint.color = borderColor
        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = getColor(context, colorId)
        mBorderPaint.color = borderColor
        invalidate()
    }

    private fun init() {
        super.setScaleType(SCALE_TYPE)
        mReady = true

        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType?) {
        require(scaleType == SCALE_TYPE) { "ScaleType $scaleType not supported." }
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }

        if (width == 0 && height == 0) {
            return
        }

        if (mBitmap == null) {
            invalidate()
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        mBitmapPaint.shader = mBitmapShader

        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = borderWidth.toFloat()

        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width

        mBorderRect.set(calculateBounds())
        mBorderRadius = min(
            (mBorderRect.height() - borderWidth) / 2.0f,
            (mBorderRect.width() - borderWidth) / 2.0f
        )

        mDrawableRect.set(mBorderRect)
        if (!mBorderOverlay && borderWidth > 0) {
            mDrawableRect.inset(borderWidth - 1.0f, borderWidth - 1.0f)
        }
        mDrawableRadius = min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)

        applyColorFilter()
        updateShaderMatrix()
        invalidate()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2F
        val top = paddingTop + (availableHeight - sideLength) / 2F

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0F
        var dy = 0F

        mShaderMatrix.set(null)

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5F
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5F
        }

        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx + 0.5F + mDrawableRect.left, dy + 0.5F + mDrawableRect.top)

        mBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        if (cf == mColorFilter) {
            return
        }

        mColorFilter = cf
        applyColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? {
        return mColorFilter
    }

    private fun applyColorFilter() {
        mBitmapPaint.colorFilter = mColorFilter
    }

    override fun onDraw(canvas: Canvas?) {
        if (mBitmap == null) {
            return
        }

        canvas?.drawCircle(
            mDrawableRect.centerX(),
            mDrawableRect.centerY(),
            mDrawableRadius,
            mBitmapPaint
        )

        if (borderWidth > 0) {
            canvas?.drawCircle(
                mBorderRect.centerX(),
                mBorderRect.centerY(),
                mBorderRadius,
                mBorderPaint
            )
        }
    }

    private fun initializeBitmap() {
        mBitmap = getBitmapFromDrawable(drawable)!!
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLOR_DRAWABLE_DIMENSION,
                    COLOR_DRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}