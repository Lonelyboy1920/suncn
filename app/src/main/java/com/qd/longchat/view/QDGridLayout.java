package com.qd.longchat.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDGridViewHolder;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xxw on 2016/7/15.
 */
public class QDGridLayout {

    public interface GridItemClickListener {
        void onGridItemClickListener(int mode, int index, boolean isDeleteItem);
    }

    public final static int PAGE_DEFAULT_SMILEY = 0;
    public final static int PAGE_CUSTOM_SMILEY = 1;
    public final static int PAGE_FUNCTION = 2;
    public final static int PAGE_GIF = 3;
    public final static int PAGE_BOTTOM = 4;

    private Context context;

    private View view;
    private List<Drawable> images;
    private List<GifDrawable> gifImages;
    private List<String> infos;

    private int pageMode;
    private int grid_col;
    private int grid_row;
    private int pageItemCount;

    private QDWrapContentViewPager viewPager;
    private LinearLayout llPageIndex;

    private List<ImageView> indexViewList;

    private PageAdapter pageAdapter;

    private int pageCount;
    private GridItemClickListener listener;

    public QDGridLayout(Context context, int mode, List<Drawable> images, @Nullable List<String> infos) {
        this.context = context;
        this.images = images;
        this.infos = infos;
        this.pageMode = mode;
        init();
    }

    public QDGridLayout(Context context, int mode, List<GifDrawable> images) {
        this.context = context;
        this.gifImages = images;
        this.pageMode = mode;
        init();
    }

    public void setOnGridItemClickListener(GridItemClickListener listener) {
        this.listener = listener;
    }

    public View getGridView() {
        return view;
    }

    private void setColRow(int col, int row) {
        this.grid_col = col;
        this.grid_row = row;
        if (pageMode == PAGE_DEFAULT_SMILEY) {
            pageItemCount = col * row - 1;
        } else {
            pageItemCount = col * row;
        }
    }

    private void init() {
        view = LayoutInflater.from(context).inflate(R.layout.im_smiley_fun_page, null);
        viewPager = view.findViewById(R.id.vp_smiley_fun_pager);
        llPageIndex = view.findViewById(R.id.ll_page_index);
        initPageParam();
        initPoint();
    }

    public void initPageParam() {
        switch (pageMode) {
            case PAGE_DEFAULT_SMILEY: {
                setColRow(7, 4);
            }
            break;
            case PAGE_CUSTOM_SMILEY: {
                int rowCount = images.size() % 4 == 0 ? (images.size() / 4) : (images.size() / 4 + 1);
                setColRow(4, rowCount);
            }
            break;
            case PAGE_FUNCTION: {
                int rowCount = images.size() % 4 == 0 ? (images.size() / 4) : (images.size() / 4 + 1);
                setColRow(4, 2);
            }
            break;
            case PAGE_GIF: {
                setColRow(4, 2);
            }
            break;
            case PAGE_BOTTOM: {
                setColRow(images.size(), 1);
            }
            default:
                break;
        }
        initPageCount();
        pageAdapter = new PageAdapter();
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                drawPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void initPageCount() {
        int length;
        if (pageMode == PAGE_GIF) {
            length = gifImages.size();
        } else {
            length = images.size();
        }
        if (length % pageItemCount == 0) {
            pageCount = length / pageItemCount;
        } else {
            pageCount = length / pageItemCount + 1;
        }
    }

    private void initPoint() {
        indexViewList = new ArrayList<>();
        llPageIndex.removeAllViews();
        ImageView imageView;

        if (pageCount < 2) {
            return;
        }
        for (int i = 0; i < pageCount; i++) {
            imageView = new ImageView(context);
            int size = QDUtil.dp2px((Activity) context, 7);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = QDUtil.dp2px((Activity) context, 9);
            layoutParams.topMargin = QDUtil.dp2px((Activity) context, 9);
            layoutParams.bottomMargin = QDUtil.dp2px((Activity) context, 9);
            layoutParams.rightMargin = QDUtil.dp2px((Activity) context, 9);
            llPageIndex.addView(imageView, layoutParams);
            indexViewList.add(imageView);
        }
        drawPoint(0);
    }

    private void drawPoint(int index) {
        for (ImageView view : indexViewList) {
            view.setBackgroundResource(R.drawable.im_smiley_fun_index_page_unselected);
        }
        indexViewList.get(index).setBackgroundResource(R.drawable.im_smiley_fun_index_page_selected);
    }

    public class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.im_smiley_fun_grid, null, true);
            container.addView(view, 0);
            QDAdaptiveGridView gridView = view.findViewById(R.id.gv_smiley_fun_grid);
            gridView.setNumColumns(grid_col);
            final GridAdapter gridAdapter = new GridAdapter(position);
            gridView.setAdapter(gridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int index = gridAdapter.getItemIndex(i);
                    if (index == -1) {
                        if (listener != null) {
                            listener.onGridItemClickListener(pageMode, index, true);
                        }
                    } else {
                        if (listener != null) {
                            listener.onGridItemClickListener(pageMode, index, false);
                        }
                    }
                }
            });
            return view;
        }

        //destroyItem必须重载,并且注释掉super
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //下面这一行必须注释掉,否则将会报UnsupportedOperationException
            //super.destroyItem(container, position, object);
        }
    }

    public class GridAdapter extends BaseAdapter {

        private int pageIndex;

        public GridAdapter(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        @Override
        public int getCount() {
            return grid_col * grid_row;
        }

        @Override
        public Drawable getItem(int i) {
            if (pageMode == PAGE_DEFAULT_SMILEY) {
                if (i == getCount() - 1) {
                    return context.getResources().getDrawable(R.mipmap.im_smiley_delete);
                }
            }
            int index = getItemIndex(i);
            if (index < images.size()) {
                return images.get(index);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public int getItemIndex(int i) {
            if (pageMode == PAGE_DEFAULT_SMILEY) {
                if (i == getCount() - 1) {
                    return -1;
                }
                return pageIndex * getCount() + i - pageIndex;
            }
            return pageIndex * getCount() + i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            QDGridViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.im_grid_item, null);
                holder = QDGridViewHolder.initView(view);
                view.setTag(holder);
            } else {
                holder = (QDGridViewHolder) view.getTag();
            }

            if (pageMode == PAGE_GIF) {
                holder.gridImage.setBackground(gifImages.get(getItemIndex(i)));
                int pad = QDUtil.dp2px((Activity) context, 15);
                holder.gridImage.setPadding(0, pad, 0, pad);
            } else {
                Drawable dr = getItem(i);
                if (dr != null) {
                    holder.gridImage.setImageDrawable(getItem(i));
                    if (pageMode == PAGE_DEFAULT_SMILEY || pageMode == PAGE_BOTTOM) {
                        int pad = QDUtil.dp2px((Activity) context, 6);
                        holder.gridImage.setPadding(0, pad, 0, pad);
                    } else if (pageMode == PAGE_FUNCTION) {
                        int pad = QDUtil.dp2px((Activity) context, 15);
                        holder.gridImage.setPadding(0, pad, 0, pad);
                    }
                }
            }

            if (infos == null) {
                holder.gridInfo.setVisibility(View.GONE);
            } else {
                int index = getItemIndex(i);
                if (index < infos.size()) {
                    holder.gridInfo.setText(infos.get(index));
                    holder.gridInfo.setVisibility(View.VISIBLE);
                }
            }
            return view;
        }
    }

}
