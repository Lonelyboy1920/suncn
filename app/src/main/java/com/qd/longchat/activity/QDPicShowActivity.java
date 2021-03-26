package com.qd.longchat.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.adapter.QDPicShowAdapter;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/21 下午3:14
 */
public class QDPicShowActivity extends QDBaseActivity {

    @BindView(R2.id.pic_pager)
    ViewPager viewPager;
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.iv_select)
    ImageView ivSelect;
    @BindView(R2.id.view_title)
    View viewTitle;
    @BindView(R2.id.rl_bottom_layout)
    View viewBottom;
    @BindView(R2.id.tv_artwork)
    TextView tvArtwork;
    @BindView(R2.id.btn_edit)
    TextView tvEdit;
    @BindView(R2.id.btn_sure)
    TextView tvSend;
    @BindView(R2.id.tv_title_name)
    TextView tvTitleName;

    @BindString(R2.string.chat_send)
    String strSend;
    @BindDrawable(R2.drawable.ic_round_unselected)
    Drawable drawUnselected;
    @BindDrawable(R2.drawable.ic_round_selected)
    Drawable drawSelected;

    private QDPicShowAdapter showAdapter;
    private List<String> pathList;
    private List<String> selectedList;
    private boolean isArtwork;
    private int index;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private QDPicActivity.OnItemClickListener itemClickListener = new QDPicActivity.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (viewTitle.getVisibility() == View.VISIBLE) {
                viewTitle.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
            } else {
                viewTitle.setVisibility(View.VISIBLE);
                viewBottom.setVisibility(View.VISIBLE);
            }
         }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.view_pic_show);
        ButterKnife.bind(this);

        selectedList = new ArrayList<>();
        pathList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS);
        selectedList.addAll(getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_LIST));
        isArtwork = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, false);

        index = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, 0);
        showAdapter = new QDPicShowAdapter(context, pathList);
        showAdapter.setItemClickListener(itemClickListener);
        viewPager.setAdapter(showAdapter);
        viewPager.setCurrentItem(index);

        initUI();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                initUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initUI() {
        tvTitleName.setText((index + 1) + "/" + pathList.size());

        if (isArtwork) {
            tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawSelected, null, null, null);
        } else {
            tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawUnselected, null, null, null);
        }

        String path = pathList.get(index);
        if (selectedList.contains(path)) {
            ivSelect.setImageResource(R.drawable.ic_round_selected);
        } else {
            ivSelect.setImageResource(R.drawable.ic_round_unselected);
        }
        initSend();
    }

    private void initSend() {
        if (selectedList.size() == 0) {
            tvSend.setText(strSend);
        } else {
            tvSend.setText(strSend + "(" + selectedList.size() + ")");
        }

        if (selectedList.size() <= 1) {
            tvEdit.setVisibility(View.VISIBLE);
        } else {
            tvEdit.setVisibility(View.GONE);
        }

    }

    @OnClick({R2.id.iv_select, R2.id.tv_artwork, R2.id.iv_back, R2.id.btn_sure, R2.id.btn_edit})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_select) {
            String path = pathList.get(index);
            if (selectedList.contains(path)) {
                ivSelect.setImageResource(R.drawable.ic_round_unselected);
                selectedList.remove(path);
            } else {
                if (selectedList.size() == 9) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.photo_select_had_nine_pic));
                    return;
                }
                ivSelect.setImageResource(R.drawable.ic_round_selected);
                selectedList.add(path);
            }
            initSend();

        } else if (i == R.id.tv_artwork) {
            if (isArtwork) {
                tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawUnselected, null, null, null);
                isArtwork = false;
            } else {
                tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawSelected, null, null, null);
                isArtwork = true;
            }

        } else if (i == R.id.iv_back) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) selectedList);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, isArtwork);
            setResult(RESULT_OK, intent);
            finish();

        } else if (i == R.id.btn_sure) {
            if (selectedList.size() == 0) {
                selectedList.add(pathList.get(index));
            }
            Intent intent1 = new Intent();
            intent1.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) selectedList);
            intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, isArtwork);
            intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FINISH, true);
            setResult(RESULT_OK, intent1);
            finish();

        } else if (i == R.id.btn_edit) {
            Intent intent2 = new Intent(context, QDGraffitiActivity.class);
            intent2.putExtra(QDIntentKeyUtil.INTENT_KEY_IMAGE_PATH, pathList.get(index));
            startActivityForResult(intent2, 1000);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000) {
            String path = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_IMAGE_PATH);
            selectedList.clear();
            selectedList.add(path);
            Intent intent = new Intent();
            intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) selectedList);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, isArtwork);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FINISH, true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
