package com.qd.longchat.activity;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDBucketAdapter;
import com.qd.longchat.adapter.QDPhotoSelectAdapter;
import com.qd.longchat.model.QDAlbum;
import com.qd.longchat.model.QDPhoto;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDPhotoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

import com.qd.longchat.R2;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/8 下午5:37
 */
public class QDSelectPhotoActivity extends QDBaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R2.id.view_ps_title)
    View viewTitle;
    @BindView(R2.id.grid_view)
    GridView gridView;
    @BindView(R2.id.list_view)
    ListView listView;
    @BindView(R2.id.back_img)
    ImageView img;
    @BindView(R2.id.btn_sure)
    Button btnSure;
    @BindView(R2.id.btn_edit)
    Button btnEdit;
    @BindView(R2.id.tv_artwork)
    TextView tvArtwork;
    @BindView(R2.id.rl_bottom_layout)
    RelativeLayout rlBottomLayout;

    @BindString(R2.string.photo_select_all_pic)
    String strAllPic;
    @BindString(R2.string.contact_sure)
    String strSure;

    @BindDrawable(R2.drawable.ic_round_unselected)
    Drawable drawUnselected;
    @BindDrawable(R2.drawable.ic_round_selected)
    Drawable drawSelected;

    private List<QDPhoto> photoList;
    private List<QDAlbum> albumList;
    private QDPhotoSelectAdapter adapter;
    private QDBucketAdapter bucketAdapter;
    private List<String> selectedPhotoList;
    private boolean isSingle;
    private boolean isArtwork;

    private QDPhotoSelectAdapter.OnPhotoSelectedListener listener = new QDPhotoSelectAdapter.OnPhotoSelectedListener() {
        @Override
        public void onPhotoSelected(String path, boolean isAdd) {
            if (isAdd) {
                selectedPhotoList.add(path);
            } else {
                selectedPhotoList.remove(path);
            }
            updateBottomLayout();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true, true);
        setContentView(R.layout.activity_photo_select);
        ButterKnife.bind(this);
        findViewById(R.id.tv_spinner).setVisibility(View.VISIBLE);
        //请求授权
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                isSingle = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_SINGLE, false);
                if (isSingle) {
                    rlBottomLayout.setVisibility(View.GONE);
                } else {
                    rlBottomLayout.setVisibility(View.VISIBLE);
                }
                initTitleView(viewTitle);
                tvTitleName.setText(strAllPic);

                selectedPhotoList = new ArrayList<>();
                getLoaderManager().initLoader(0, null, QDSelectPhotoActivity.this);
                findViewById(R.id.ll_title).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listView.getVisibility() == View.GONE) {
                            listView.setVisibility(View.VISIBLE);
                            img.setVisibility(View.VISIBLE);
                        } else {
                            listView.setVisibility(View.GONE);
                            img.setVisibility(View.GONE);
                        }
                    }
                });

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listView.getVisibility() == View.VISIBLE) {
                            listView.setVisibility(View.GONE);
                            img.setVisibility(View.GONE);
                        }
                    }
                });

                btnSure.setBackgroundResource(R.drawable.ic_rounded_rectangle_gray_btn);
                btnSure.setEnabled(false);
            }

            @Override
            public void onClose() { // 用户关闭权限申请
            }

            @Override
            public void onFinish() { // 所有权限申请完成
            }

            @Override
            public void onDeny(String permisson, int position) { // 拒绝
            }
        });

    }

    @OnItemClick({R2.id.grid_view, R2.id.list_view})
    public void onItemClick(AdapterView<?> parent, int position) {
        int i = parent.getId();
        if (i == R.id.grid_view) {
            if (isSingle) {
                Intent intent = new Intent();
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, photoList.get(position).getPath());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                List<String> pathList = new ArrayList<>();
                for (QDPhoto photo : photoList) {
                    pathList.add(photo.getPath());
                }
                Intent intent = new Intent(context, QDPicShowActivity.class);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_LIST, (ArrayList<String>) selectedPhotoList);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, isArtwork);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, position);
                startActivityForResult(intent, 10000);
            }

        } else if (i == R.id.list_view) {
            tvTitleName.setText(albumList.get(position).getName());
            photoList = albumList.get(position).getPhotoList();
            adapter.setPhotoList(photoList);
            listView.setVisibility(View.GONE);
            img.setVisibility(View.GONE);

        }
    }

    @OnClick({R2.id.btn_sure, R2.id.btn_edit, R2.id.tv_artwork})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_sure) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) selectedPhotoList);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, isArtwork);
            setResult(RESULT_OK, intent);
            finish();
        } else if (view.getId() == R.id.tv_artwork) {
            if (isArtwork) {
                tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawUnselected, null, null, null);
                isArtwork = false;
            } else {
                tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawSelected, null, null, null);
                isArtwork = true;
            }
        } else {
            Intent intent = new Intent(context, QDGraffitiActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IMAGE_PATH, selectedPhotoList.get(0));
            startActivityForResult(intent, 1000);
        }
    }

    private void updateBottomLayout() {
        int size = selectedPhotoList.size();
        if (size > 0) {
            if (size == 1) {
                btnEdit.setVisibility(View.VISIBLE);
            } else {
                btnEdit.setVisibility(View.GONE);
            }
            btnSure.setBackgroundResource(R.drawable.ic_rounded_rectangle_btn);
            btnSure.setEnabled(true);
            btnSure.setText(strSure + " (" + selectedPhotoList.size() + ")");
        } else {
            btnSure.setBackgroundResource(R.drawable.ic_rounded_rectangle_gray_btn);
            btnSure.setEnabled(false);
            btnSure.setText(strSure);
            btnEdit.setVisibility(View.GONE);
        }
    }

    private void test(String path) {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int sWidth = outMetrics.widthPixels;
        int sHeight = outMetrics.heightPixels;

        float scale;
        if (bWidth >= bHeight) {
            scale = sWidth / bWidth;
        } else {
            scale = sHeight / bHeight;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);

        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bWidth, bHeight, matrix, true);

        ViewGroup decorView = getWindow().getDecorView().findViewById(android.R.id.content);

        ImageView imageView = new ImageView(this);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setImageBitmap(b);
        imageView.setLayoutParams(params);
        decorView.addView(imageView);
        imageView.startAnimation(animation);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDLog.e("111", "2222");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000) {
            String path = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_IMAGE_PATH);
            selectedPhotoList.clear();
            selectedPhotoList.add(path);
            Intent intent = new Intent();
            intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) selectedPhotoList);
            setResult(RESULT_OK, intent);
            finish();
        } else if (resultCode == RESULT_OK && requestCode == 10000) {
            List<String> pathList = data.getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS);
            boolean isFinish = data.getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FINISH, false);
            isArtwork = data.getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, false);
            if (isFinish) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                if (isArtwork) {
                    tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawSelected, null, null, null);
                } else {
                    tvArtwork.setCompoundDrawablesWithIntrinsicBounds(drawUnselected, null, null, null);
                }
                selectedPhotoList.clear();
                selectedPhotoList.addAll(pathList);
                adapter.notifyDataSetChanged();
                updateBottomLayout();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursor = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DATE_ADDED + " desc");
        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            int count = data.getCount();
            if (count <= 0) {
                data.close();
            } else {
                photoList = QDPhotoUtil.initMobileAlbum(data);
                adapter = new QDPhotoSelectAdapter(this, photoList, selectedPhotoList, isSingle);
                adapter.setListener(listener);
                gridView.setAdapter(adapter);


                albumList = new ArrayList<>();
                for (Map.Entry entry : QDPhotoUtil.bucketMap.entrySet()) {
                    albumList.add((QDAlbum) entry.getValue());
                }
                bucketAdapter = new QDBucketAdapter(this, albumList);
                listView.setAdapter(bucketAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
