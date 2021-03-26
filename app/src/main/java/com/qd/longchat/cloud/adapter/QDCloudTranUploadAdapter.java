package com.qd.longchat.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.callback.QDCloudFileCallBack;
import com.longchat.base.dao.QDUploadInfo;
import com.longchat.base.databases.QDUploadInfoHelper;
import com.longchat.base.manager.listener.QDFileCallBackManager;
import com.longchat.base.util.QDFileAccess;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.cloud.holder.QDCloudTranFileHolder;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/13 下午1:33
 */
public class QDCloudTranUploadAdapter extends BaseAdapter {

    private Context context;
    private List<QDUploadInfo> infoList;
    private int uploadintIndex, uploadedIndex;

    public QDCloudTranUploadAdapter(Context context, List<QDUploadInfo> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
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
        final QDCloudTranFileHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cloud_tran, null);
            holder = new QDCloudTranFileHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDCloudTranFileHolder) convertView.getTag();
        }
        final QDUploadInfo info = infoList.get(position);
        final int state = info.getState();
        if (position == 0) {
            initCount();
            holder.itemTitleLayout.setVisibility(View.VISIBLE);
            if (state != QDUploadInfo.TYPE_COMPLETE) {
                boolean isAllPause = false;
                for (QDUploadInfo i : infoList) {
                    if (i.getState() == QDUploadInfo.TYPE_UPLOAD) {
                        isAllPause = true;
                        break;
                    }
                }
                holder.tvItemTitle.setText(context.getResources().getString(R.string.file_loading) + "(" + uploadintIndex + ")");
                if (isAllPause) {
                    holder.tvItemRight.setText("");
                } else {
                    holder.tvItemRight.setText("");
                }
            } else {
                holder.tvItemTitle.setText(context.getResources().getString(R.string.file_loading) + "(" + uploadedIndex + ")");
                holder.tvItemRight.setText(R.string.cloud_clear_history);
            }
        } else {
            QDUploadInfo info1 = infoList.get(position - 1);
            if (info.getState() == QDUploadInfo.TYPE_COMPLETE && info.getState() != info1.getState()) {
                holder.itemTitleLayout.setVisibility(View.VISIBLE);
                holder.tvItemTitle.setText(context.getResources().getString(R.string.file_loading) + "(" + uploadedIndex + ")");
                holder.tvItemRight.setText(R.string.cloud_clear_history);
            } else {
                holder.itemTitleLayout.setVisibility(View.GONE);
            }
        }
        holder.tvItemName.setText(info.getName());
        holder.ivItemIcon.setImageResource(QDUtil.getFileIconByName(context, info.getName()));
        if (state == QDUploadInfo.TYPE_UPLOAD) {
            uploadFile(holder, info);
        } else if (state == QDUploadInfo.TYPE_PAUSE) {
            holder.tvItemInfo.setVisibility(View.VISIBLE);
            holder.pbItemBar.setVisibility(View.GONE);
            holder.tvItemInfo.setText(R.string.cloud_pause_upload);
        } else {
            holder.pbItemBar.setVisibility(View.GONE);
            holder.tvItemInfo.setVisibility(View.VISIBLE);
            holder.tvItemInfo.setText(QDUtil.changFileSizeToString(info.getSize()) + "   " + QDDateUtil.longToString(info.getCreateDate(), QDDateUtil.CLOUD_FORMAT));
        }
        holder.tvItemRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = holder.tvItemRight.getText().toString();
//                if (text.equalsIgnoreCase("清空记录")) {
                    final QDAlertView view = new QDAlertView.Builder()
                            .setContext(context)
                            .setStyle(QDAlertView.Style.Alert)
                            .isHaveCancelBtn(true)
                            .setTitle(context.getResources().getString(R.string.cloud_clear_history))
                            .setContent(context.getResources().getString(R.string.cloud_sure_clear_history))
                            .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                                @Override
                                public void onClick(boolean b) {
                                    if (b) {
                                        QDUploadInfoHelper.deleteTable();
                                        infoList = QDUploadInfoHelper.loadAll();
                                        notifyDataSetChanged();
                                    }
                                }
                            })
                            .build();
                    view.show();
//                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == QDUploadInfo.TYPE_COMPLETE) {
                    String path = QDStorePath.CLOUD_PATH + info.getName();
                    QDUtil.openFile(context, path);
                }
            }
        });
        return convertView;
    }

    private void uploadFile(final QDCloudTranFileHolder holder, final QDUploadInfo info) {
        holder.tvItemInfo.setVisibility(View.GONE);
        holder.pbItemBar.setVisibility(View.VISIBLE);
        holder.pbItemBar.setProgress((int) (info.getChunk() * QDFileAccess.SPLIT_FILE_SIZE * 100 / info.getSize()));
        QDFileCallBackManager.getInstance().addCallBack(info.getMd5(), new QDCloudFileCallBack<Boolean>() {
            @Override
            public void onUploading(String path, int per) {
                int p;
                int index = Integer.valueOf(path);
                if (index != info.getTotal() - 1) {
                    p = (int) (((index * QDFileAccess.SPLIT_FILE_SIZE + (QDFileAccess.SPLIT_FILE_SIZE * per) / 100) * 100) / info.getSize());
                } else {
                    p = (int) (((index * QDFileAccess.SPLIT_FILE_SIZE + ((info.getSize() - QDFileAccess.SPLIT_FILE_SIZE * index) * per) / 100) * 100) / info.getSize());
                }
                QDLog.e("11111", "index is:" + index + "the per is:" + per + ", " + p);
                holder.pbItemBar.setProgress(p);
            }

            @Override
            public void onUploadFailed(String errorMsg) {

            }

            @Override
            public void onUploadSuccess(Boolean b) {
                if (b) {
                    holder.pbItemBar.setVisibility(View.GONE);
                    holder.tvItemInfo.setVisibility(View.VISIBLE);
                    holder.tvItemInfo.setText(QDUtil.changFileSizeToString(info.getSize()) + "   " + QDDateUtil.longToString(info.getCreateDate(), QDDateUtil.CLOUD_FORMAT));
                }
            }

            @Override
            public void onPause() {
                holder.pbItemBar.setVisibility(View.GONE);
                holder.tvItemInfo.setVisibility(View.VISIBLE);
                holder.tvItemInfo.setText(R.string.cloud_pause_upload);
            }
        });
    }

    private void initCount() {
        uploadintIndex = 0;
        uploadedIndex = 0;
        for (QDUploadInfo uploadInfo : infoList) {
            int state = uploadInfo.getState();
            if (state == QDUploadInfo.TYPE_PAUSE || state == QDUploadInfo.TYPE_UPLOAD) {
                uploadintIndex++;
            } else {
                uploadedIndex++;
            }
        }
    }
}
