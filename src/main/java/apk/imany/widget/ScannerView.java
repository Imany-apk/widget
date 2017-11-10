package apk.imany.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Imany on 2017-10-12.
 */

public class ScannerView extends View {

    private Paint mBackgroundCirclePainter, mScannerPainter, miniPainter;
    private int width, height, centerX, centerY;

    private int radian = 0;
    private int diameter = 2;
    private float sweepAngle = 270;
    private int mDelay = 1;

    // attrs
    private int _sc_background = Color.parseColor("#222222");
    private int _color_first = _sc_background;
    private int _color_second = Color.parseColor("#333333");
    private float _padding = 20;
    private boolean _back_light = false;
    private boolean _top_left, _top_right, _bottom_left, _bottom_right, _mini_all;
    private float _shadow_radius = 8f;
    private int _shadow_color = Color.BLACK;
    private int _speed = 2;
    private Thread mThread;

    public ScannerView(Context context) {
        super(context);
        init();
    }

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(context, attrs);
        init();
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribute(context, attrs);
        init();
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ScannerView, 0, 0);
        try {
            _sc_background = ta.getColor(R.styleable.ScannerView_sc_background, _sc_background);
            _color_first = ta.getColor(R.styleable.ScannerView_sc_color_first, _color_first);
            _color_second = ta.getColor(R.styleable.ScannerView_sc_color_second, _color_second);
            _padding = ta.getDimension(R.styleable.ScannerView_sc_padding, _padding);
            _back_light = ta.getBoolean(R.styleable.ScannerView_sc_back_light, _back_light);
            _top_left = ta.getBoolean(R.styleable.ScannerView_sc_mini_top_left, _top_left);
            _top_right = ta.getBoolean(R.styleable.ScannerView_sc_mini_top_right, _top_right);
            _bottom_left = ta.getBoolean(R.styleable.ScannerView_sc_mini_bottom_left, _bottom_left);
            _bottom_right = ta.getBoolean(R.styleable.ScannerView_sc_mini_bottom_right, _bottom_right);
            _mini_all = ta.getBoolean(R.styleable.ScannerView_sc_mini_all, _mini_all);
            _shadow_radius = ta.getDimension(R.styleable.ScannerView_sc_shadow_radius, _shadow_radius);
            _shadow_color = ta.getColor(R.styleable.ScannerView_sc_shadow_color, _shadow_color);
            speed(ta.getInt(R.styleable.ScannerView_sc_speed, _speed));
            mDelay = 100 - _speed * 20;

        } finally {
            ta.recycle();
        }
    }

    private void init() {

        mBackgroundCirclePainter = new Paint();
        mBackgroundCirclePainter.setAntiAlias(true);
        mBackgroundCirclePainter.setStrokeWidth(3);
        mBackgroundCirclePainter.setStyle(Paint.Style.FILL);

        mScannerPainter = new Paint(mBackgroundCirclePainter);

        miniPainter = new Paint(mBackgroundCirclePainter);
        miniPainter.setStyle(Paint.Style.STROKE);
        miniPainter.setColor(Color.WHITE);


        mBackgroundCirclePainter.setColor(_sc_background);
        mBackgroundCirclePainter.setShadowLayer(_shadow_radius, 0.0f, 0.6f, _shadow_color);
        setLayerType(LAYER_TYPE_SOFTWARE, mBackgroundCirclePainter);


        start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        centerX = width / 2;
        centerY = height / 2;
        int minSize = Math.min(width, height);
        diameter = (int) (minSize - (2 * _padding));

        shade();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (_back_light) {
            drawBackLight(canvas);
        }

        canvas.drawCircle(centerX, centerY, diameter / 2 + _padding / 4, mBackgroundCirclePainter);

        drawScannerView(canvas);

        if ((_mini_all && !_top_left) || _top_left) drawTopLeftArc(canvas);
        if ((_mini_all && !_top_right) || _top_right) drawTopRightArc(canvas);
        if ((_mini_all && !_bottom_left) || _bottom_left) drawBottomLeftArc(canvas);
        if ((_mini_all && !_bottom_right) || _bottom_right) drawBottomRightArc(canvas);

    }

    private void drawTopLeftArc(Canvas canvas) {
        @SuppressLint("DrawAllocation") RectF tlRect = new RectF(
                _padding,
                _padding,
                _padding + diameter / 10,
                _padding + diameter / 10
        );

        canvas.drawArc(tlRect, radian, sweepAngle, false, miniPainter);
    }

    private void drawTopRightArc(Canvas canvas) {
        @SuppressLint("DrawAllocation") RectF tlRect = new RectF(
                width - _padding - diameter / 10,
                _padding,
                width - _padding,
                _padding + diameter / 10
        );

        canvas.drawArc(tlRect, radian, sweepAngle, false, miniPainter);
    }

    private void drawBottomLeftArc(Canvas canvas) {
        @SuppressLint("DrawAllocation") RectF brRect = new RectF(
                _padding,
                height - _padding - diameter / 10,
                _padding + diameter / 10,
                height - _padding
        );

        canvas.drawArc(brRect, radian, sweepAngle, true, miniPainter);
    }

    private void drawBottomRightArc(Canvas canvas) {
        @SuppressLint("DrawAllocation") RectF brRect = new RectF(
                width - _padding - diameter / 10,
                height - _padding - diameter / 10,
                width - _padding,
                height - _padding
        );

        canvas.drawArc(brRect, radian, sweepAngle, true, miniPainter);
    }


    private void drawBackLight(Canvas canvas) {
        @SuppressLint("DrawAllocation") RectF rect0 = new RectF(
                centerX - diameter / 2 - 1000,
                centerY - diameter / 2 - 1000,
                centerX + diameter / 2 + 1000,
                centerY + diameter / 2 + 1000
        );

        canvas.drawArc(rect0, radian, sweepAngle, true, mScannerPainter);
    }

    private void drawScannerView(Canvas canvas) {

        @SuppressLint("DrawAllocation") RectF rect = new RectF(
                centerX - diameter / 2,
                centerY - diameter / 2,
                centerX + diameter / 2,
                centerY + diameter / 2
        );

        canvas.drawArc(rect, radian, sweepAngle, true, mScannerPainter);
    }

    private void shade() {
        int temp = _color_first;
        _color_first = _color_second;
        _color_second = temp;
        Shader shader = new RadialGradient(
                centerX,
                centerY,
                diameter / 2,
                _color_first,
                _color_second,
                Shader.TileMode.REPEAT
        );
        mScannerPainter.setShader(shader);
    }

    private boolean isOpening = true;

    public void increaseRadian() {
        if (radian >= 360) {
            radian = 0;
        }

        if (sweepAngle >= 360 && isOpening) {
            isOpening = false;
        } else if (sweepAngle <= 6 && !isOpening) {
            isOpening = true;
            shade();
        }
        if (isOpening) {
            sweepAngle += (_speed * 2);
            radian += _speed;
        } else {
            sweepAngle -= (_speed * 4);
            radian += (_speed * 6 );
        }

        postInvalidate();
    }

    public ScannerView backLight(boolean drawBackLight) {
        _back_light = drawBackLight;
        return this;
    }

    /**
     * @param speed :  between 1 and 5
     */
    public ScannerView speed(int speed) {
        _speed = speed;
        if (_speed > 5) _speed = 5;
        else if (_speed < 1) _speed = 1;
        return this;
    }

    public void start() {
        dismiss();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(mDelay);
                    increaseRadian();
                }
            }
        });
        mThread.start();
    }

    public void dismiss() {
        if (mThread != null && !mThread.isInterrupted()) {
            mThread.interrupt();
            mThread = null;
        }
    }

}
