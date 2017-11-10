package apk.imany.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Imany on 2017-10-12.
 */

public class RadarView extends View {

    private Paint mBackgroundCirclePainter, mRadarPainter;
    private int centerX, centerY;

    private int diameter = 2;
    private int mDelay = 10;

    // attrs
    private int _radar_background = Color.parseColor("#222222");
    private int _color_first = _radar_background;
    private int _color_second = Color.parseColor("#333333");
    private float _padding = 20;
    private float _shadow_radius = 8f;
    private int _shadow_color = Color.BLACK;
    private int _speed = 2;
    private Thread mThread;

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(context, attrs);
        init();
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribute(context, attrs);
        init();
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RadarView, 0, 0);
        try {
            _radar_background = ta.getColor(R.styleable.RadarView_rd_background, _radar_background);
            _color_first = ta.getColor(R.styleable.RadarView_rd_color_first, _color_first);
            _color_second = ta.getColor(R.styleable.RadarView_rd_color_second, _color_second);
            _padding = ta.getDimension(R.styleable.RadarView_rd_padding, _padding);
            _shadow_radius = ta.getDimension(R.styleable.RadarView_rd_shadow_radius, _shadow_radius);
            _shadow_color = ta.getColor(R.styleable.RadarView_rd_shadow_color, _shadow_color);
            speed(ta.getInt(R.styleable.RadarView_rd_speed, _speed));

        } finally {
            ta.recycle();
        }
    }

    private void init() {

        mBackgroundCirclePainter = new Paint();
        mBackgroundCirclePainter.setAntiAlias(true);
        mBackgroundCirclePainter.setStrokeWidth(3);
        mBackgroundCirclePainter.setStyle(Paint.Style.FILL);

        mRadarPainter = new Paint(mBackgroundCirclePainter);

        mBackgroundCirclePainter.setColor(_radar_background);
        mBackgroundCirclePainter.setShadowLayer(_shadow_radius, 0.0f, 0.6f, _shadow_color);
        setLayerType(LAYER_TYPE_SOFTWARE, mBackgroundCirclePainter);

        start();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        centerX = width / 2;
        centerY = height / 2;
        int minSize = Math.min(width, height);
        diameter = (int) (minSize - (2 * _padding));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, diameter / 2 + _padding / 4, mBackgroundCirclePainter);

        drawRadarView(canvas);

    }

    private int radarDegree = 0;

    private void drawRadarView(Canvas canvas) {

        Shader shader = new SweepGradient(centerX, centerY, _color_first, _color_second);
        Matrix m = new Matrix();
        m.postRotate(radarDegree, centerX, centerY);
        shader.setLocalMatrix(m);
        mRadarPainter.setShader(shader);

        canvas.drawCircle(centerX, centerY, diameter / 2 + _padding / 4, mRadarPainter);

    }

    /**
     * @param speed :  between 1 and 5
     */
    public RadarView speed(int speed) {
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
                    if (radarDegree == 360) {
                        radarDegree = 0;
                    }
                    radarDegree += _speed;
                    postInvalidate();
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
