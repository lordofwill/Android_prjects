package com.geos.colorlegendapplied;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorLegend extends View implements GestureDetector.OnGestureListener {
    private Context _context;
    private int _width = 0; //뷰크기를 결정하는 변수
    private int _height = 0; //뷰크기를 결정하는 변수
    private float _mWidth; //사이즈 변동시 뷰 크기
    private float _mHeight; //사이즈 변동시 뷰 크기
    private float _legendCornerRadius;//뷰의 모서리 반지름
    private int _legendBackgroundColor;//뷰의 배경색
    private float _colorBoxWidth = 40f;//
    private float _colorBoxHeight = 40f;//
    private float _titleFontSize = 40f;//
    private int _titleFontColor = Color.WHITE;
    private int _toggleImageSrc=0;//토글 이미지 상대 주소
    private float _toggleImageScale=1.0f;//토글 이미지의 범례색상과 비교 배율
//    private int _toggleMarkColor = Color.WHITE;//토글 스위치 표시의 색
//    private float _toggleMarkRadius = 6.0f;//토글 스위치 표시의 크기
//    private float _toggleMarkStrokeWidth = 6.0f;//토글 스위치 선의 굵기
    private float _paddingLeft = 0;
    private float _paddingRight = 0;
    private float _paddingTop = 0;
    private float _paddingBottom = 0;
    private float _boxTextGap = 0;
    private float _itemGap = 0; //범례간의 거리
    private float _legendContent = 0; //범례 내용물의 넓이 계산 결과

    private GestureDetectorCompat _gDetector; //온탭등의 사용자 액션 감지
    private List<ColorLegendItem> _legend = new ArrayList<>();//item을 받는 곳
    private List<RectF> _colorboxRect = new ArrayList<>();//colorbox의 좌표를 받는 곳
    private Paint _titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //범례 제목 페인트
    private Paint _colorBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 범례 상자 페인트
//    private Paint _togglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//토글스위치 페인트
    private Drawable _drawable; //토글스위치의 이미지를 그리는 페인트
    private final float[] TEMPRESULT = new float[2]; //임시저장용이며 이미지작업의 자원낭비 감소
    private List<OnMyTapListener> _listeners = new ArrayList<>();//뷰와 메인액티비티의 이동을 위한 리스너 리스트
    //화면 계산을 위해 쓰이는 것
    private DisplayMetrics _dm = new DisplayMetrics();
    private WindowManager _windowManager = (WindowManager) getContext().getApplicationContext().getSystemService(_context.WINDOW_SERVICE);

    //.

    //생성자
    public ColorLegend(Context context) {
        super(context);
        init(context);
    }

    public ColorLegend(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray _attribute = context.obtainStyledAttributes(attrs, R.styleable.ColorLegend);
            _colorBoxWidth = _attribute.getDimension(R.styleable.ColorLegend_color_box_width, 40f);
            _colorBoxHeight = _attribute.getDimension(R.styleable.ColorLegend_color_box_height, 40f);
            _titleFontSize = _attribute.getDimension(R.styleable.ColorLegend_title_font_size, 40f);
            _titleFontColor = _attribute.getInt(R.styleable.ColorLegend_title_font_color, Color.WHITE);
            _boxTextGap = _attribute.getDimension(R.styleable.ColorLegend_gap_box_title, 0);
            _itemGap = _attribute.getDimension(R.styleable.ColorLegend_gap_items, 0);
//            _toggleMarkColor = _attribute.getInt(R.styleable.ColorLegend_toggle_circle_color, Color.WHITE);
//            _toggleMarkRadius = _attribute.getDimension(R.styleable.ColorLegend_toggle_circle_size, 6.0f);
//            _toggleMarkStrokeWidth = _attribute.getDimension(R.styleable.ColorLegend_toggle_circle_strokeWidth, 6.0f);
            _toggleImageSrc = _attribute.getResourceId(R.styleable.ColorLegend_toggle_image_src, 0);
            _legendCornerRadius = _attribute.getDimension(R.styleable.ColorLegend_cover_corner_radius,0);
            _legendBackgroundColor = _attribute.getInt(R.styleable.ColorLegend_cover_color, 0x9c000000);
            _toggleImageScale = _attribute.getFloat(R.styleable.ColorLegend_toggle_image_scale, 1.0f);
        }

        init(context);
    }

    public ColorLegend(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    //.

    //데이터 처리부
    public void addItem(ColorLegendItem item) {
        _legend.add(item);
    }

    public ColorLegendItem getItem(int index) {
        ColorLegendItem _item = _legend.get(index);
        return _item;
    }
    //.

    //크기조절부

    public float get_colorBoxWidth() {
        return _colorBoxWidth;
    }

    public void set_colorBoxWidth(float colorBoxWidth) {
        this._colorBoxWidth = colorBoxWidth;
    }

    public float get_colorBoxHeight() {

        return _colorBoxHeight;
    }

    public void set_colorBoxHeight(float colorBoxHeight) {
        this._colorBoxHeight = colorBoxHeight;
    }

    public float get_titleFontSize() {

        return _titleFontSize;
    }

    public void set_titleFontSize(float titleFontSize) {
        this._titleFontSize = titleFontSize;
    }

    public float get_titleFontColor() {

        return _titleFontColor;
    }

    public void set_titleFontColor(int titleFontColor) {
        this._titleFontColor = titleFontColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int _widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int _widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int _heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int _heightSize = MeasureSpec.getSize(heightMeasureSpec);
        _titlePaint.setTextSize(_titleFontSize);

        _paddingLeft =getPaddingLeft();
        _paddingRight =getPaddingRight();
        _paddingTop =getPaddingTop();
        _paddingBottom =getPaddingBottom();

//        Log.d("^^1",""+legendContent);//matchparent 일 때만 값이 이상하게 변동된다. 값이 입력 될 경우는 오히려 제대로 작동

        if (_widthMode == MeasureSpec.AT_MOST) {
            _legendContent = getContendWidth();
            _width = (int) _legendContent +/*(Legend.size()-1)*/(int) _itemGap + (int) _paddingLeft + (int) _paddingLeft;

        } else if (_widthMode == MeasureSpec.EXACTLY) {
            _width = _widthSize;
        } else {
            _width = getDeviceWidth();
        }

        if (_heightMode == MeasureSpec.AT_MOST) {
            _height = (int) _colorBoxHeight + (int) _paddingTop + (int) _paddingBottom;
        } else if (_heightMode == MeasureSpec.EXACTLY) {
            _height = _heightSize;
        } else {
            _height = 60;
        }
        setMeasuredDimension(_width, _height);
    }

    //연산부
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        _mWidth = w;
        _mHeight = h;
    }

    private float[] computeXYForPosition(int index) {//그림위치 결정을 위한 좌표 계산부
        float[] _result = TEMPRESULT;
        _legendContent = getContendWidth();
        float _blankW = (_mWidth - (_legendContent + _paddingLeft + _paddingRight)) / (_legend.size() + 1);//가로 공백 계산 변수
        float _blankH = (_mHeight - (_paddingTop + _paddingBottom + _colorBoxHeight)) / 2;//세로 공백 계산 변수


        for (int i = 0; i <= index; i++) {
            if (i == 0) {
                _result[0] = _blankW;
            } else {
                _result[0] += (float) _colorBoxWidth + _titlePaint.measureText(getItem(i - 1).getTitle()) + _blankW + _boxTextGap + _itemGap;
            }
        }
        _result[0] += _paddingLeft;
        _result[1] = _blankH + _paddingTop;
        return _result;
    }

    public int getDeviceWidth() {//전체 회면의 넓이를 계산하는 메소드
        _windowManager.getDefaultDisplay().getRealMetrics(_dm);
//        int _getDpi = _dm.densityDpi;
        int _width = _dm.heightPixels;
        return _width;
    }

    public int getContendWidth() {//범례의 글자와 상자의 총 넓이를 구한다
        int _legendCont = 0;
        for (int i = 0; i < _legend.size(); i++) {
            _legendCont += _colorBoxWidth + _titlePaint.measureText(getItem(i).getTitle()) + _boxTextGap;
        }
        _legendCont += (_legend.size() - 1) * _itemGap;
        return _legendCont;
    }
    //.

    //기능부
    private void init(Context context) {
        this._gDetector = new GestureDetectorCompat(context, this);

            //겉상자 모서리의 곡률 및 배경색 결정부분
            float[] _outerRadii = new float[8];
            Arrays.fill(_outerRadii, _legendCornerRadius);
            ShapeDrawable _roundedRect = new ShapeDrawable(new RoundRectShape(_outerRadii, null, null));
            _roundedRect.getPaint().setColor(_legendBackgroundColor);
            setBackground(_roundedRect);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(true);//롤리팝 이전 버전에서는 지원되지 않는다고 한다.
            //.
        }
    }

    public void setOnMyTapListener (OnMyTapListener listener) {
        _listeners.add(listener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        _togglePaint.setStyle(Paint.Style.STROKE);
//        _togglePaint.setStrokeWidth(_toggleMarkStrokeWidth);
//        _togglePaint.setColor(_toggleMarkColor);
        _colorBoxPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        _titlePaint.setColor(_titleFontColor);
        _titlePaint.setTextSize(_titleFontSize);
        _titlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if(_toggleImageSrc !=0)
            _drawable = getResources().getDrawable(_toggleImageSrc);


        for (int i = 0; i < _legend.size(); i++) {
            _colorBoxPaint.setColor(getItem(i).getColor());
            float[] _xyData = computeXYForPosition(i);
            float _x = _xyData[0];
            float _y = _xyData[1];
            RectF rect = new RectF(_x, _y, _x + _colorBoxWidth, _y + _colorBoxHeight);
            canvas.drawRect(rect, _colorBoxPaint);
            rect = new RectF(_x, _y, _x + _colorBoxWidth + _boxTextGap + _titlePaint.measureText(getItem(i).getTitle()), _y + _colorBoxHeight);
            _colorboxRect.add(rect);
            canvas.drawText(getItem(i).getTitle(), _x + _colorBoxWidth + _boxTextGap, _y + _colorBoxHeight /2 + _titleFontSize /3 , _titlePaint);

            if(getItem(i).getToggleSwitch()==true&&_toggleImageSrc !=0){
                //표시를 image file로 넣을 시
                    Rect picrect = new Rect((int) (_x + _colorBoxWidth * (1 - _toggleImageScale) / 2), (int) (_y + _colorBoxHeight * (1 - _toggleImageScale) / 2), (int) (_x + _colorBoxWidth * (1 + _toggleImageScale) / 2), (int) (_y + _colorBoxHeight * (1 + _toggleImageScale) / 2));
                    _drawable.setBounds(picrect);
                    _drawable.draw(canvas);
                //표시를 도형으로 넣을 시
//                canvas.drawCircle(_x+ColorBoxWidth/2,_y,ToggleMarkRadius,TogglePaint);
            }

        }

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        int _x = (int) motionEvent.getX();
        int _y = (int) motionEvent.getY();

        for(int i = 0; i< _colorboxRect.size(); i++) {

            if(_colorboxRect.get(i).contains(_x, _y) ) {

                for(OnMyTapListener listener: _listeners) {
                    if(getItem(i).getToggleSwitch()==true)
                    getItem(i).setToggleSwitch(false);
                    else
                        getItem(i).setToggleSwitch(true);
                    listener.onTap(getItem(i));
                    invalidate();
                }
                return false;
            }
        }
            return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this._gDetector.onTouchEvent(event);
    }
}
