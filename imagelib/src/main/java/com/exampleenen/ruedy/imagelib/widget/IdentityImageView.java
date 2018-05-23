package com.exampleenen.ruedy.imagelib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.exampleenen.ruedy.imagelib.R;


/**
 * Created by 丁瑞 on 2017/4/9.
 */

public class IdentityImageView extends FrameLayout {
    private Context mContext;
    private CircleImageView bigImageView;//大圆
    private CircleImageView smallImageView;//小圆
    private float radiusScale;//小图片与大图片的比例，默认0.28，刚刚好，大了不好看
    int radius;//大图片半径
    private int smallRadius;//小图片半径
    private double angle = 45; //标识角度大小
    private boolean isprogress;//是否可以加载进度条，必须设置为true才能开启
    private int borderColor = 0;//边框颜色
    private int borderWidth;//边框、进度条宽度
    private TextView textView;//标识符为文字，用的地方比较少
    private boolean hintSmallView;//标识符是否隐藏
    private float progresss;
    private Drawable bigImage;//大图片
    private Drawable smallimage;//小图片
    private int setborderColor = 0;//动态设置边框颜色值
    private int totalwidth;

    public IdentityImageView(Context context) {
        this(context, null);
    }

    public IdentityImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IdentityImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setWillNotDraw(false);//是的ondraw方法被执行

        addThreeView();

        initAttrs(attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int viewHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int viewWidht = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        switch (viewWidthMode) {
            case MeasureSpec.EXACTLY:   //说明在布局文件中使用的是具体值：100dp或者match_parent
                //为了方便，让半径等于宽高中小的那个，再设置宽高至半径大小
                totalwidth = viewWidht < viewHeight ? viewWidht : viewHeight;
                float scale = 1 + radiusScale;
                radius = totalwidth / 2;
                break;
            case MeasureSpec.AT_MOST:  //说明在布局文件中使用的是wrap_content:
                //这时我们也写死宽高
                radius = 200;
                totalwidth = (int) ((radius + radius * radiusScale) * 2);
                break;
            default:
                radius = 200;
                totalwidth = (int) ((radius + radius * radiusScale) * 2);
                break;
        }
        setMeasuredDimension(totalwidth, totalwidth);
        adjustThreeView();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        super.onLayout(b, i, i1, i2, i3);
        //重点在于smallImageView的位置确定,默认为放在右下角，可自行拓展至其他位置
        double cos = Math.cos(angle * Math.PI / 180);
        double sin = Math.sin(angle * Math.PI / 180);
        double left = totalwidth / 2 + (radius * cos - smallRadius);
        double top = totalwidth / 2 + (radius * sin - smallRadius);
        int right = (int) (left + 2 * smallRadius);
        int bottom = (int) (top + 2 * smallRadius);
        //        bigImageView.layout(smallRadius / 2, smallRadius / 2, totalwidth - smallRadius / 2, totalwidth - smallRadius / 2);
        textView.layout((int) left, (int) top, right, bottom);
        smallImageView.layout((int) left, (int) top, right, bottom);

    }

    private void adjustThreeView() {
        bigImageView.setLayoutParams(new LayoutParams(radius * 2, radius * 2));
        smallRadius = (int) (radius * radiusScale);
        smallImageView.setLayoutParams(new LayoutParams(smallRadius, smallRadius));
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    private void addThreeView() {
        bigImageView = new CircleImageView(mContext);//大圆
        bigImageView.setBorderWidth(borderWidth);
        bigImageView.setBorderColor(borderColor);
        smallImageView = new CircleImageView(mContext);//小圆

        textView = new TextView(mContext);//文本
        textView.setGravity(Gravity.CENTER);

        addView(bigImageView);

        smallRadius = (int) (radius * 2 * radiusScale);

        LayoutParams layoutParams = new LayoutParams(smallRadius, smallRadius);
        addView(smallImageView, layoutParams);

        addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        smallImageView.bringToFront();//使小图片位于最上层
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IdentityImageView, defStyleAttr, 0);
        bigImage = typedArray.getDrawable(R.styleable.IdentityImageView_iciv_bigimage);
        smallimage = typedArray.getDrawable(R.styleable.IdentityImageView_iciv_smallimage);
        angle = typedArray.getFloat(R.styleable.IdentityImageView_iciv_angle, 45);//小图以及进度条起始角度
        radiusScale = typedArray.getFloat(R.styleable.IdentityImageView_iciv_radiusscale, 0.28f);//大图和小图的比例
        //是否要进度条，不为true的话，设置，进度条颜色和宽度也没用
        borderColor = typedArray.getColor(R.styleable.IdentityImageView_iciv_border_color, 0);//边框颜色
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.IdentityImageView_iciv_border_width, 0);//边框宽（，同为进度条）
        hintSmallView = typedArray.getBoolean(R.styleable.IdentityImageView_iciv_hint_smallimageview, false);//隐藏小图片
        if (hintSmallView) {
            smallImageView.setVisibility(GONE);
        }
        if (bigImage != null) {
            bigImageView.setImageDrawable(bigImage);
            bigImageView.setBorderWidth(borderWidth);
            bigImageView.setBorderColor(borderColor);
        }
        if (smallimage != null) {
            smallImageView.setImageDrawable(smallimage);
        }
        typedArray.recycle();
    }

    /**
     * 获得textview
     *
     * @return textView
     */
    public TextView getTextView() {
        if (textView != null)
            return textView;
        else
            return null;
    }

    /**
     * 获得大图片
     *
     * @return bigImageView
     */
    public CircleImageView getBigCircleImageView() {
        if (bigImageView != null)
            return bigImageView;
        else
            return null;
    }

    /**
     * 获得小图片
     *
     * @return smallImageView
     */
    public CircleImageView getSmallCircleImageView() {
        if (smallImageView != null)
            return smallImageView;
        else
            return null;
    }

    /**
     * 设置进度条进度，一共360
     *
     * @param angle 进度大小
     */
    public void setProgress(float angle) {
        if (progresss == angle) {
            return;
        }
        progresss = angle;
        requestLayout();
        invalidate();
    }

    /**
     * 设置标识的角度
     *
     * @param angles 角度
     */
    public void setAngle(int angles) {
        if (angles == angle)
            return;
        angle = angles;
        requestLayout();
        invalidate();
    }

    /**
     * 设置标识的大小
     *
     * @param v 比例
     */
    public void setRadiusScale(float v) {
        if (v == radiusScale)
            return;
        radiusScale = v;
        requestLayout();
        invalidate();

    }

    /**
     * 设置是否可以有进度条
     *
     * @param b 是否有进度条
     */
    public void setIsprogress(boolean b) {

        if (b == isprogress)
            return;
        isprogress = b;
        requestLayout();
        invalidate();
    }

    /**
     * 设置填充的颜色
     *
     * @param color 边框颜色
     */
    public void setBorderColor(int color) {
        if (color == borderColor)
            return;
        setborderColor = color;
        bigImageView.setBorderColor(setborderColor);
    }


    /**
     * @param width 边框和进度条宽度
     */
    public void setBorderWidth(int width) {
        int widthPx = dp2px(width);
        if (widthPx == borderWidth)
            return;
        borderWidth = widthPx;
        bigImageView.setBorderWidth(widthPx);
    }

    public void setHintSmallView(boolean hint) {
        if (hint) {
            smallImageView.setVisibility(GONE);
        } else {
            smallImageView.setVisibility(VISIBLE);
        }
    }

    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, Resources.getSystem().getDisplayMetrics());
    }
}
