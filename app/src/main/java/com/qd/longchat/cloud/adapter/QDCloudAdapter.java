package com.qd.longchat.cloud.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.callback.QDCloudFileCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDUploadInfo;
import com.longchat.base.databases.QDUploadInfoHelper;
import com.longchat.base.manager.QDCloudFileManager;
import com.longchat.base.manager.QDCloudManager;
import com.longchat.base.manager.listener.QDFileCallBackManager;
import com.longchat.base.model.gd.QDCloudFile;
import com.longchat.base.util.QDFileAccess;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.holder.QDCloudHolder;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/16 下午1:50
 */
public class QDCloudAdapter extends BaseAdapter {

    private Context context;
    private List<QDCloud> cloudList;
    private OnMoreClickListener listener;
    private int index;

    public void setListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    public interface OnMoreClickListener {
        void onMoreClick(QDCloud cloud, int type);
    }

    public QDCloudAdapter(Context context) {
        this.context = context;
        index = 0;
    }

    @Override
    public int getCount() {
        if (cloudList == null)
            return 0;
        return cloudList.size();
    }

    @Override
    public QDCloud getItem(int position) {
        return cloudList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<QDCloud> getCloudList() {
        return cloudList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        QDCloudHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cloud, null);
            holder = new QDCloudHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDCloudHolder) convertView.getTag();
        }
        final QDCloud cloud = cloudList.get(position);
        if (cloud.getType() == QDCloud.TYPE_CLOUD) {
            holder.ivItemMore.setVisibility(View.GONE);
        } else {
            holder.ivItemMore.setVisibility(View.VISIBLE);
        }
        if (cloud.getType() == QDCloud.TYPE_FILE) {
            holder.tvItemInfo.setVisibility(View.VISIBLE);
            holder.tvItemSize.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(cloud.getCreaterName()).append("  ").append(QDUtil.changFileSizeToString(cloud.getSize()))
                    .append("   ").append(QDDateUtil.dateToString(new Date(cloud.getCreateTime() * 1000), QDDateUtil.CLOUD_FORMAT));
            holder.tvItemInfo.setText(sb.toString());
            holder.ivItemIcon.setImageResource(QDUtil.getFileIconByName(context, cloud.getName()));
        } else {
            holder.tvItemInfo.setVisibility(View.GONE);
            holder.tvItemSize.setVisibility(View.GONE);
            holder.ivItemIcon.setImageResource(R.drawable.ic_folder);
        }
        if (cloud.getStatus() == QDCloud.STATUS_UPLOAD) {
            if (cloud.getSize() <= QDFileAccess.SPLIT_FILE_SIZE) {
                uploadFile(holder, cloud, position);
            } else {
                uploadChunkFile(holder, cloud, position);
            }
        } else if (cloud.getStatus() == QDCloud.STATUS_PAUSE_UPLOAD) {
            holder.tvItemInfo.setVisibility(View.VISIBLE);
            holder.pbItemProgress.setVisibility(View.GONE);
            holder.tvItemInfo.setText(R.string.cloud_pause_upload);
            QDUploadInfoHelper.updateState(cloud.getMd5());
        }

        holder.tvItemName.setText(cloud.getName());

        holder.ivItemMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMoreClick(cloud, cloud.getType());
            }
        });
        return convertView;
    }

    public void setCloudList(List<QDCloud> cloudList) {
        this.cloudList = cloudList;
        notifyDataSetChanged();
    }

    private void uploadFile(final QDCloudHolder holder, final QDCloud cloud, final int position) {
        holder.tvItemInfo.setVisibility(View.GONE);
        holder.pbItemProgress.setVisibility(View.VISIBLE);
        final File file = new File(cloud.getPath());
        QDUploadInfo info = new QDUploadInfo();
        info.setMd5(cloud.getMd5());
        info.setName(cloud.getName());
        info.setCreateDate(System.currentTimeMillis());
        info.setSize(cloud.getSize());
        info.setState(QDUploadInfo.TYPE_UPLOAD);

        Map<String, String> map = new HashMap<>();
        map.put("size", String.valueOf(cloud.getSize()));
        map.put("md5", cloud.getMd5());
        if (!TextUtils.isEmpty(cloud.getPid()))
            map.put("folder_id", cloud.getPid());

        QDUploadInfo uploadInfo = new QDUploadInfo();
        uploadInfo.setMd5(cloud.getMd5());
        uploadInfo.setState(QDUploadInfo.TYPE_UPLOAD);
        uploadInfo.setSize(cloud.getSize());
        uploadInfo.setCreateDate(System.currentTimeMillis());
        uploadInfo.setPid(cloud.getPid());
        uploadInfo.setChunk(1);
        uploadInfo.setTotal(1);
        uploadInfo.setName(cloud.getName());
        QDUploadInfoHelper.insert(uploadInfo);

        QDFileCallBackManager.getInstance().addCallBack(cloud.getMd5(), new QDCloudFileCallBack<QDCloudFile>() {
            @Override
            public void onUploading(String path, int per) {
                holder.pbItemProgress.setProgress(per);
            }

            @Override
            public void onUploadFailed(String errorMsg) {
                holder.pbItemProgress.setVisibility(View.GONE);
                holder.tvItemInfo.setVisibility(View.VISIBLE);
                QDUtil.showToast(context, context.getResources().getString(R.string.file_load_failed) + ": " + errorMsg);
                cloudList.remove(cloud);
                notifyDataSetChanged();
            }

            @Override
            public void onUploadSuccess(QDCloudFile model) {
                updateCloud(holder, model, cloud, position);
            }

            @Override
            public void onPause() {

            }
        });

        QDCloudFileManager.uploadFile(cloud.getRootId(), cloud.getPath(), uploadInfo);
    }

    private void uploadChunkFile(final QDCloudHolder holder, final QDCloud cloud, final int position) {
        index = 0;
        holder.tvItemInfo.setVisibility(View.GONE);
        holder.pbItemProgress.setVisibility(View.VISIBLE);
        QDUploadInfo uploadInfo = QDUploadInfoHelper.getInfoByMd5(cloud.getMd5());
        QDFileAccess access;
        if (uploadInfo == null) {
            access = new QDFileAccess(cloud.getPath());
        } else {
            access = new QDFileAccess(cloud.getPath(), uploadInfo.getChunk());
        }
        access.splitFile();
        List<File> fileList = access.getFileList();
        final int total = fileList.size();
        if (uploadInfo == null) {
            uploadInfo = new QDUploadInfo();
            uploadInfo.setMd5(cloud.getMd5());
            uploadInfo.setState(QDUploadInfo.TYPE_UPLOAD);
            uploadInfo.setSize(cloud.getSize());
            uploadInfo.setCreateDate(System.currentTimeMillis());
            uploadInfo.setPid(cloud.getPid());
            uploadInfo.setChunk(1);
            uploadInfo.setTotal(total);
            uploadInfo.setName(cloud.getName());
            QDUploadInfoHelper.insert(uploadInfo);
        } else {
            index = uploadInfo.getChunk() - 1;
        }

        QDFileCallBackManager.getInstance().addCallBack(cloud.getMd5(), new QDCloudFileCallBack<Boolean>() {
            @Override
            public void onUploading(String path, int per) {
                int p;
                long writeLength;
                index = Integer.valueOf(path);
                if (index != total - 1) {
                    writeLength = index * QDFileAccess.SPLIT_FILE_SIZE + (QDFileAccess.SPLIT_FILE_SIZE * per)/100;
                } else {
                    writeLength = index * QDFileAccess.SPLIT_FILE_SIZE + ((cloud.getSize() - QDFileAccess.SPLIT_FILE_SIZE * index) * per) / 100;
                }
                p = (int) ((writeLength * 100) / cloud.getSize());
                QDLog.e("11111", "The index is: " + index + ", writtenLength is:" + writeLength + ", the size is:" + cloud.getSize() + ", the p is: " + p + ", the per is: " + per);


//                QDLog.e("11111", "name is:" + cloud.getName() + "index is:" + index + "the per is:" + per + ", " + p);
                holder.pbItemProgress.setProgress(p);
            }

            @Override
            public void onUploadFailed(String errorMsg) {
                holder.pbItemProgress.setVisibility(View.GONE);
                holder.tvItemInfo.setVisibility(View.VISIBLE);
                QDUtil.showToast(context, context.getResources().getString(R.string.file_load_failed) + ": " + errorMsg);
                cloudList.remove(cloud);
                notifyDataSetChanged();
            }

            @Override
            public void onUploadSuccess(Boolean o) {
                if (!o) {
                    index++;
                } else {
                    mergeFile(holder, cloud, position);
                }
            }

            @Override
            public void onPause() {
                holder.pbItemProgress.setVisibility(View.GONE);
                holder.tvItemInfo.setVisibility(View.VISIBLE);
                holder.tvItemInfo.setText(R.string.cloud_pause_upload);
            }
        });

        QDCloudFileManager.uploadChunkFile(cloud.getRootId(), uploadInfo);
    }

    private void mergeFile(final QDCloudHolder holder, final QDCloud cloud, final int position) {
        QDCloudManager.getInstance().mergeChunk(cloud.getRootId(), cloud.getName(), cloud.getSize(), cloud.getMd5(), cloud.getPid(), new QDResultCallBack<QDCloudFile>() {

            @Override
            public void onError(String errorMsg) {
                holder.pbItemProgress.setVisibility(View.GONE);
                holder.tvItemInfo.setVisibility(View.VISIBLE);
                QDUtil.showToast(context, errorMsg);
                cloudList.remove(cloud);
                notifyDataSetChanged();
            }

            @Override
            public void onSuccess(QDCloudFile cloudFile) {
                cloud.setStatus(QDCloud.STATUS_NORMAL);
                updateCloud(holder, cloudFile, cloud, position);
            }
        });
    }


    private void updateCloud(QDCloudHolder holder, QDCloudFile cloudFile, QDCloud cloud, int position) {
        holder.pbItemProgress.setVisibility(View.GONE);
        holder.tvItemInfo.setVisibility(View.VISIBLE);
        cloud.setId(cloudFile.getId());
        cloud.setOriginalUrl(cloudFile.getUrlOriginal());
        cloud.setStatus(QDCloud.STATUS_NORMAL);
        cloud.setCreateTime(cloudFile.getCreateTime());
        cloudList.remove(position);
        cloudList.add(position, cloud);
        notifyDataSetChanged();

        String path = QDStorePath.CLOUD_PATH + cloudFile.getName();
        QDUtil.copyFile(cloud.getPath(), path);
    }

}