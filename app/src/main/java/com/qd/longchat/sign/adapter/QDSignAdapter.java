package com.qd.longchat.sign.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.model.gd.QDSignModel;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.sign.holder.QDSignHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/24 下午4:32
 */
public class QDSignAdapter extends BaseAdapter {

    private Context context;
    private List<QDSignModel.ListBean> beanList;
    private boolean isMine;
    private LruCache<String, Bitmap> cache;

    public QDSignAdapter(Context context, List<QDSignModel.ListBean> beanList, boolean isMine) {
        this.context = context;
        this.beanList = beanList;
        this.isMine = isMine;
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        cache = new LruCache<>(cacheSize);
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final QDSignHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.im_item_record, null);
            holder = new QDSignHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDSignHolder) convertView.getTag();
        }
        QDSignModel.ListBean bean = beanList.get(position);
        String userId = bean.getCreateUid();
        String userName = bean.getCreateUname();
        String note = bean.getNote();
        if (TextUtils.isEmpty(note)) {
            holder.tvItemNote.setVisibility(View.GONE);
        } else {
            holder.tvItemNote.setVisibility(View.VISIBLE);
//            String note = QDUtil.decodeString(signNote.replaceAll(" ", "+")); //replaceAll方法解决解码中文乱码
            holder.tvItemNote.setText(context.getString(R.string.string_remarks) + ": " + note);
        }
        long time = bean.getCreateTime();
        String address = bean.getArea();
        List<QDSignModel.ListBean.RecvsBean> recvsBeans = bean.getRecvs();
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.string_inform_people)+": ");
        for (QDSignModel.ListBean.RecvsBean recvsBean : recvsBeans) {
            sb.append(recvsBean.getUserName()).append(",");
        }
        if (isMine) {
            holder.ivItemIcon.setVisibility(View.GONE);
            holder.tvItemName.setVisibility(View.GONE);
        } else {
            holder.ivItemIcon.setVisibility(View.VISIBLE);
            holder.tvItemName.setVisibility(View.VISIBLE);
            QDUser user = QDUserHelper.getUserById(bean.getCreateUid());
            if (user != null) {
                QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), holder.ivItemIcon);
            } else {
                QDBitmapUtil.getInstance().createAvatar(context, bean.getCreateUid(), bean.getCreateUname(), holder.ivItemIcon);
            }
            holder.tvItemName.setText(bean.getCreateUname());
        }
        holder.tvItemAddress.setText(address);
        holder.tvItemTime.setText(QDDateUtil.longToString(Long.valueOf(time) * 1000, QDDateUtil.MSG_FORMAT4));
        holder.tvItemReveivers.setText(sb.toString().substring(0, sb.toString().length() - 1));

        if (bean.getAttachs() == null || bean.getAttachs().size() == 0) {
            return convertView;
        }

        String url = bean.getAttachs().get(0).getUrlOriginal();
        String name = url.substring(url.lastIndexOf("/") + 1);
        final String path = QDStorePath.IMG_PATH + name;
        holder.ivItemImg.setImageResource(R.mipmap.ic_download_loading);
        File file = new File(path);
        if (file.exists() && file.length() != 0) {
            holder.ivItemImg.setImageBitmap(getBitmap(path));
        } else {
            QDFileManager.getInstance().downloadFile(path, QDUtil.getWebFileServer() + url, new QDFileDownLoadCallBack() {

                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    holder.ivItemImg.setImageBitmap(getBitmap(path));
                }

                @Override
                public void onDownLoadFailed(String msg) {
                    holder.ivItemImg.setImageResource(R.mipmap.ic_download_failed);
                }
            });
        }

        holder.ivItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>(1);
                list.add(path);
                Intent intent = new Intent(context, QDPicActivity.class);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) list);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private Bitmap getBitmap(String path) {
        Bitmap bitmap;
        if (cache.get(path) == null) {
            try {
                bitmap = BitmapFactory.decodeFile(path);
                if (bitmap == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeFile(path, options);
                }
            } catch (Exception e) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
            cache.put(path, bitmap);
        } else {
            bitmap = cache.get(path);
        }
        return bitmap;
    }
}
