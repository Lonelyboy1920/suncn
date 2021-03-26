package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 重写GridView，解决与ScrollView兼容性问题
 * 自定义实现拖动通过传值isMove确定是否可拖动，默认不允许拖动
 * implements AdapterView.OnItemClickListener
 */
public class MyGridview extends GridView  {
    private boolean isMove = false;//是否可以移动
    private static final int DRAG_IMG_SHOW = 1;
    private static final int DRAG_IMG_NOT_SHOW = 0;
    private static final String LOG_TAG = "DragGridView";
    private static final float AMP_FACTOR = 1.2f;

    public boolean isMove() {
        return isMove;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    /**
     * 被拖动的视图
     */
    private ImageView dragImageView;
    private WindowManager.LayoutParams dragImageViewParams;
    private WindowManager windowManager;
    private boolean isViewOnDrag = false;

    /**
     * 前一次拖动的位置
     */
    private int preDraggedOverPositon = AdapterView.INVALID_POSITION;
    private int downRawX;
    private int downRawY;
    private OnItemLongClickListener onLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            return true;
        }
    };

    public MyGridview(Context context) {
        super(context);
        initView();
    }

    public MyGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MyGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        // TODO 自动生成的构造函数存根
    }

    public void initView() {
        setOnItemLongClickListener(onLongClickListener);
        //初始化显示被拖动item的image view
        dragImageView = new ImageView(getContext());
        dragImageView.setTag(DRAG_IMG_NOT_SHOW);
        //初始化用于设置dragImageView的参数对象
        dragImageViewParams = new WindowManager.LayoutParams();
        //获取窗口管理对象，用于后面向窗口中添加dragImageView
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO 自动生成的方法存根
        int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //被按下时记录按下的坐标
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //获取触摸点相对于屏幕的坐标
            downRawX = (int) ev.getRawX();
            downRawY = (int) ev.getRawY();
        }
        //dragImageView处于被拖动时，更新dragImageView位置
        else if ((ev.getAction() == MotionEvent.ACTION_MOVE) && (isViewOnDrag == true)) {
            Log.i(LOG_TAG, "" + ev.getRawX() + " " + ev.getRawY());
            //设置触摸点为dragImageView中心
            dragImageViewParams.x = (int) (ev.getRawX() - dragImageView.getWidth() / 2);
            dragImageViewParams.y = (int) (ev.getRawY() - dragImageView.getHeight() / 2);
            //更新窗口显示
            windowManager.updateViewLayout(dragImageView, dragImageViewParams);
            //获取当前触摸点的item position
            int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
            //如果当前停留位置item不等于上次停留位置的item，交换本次和上次停留的item
            if ((currDraggedPosition != AdapterView.INVALID_POSITION) && (currDraggedPosition != preDraggedOverPositon)) {
                preDraggedOverPositon = currDraggedPosition;
            }
        }
        //释放dragImageView
        else if ((ev.getAction() == MotionEvent.ACTION_UP) && (isViewOnDrag == true)) {
            if ((int) dragImageView.getTag() == DRAG_IMG_SHOW) {
                windowManager.removeView(dragImageView);
                dragImageView.setTag(DRAG_IMG_NOT_SHOW);
            }
            isViewOnDrag = false;
        }
        return super.onTouchEvent(ev);
    }
}
