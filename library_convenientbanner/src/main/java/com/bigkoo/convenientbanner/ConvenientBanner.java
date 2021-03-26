package com.bigkoo.convenientbanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.bigkoo.convenientbanner.adapter.CBPageAdapter;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.CBPageChangeListener;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bigkoo.convenientbanner.view.CBLoopViewPager;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.widget.GITextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 *
 * @author Sai 支持自动翻页
 */
public class ConvenientBanner<T> extends LinearLayout {
    private List<T> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private CBPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime;
    private boolean turning;
    private boolean canTurn = false;
    private boolean manualPageable = true;
    private boolean canLoop = true;
    private List<String> titles;
    private GITextView titleView;
    private GITextView count_TextView;
    private GITextView allCount_TextView;
    private float showHeight = 450;
    private int width = 16;
    private int height = 9;
    private int otherWidth = 71;
    private Context context;
//    private float showScreenHeight;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private AdSwitchTask adSwitchTask;

    public ConvenientBanner(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public void setProportion(int width, int height, int otherWidth) {
        this.width = width;
        this.height = height;
        this.otherWidth = otherWidth;
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        showHeight = a.getDimension(R.styleable.ConvenientBanner_showHeight, 80);
        a.recycle();

        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        showHeight = a.getDimension(R.styleable.ConvenientBanner_showHeight, 70);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        showHeight = a.getDimension(R.styleable.ConvenientBanner_showHeight, 70);
        a.recycle();
        init(context);
    }

    public void init(Context context) {
//        showScreenHeight = getPingMuSize(context);
//        int h = context.getResources().getDisplayMetrics().heightPixels;
//        showHeight = h / (float) 2.5;
        int screenWidth = GIDensityUtil.getScreenW(context);
        showHeight = (screenWidth / width) * height + GIDensityUtil.dip2px(context, otherWidth);

        View hView = LayoutInflater.from(context).inflate(R.layout.include_viewpager, this, true);
        hView.setMinimumHeight((int) showHeight);
        viewPager = (CBLoopViewPager) hView.findViewById(R.id.cbLoopViewPager);
//        DisplayMetrics dm = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(GIDensityUtil.getScreenW(context),
//                ((GIDensityUtil.getScreenW(context)) / 16) * 9);
//        viewPager.setLayoutParams(params);
        loPageTurningPoint = (ViewGroup) hView.findViewById(R.id.loPageTurningPoint);
        titleView = hView.findViewById(R.id.tv_title);
        count_TextView = hView.findViewById(R.id.tv_count);
        allCount_TextView = hView.findViewById(R.id.tv_all_count);
        initViewPagerScroll();
        adSwitchTask = new AdSwitchTask(this);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//    public static float getPingMuSize(Context mContext) {
//        int densityDpi = mContext.getResources().getDisplayMetrics().densityDpi;
//        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
//        float density = mContext.getResources().getDisplayMetrics().density;
//        float xdpi = mContext.getResources().getDisplayMetrics().xdpi;
//        float ydpi = mContext.getResources().getDisplayMetrics().ydpi;
//        int width = mContext.getResources().getDisplayMetrics().widthPixels;
//        int height = mContext.getResources().getDisplayMetrics().heightPixels;
//        // 这样可以计算屏幕的物理尺寸
//        float width2 = (width / xdpi) * (width / xdpi);
//        float height2 = (height / ydpi) * (width / xdpi);
//        return (float) Math.sqrt(width2 + height2);
//    }

    static class AdSwitchTask implements Runnable {

        private final WeakReference<ConvenientBanner> reference;

        AdSwitchTask(ConvenientBanner convenientBanner) {
            this.reference = new WeakReference<ConvenientBanner>(convenientBanner);
        }

        @Override
        public void run() {
            ConvenientBanner convenientBanner = reference.get();
            if (convenientBanner != null) {
                if (convenientBanner.viewPager != null && convenientBanner.turning) {
                    int page = convenientBanner.viewPager.getCurrentItem() + 1;
                    convenientBanner.viewPager.setCurrentItem(page);
                    convenientBanner.postDelayed(convenientBanner.adSwitchTask, convenientBanner.autoTurningTime);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ConvenientBanner setPages(CBViewHolderCreator holderCreator, List<T> datas) {
        this.mDatas = datas;
        pageAdapter = new CBPageAdapter(holderCreator, mDatas);
        viewPager.setAdapter(pageAdapter, canLoop);

        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
        return this;
    }

    public ConvenientBanner setImageTitle(final List<String> titles) {
        this.titles = titles;
        if (onPageChangeListener != null) {
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        } else {
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setNewsTitle(position);
                    for (int i = 0; i < mPointViews.size(); i++) {
                        mPointViews.get(position).setImageResource(page_indicatorId[1]);
                        if (position != i) {
                            mPointViews.get(i).setImageResource(page_indicatorId[0]);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        setNewsTitle(0);
        return this;
    }

    @SuppressWarnings("deprecation")
    private void setNewsTitle(int position) {
        if (titles != null && titles.size() > 0) {
            int currrentPos = position + 1;
            int allPos = titles.size();
            String html = "<font color=#333333> " + titles.get(position) + "</font>";
            String intCount = "<font color=#ef4b39> " + currrentPos + "</font>";

            titleView.setText(Html.fromHtml(html));
            count_TextView.setText(Html.fromHtml(intCount));
            allCount_TextView.setText("/" + allPos);
        }
    }

    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 底部指示器资源图片
     *
     * @param page_indicatorId
     */
    public ConvenientBanner setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (mDatas == null) return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(10, 10);
            params.setMargins(5, 0, 5, 0);
            pointView.setLayoutParams(params);
            if (mPointViews.isEmpty())
                pointView.setBackgroundResource(page_indicatorId[1]);
            else
                pointView.setBackgroundResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
//        pageChangeListener = new CBPageChangeListener(mPointViews,
//                page_indicatorId);
//        pageChangeListener.setTitles(titles);
//        viewPager.setOnPageChangeListener(pageChangeListener);
//        pageChangeListener.onPageSelected(viewPager.getRealItem());
//        if (onPageChangeListener != null)
//            pageChangeListener.setOnPageChangeListener(onPageChangeListener);

        return this;
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
      /*  RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
//        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, align == PageIndicatorAlign.CENTER_VERTICAL ? RelativeLayout.TRUE : 0);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        loPageTurningPoint.setLayoutParams(layoutParams);*/
        return this;
    }

    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public ConvenientBanner startTurning(long autoTurningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(ViewPager.PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }


    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(
                    viewPager.getContext());
            mScroller.set(viewPager, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanScroll(manualPageable);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurn) stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    //获取当前的页面index
    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getRealItem();
        }
        return -1;
    }

    //设置当前的页面index
    public void setcurrentitem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置翻页监听器
     *
     * @param onPageChangeListener
     * @return
     */
    public ConvenientBanner setOnPageChangeListener(ViewPager.OnPageChangeListener
                                                            onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else viewPager.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            viewPager.setOnItemClickListener(null);
            return this;
        }
        viewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    public CBLoopViewPager getViewPager() {
        return viewPager;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        viewPager.setCanLoop(canLoop);
    }
}
