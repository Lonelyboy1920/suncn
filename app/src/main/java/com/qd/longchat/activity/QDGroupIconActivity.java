package com.qd.longchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDGroupIconAdapter;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDResourceUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/19 下午2:15
 */
public class QDGroupIconActivity extends QDBaseActivity {

    private final static int REQUEST_SELECT_PHOTO = 1006;
    private final static int REQUEST_CAMERA = 1007;
    private final static int REQUEST_CLIPPING = 1008;

    @BindView(R2.id.view_gi_title)
    View viewTitle;
    @BindView(R2.id.iv_gi_icon)
    ImageView ivIcon;
    @BindView(R2.id.view_gi_list)
    GridView lvList;
    @BindView(R2.id.btn_gi_sure)
    Button btnSure;

    @BindString(R2.string.text_camera)
    String strCamera;
    @BindString(R2.string.text_album)
    String strAlbum;
    @BindString(R2.string.group_icon_title)
    String strTitle;

    private QDGroupIconAdapter adapter;

    private QDAlertView alertView;

    private File cameraFile;

    private String tempPath;

    private String[] iconPaths;

    private Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_icon);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        iconPaths = new String[7];
        adapter = new QDGroupIconAdapter(context);
        lvList.setAdapter(adapter);
        initAlert();
        saveBitmap();
    }

    @OnClick(R2.id.btn_gi_sure)
    public void onClick() {
        Intent intent = new Intent();
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, tempPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnItemClick(R2.id.view_gi_list)
    public void OnItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            alertView.show();
        } else {
            adapter.setIndex(position);
            ivIcon.setImageResource(QDResourceUtil.getMipmapId(context, "ic_group_" + position));
            tempPath = iconPaths[position - 1];
        }

    }

    private QDAlertView.OnStringItemClickListener listener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (position == 1) {
                Intent intent = new Intent(context, QDSelectPhotoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_SINGLE, true);
                startActivityForResult(intent, REQUEST_SELECT_PHOTO);
            } else {
                if (AndPermission.hasPermissions(context, Permission.Group.CAMERA)) {
                    String path = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
                    cameraFile = new File(path);
                    if (cameraFile.exists()) {
                        cameraFile.delete();
                    }
                    QDUtil.startTakePhoto(QDGroupIconActivity.this, REQUEST_CAMERA, cameraFile);
                } else {
                    QDUtil.getPermission(context, Permission.Group.CAMERA);
                }
            }
        }
    };

    private void initAlert() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strCamera, strAlbum)
                .setOnStringItemClickListener(listener)
                .setDismissOutside(true)
                .build();
    }

    private void saveBitmap() {
        for (int i=1; i<8; i++) {
            String path = QDStorePath.IMG_PATH + "group_icon_" + i + ".png";
            File file = new File(path);
            if (!file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), QDResourceUtil.getMipmapId(context, "ic_group_" + i));
                QDUtil.savePhotoToSD(bitmap, path);
            }
            iconPaths[i - 1] = path;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SELECT_PHOTO:
                String filePath = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
                tempPath = QDUtil.startPhotoZoom(QDGroupIconActivity.this, new File(filePath), REQUEST_CLIPPING);
                break;
            case REQUEST_CAMERA:
                tempPath = QDUtil.startPhotoZoom(QDGroupIconActivity.this, cameraFile, REQUEST_CLIPPING);
                break;
            case REQUEST_CLIPPING:
                if (cameraFile != null)
                    cameraFile.deleteOnExit();
                if (mBitmap != null) {
                    mBitmap.recycle();
                }
                mBitmap = QDBitmapUtil.getInstance().createAvatarBitmap(tempPath);
                ivIcon.setImageBitmap(mBitmap);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }
}
