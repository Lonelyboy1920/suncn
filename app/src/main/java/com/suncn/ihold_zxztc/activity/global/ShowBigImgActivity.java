package com.suncn.ihold_zxztc.activity.global;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.bluemobi.dylan.photoview.HackyViewPager;
import cn.bluemobi.dylan.photoview.library.PhotoView;
import cn.bluemobi.dylan.photoview.library.PhotoViewAttacher;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;


/**
 * 查看大图Activity
 */
public class ShowBigImgActivity extends BaseActivity {
    @BindView(id = R.id.big_img_vp)
    private HackyViewPager bigImgVp;//查看HackyViewPager
    private int position;
    private ArrayList<String> paths;
    private Date startDate;//打开网页开始时间
    private Date endDate;//打开网页结束时间
    private String strId = "";

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_show_bigimg);
    }

    @Override
    public void onResume() {
        super.onResume();
        startDate = new Date();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (GIStringUtil.isNotBlank(strId)) {
            endDate = new Date();
            long strDurationDate = (endDate.getTime() - startDate.getTime()) / 1000;
            textParamMap = new HashMap<>();
            textParamMap.put("strId", strId);
            textParamMap.put("strType", "2");
            textParamMap.put("strDurationDate", String.valueOf(strDurationDate));
            doRequestNormal(ApiManager.getInstance().CommonReadRuleServlet(textParamMap), 99);
        }
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {

            }
        };
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        paths = intent.getStringArrayListExtra("paths");
        String title = intent.getStringExtra("title");
        strId = intent.getStringExtra("strId");
        initView();
    }

    public void initView() {
        bigImgVp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return paths == null ? 0 : paths.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                int imagePosition = position;
                View adView = LayoutInflater.from(activity).inflate(R.layout.item_vp_show_bigimg, null);
                PhotoView icon = adView.findViewById(R.id.flaw_img);
                icon.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        finish();
                    }
                });
                icon.setBackgroundColor(getResources().getColor(R.color.black));
                TextView tvCount = adView.findViewById(R.id.tv_count);
                TextView tvSave = adView.findViewById(R.id.tv_save);
                tvCount.setText((imagePosition + 1) + "/" + paths.size() + "");
                GIImageUtil.loadImg(activity, icon, paths.get(imagePosition), 0);
                container.addView(adView);
                tvSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HiPermission.create(activity).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                            @Override
                            public void onGuarantee(String permisson, int position) { // 同意/已授权
                                new AsyncTask<Void, Void, Bitmap>() {
                                    @Override
                                    protected Bitmap doInBackground(Void... params) {
                                        Bitmap bitmap = null;
                                        try {
                                            bitmap = Glide.with(activity)
                                                    .asBitmap()
                                                    .load(paths.get(imagePosition))
                                                    .submit(360, 480).get();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return bitmap;
                                    }

                                    @Override
                                    protected void onPostExecute(Bitmap bitmap) {
                                        GIImageUtil.savaImage(activity, bitmap);
                                    }
                                }.execute();


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
                });
                return adView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (object instanceof PhotoView) {
                    PhotoView s = (PhotoView) object;
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) s.getDrawable();
                    if (bitmapDrawable != null) {
                        Bitmap bm = bitmapDrawable.getBitmap();
                        if (bm != null && !bm.isRecycled()) {
                            s.setImageResource(0);
                            bm.recycle();
                        }
                    }
                }
            }
        });
        bigImgVp.setCurrentItem(position, true);
    }

    private static Bitmap bitmap;

    public static Bitmap getBitmap(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return bitmap;
    }
}
