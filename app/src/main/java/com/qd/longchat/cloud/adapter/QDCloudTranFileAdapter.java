package com.qd.longchat.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.dao.QDDownloadInfo;
import com.longchat.base.databases.QDDownloadInfoHelper;
import com.longchat.base.manager.QDCloudFileManager;
import com.qd.longchat.R;
import com.qd.longchat.cloud.holder.QDCloudTranFileHolder;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.qd.longchat.view.QDCopyPopupList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/7 下午1:46
 */
public class QDCloudTranFileAdapter extends BaseAdapter {

    private Context context;
    private List<QDDownloadInfo> infoList;
    private int downloadingIndex, downloadedIndex;

    public QDCloudTranFileAdapter(Context context, List<QDDownloadInfo> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public QDDownloadInfo getItem(int position) {
        return infoList.get(position);
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
        final QDDownloadInfo info = infoList.get(position);
        final int state = info.getState();
        if (position == 0) {
            initCount();
            holder.itemTitleLayout.setVisibility(View.VISIBLE);
            if (state != QDDownloadInfo.TYPE_COMPLETE) {
                boolean isAllPause = false;
                for (QDDownloadInfo i : infoList) {
                    if (i.getState() == QDDownloadInfo.TYPE_DOWNLOAD) {
                        isAllPause = true;
                        break;
                    }
                }
                holder.tvItemTitle.setText(context.getResources().getString(R.string.str_downloading) + "(" + downloadingIndex + ")");
                if (isAllPause) {
                    holder.tvItemRight.setText(R.string.cloud_all_pause);
                } else {
                    holder.tvItemRight.setText(R.string.cloud_all_start);
                }
            } else {
                holder.tvItemTitle.setText(context.getResources().getString(R.string.cloud_downloaded) + "(" + downloadingIndex + ")");
                holder.tvItemRight.setText(R.string.cloud_clear_history);
            }
        } else {
            QDDownloadInfo info1 = infoList.get(position - 1);
            if (info.getState() == QDDownloadInfo.TYPE_COMPLETE && info.getState() != info1.getState()) {
                holder.itemTitleLayout.setVisibility(View.VISIBLE);
                holder.tvItemTitle.setText(context.getResources().getString(R.string.cloud_downloaded) + "(" + downloadingIndex + ")");
                holder.tvItemRight.setText(R.string.cloud_clear_history);
            } else {
                holder.itemTitleLayout.setVisibility(View.GONE);
            }
        }
        holder.tvItemName.setText(info.getName());
        holder.ivItemIcon.setImageResource(QDUtil.getFileIconByName(context, info.getName()));
        if (state == QDDownloadInfo.TYPE_DOWNLOAD) {
           downloadFile(holder, info);
        } else if (state == QDDownloadInfo.TYPE_PAUSE) {
            holder.pbItemBar.setVisibility(View.GONE);
            holder.tvItemInfo.setVisibility(View.VISIBLE);
            holder.tvItemInfo.setText(R.string.cloud_pause_download);
        } else {
            holder.pbItemBar.setVisibility(View.GONE);
            holder.tvItemInfo.setVisibility(View.VISIBLE);
            holder.tvItemInfo.setText(QDUtil.changFileSizeToString(info.getTotalLength()) + "   " + QDDateUtil.longToString(info.getCreateDate(), QDDateUtil.CLOUD_FORMAT));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == QDDownloadInfo.TYPE_DOWNLOAD) {
                    QDCloudFileManager.cancel(info.getMd5());
                } else if (state == QDDownloadInfo.TYPE_PAUSE) {
                    QDDownloadInfoHelper.updateStateAndReadLength(info.getMd5(), QDDownloadInfo.TYPE_DOWNLOAD, info.getReadLength());
                } else if (state == QDDownloadInfo.TYPE_COMPLETE) {
                    QDUtil.openFile(context, info.getPath());
                    return;
                }
                infoList = QDSortUtil.sortDownloadInfo(QDDownloadInfoHelper.loadAll());
                notifyDataSetChanged();
            }
        });

        holder.tvItemRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = holder.tvItemRight.getText().toString();
                if (text.equalsIgnoreCase(context.getResources().getString(R.string.cloud_clear_history))) {
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
                                        QDDownloadInfoHelper.clearCompleteInfo();
                                        infoList = QDDownloadInfoHelper.loadAll();
                                        notifyDataSetChanged();
                                    }
                                }
                            })
                            .build();
                    view.show();

                } else if (text.equalsIgnoreCase(context.getResources().getString(R.string.cloud_all_pause))) {
                    QDCloudFileManager.cancelAll();
                    infoList = QDSortUtil.sortDownloadInfo(QDDownloadInfoHelper.loadAll());
                    notifyDataSetChanged();
                    holder.tvItemRight.setText(R.string.cloud_all_start);
                } else if (text.equalsIgnoreCase(context.getResources().getString(R.string.cloud_all_start))) {
                    QDDownloadInfoHelper.updateState();
                    infoList = QDSortUtil.sortDownloadInfo(QDDownloadInfoHelper.loadAll());
                    notifyDataSetChanged();
                    holder.tvItemRight.setText(R.string.cloud_all_pause);
                }
            }
        });
        showPopupWindow(context, convertView, position);
        return convertView;
    }

    private void downloadFile(final QDCloudTranFileHolder holder, QDDownloadInfo info) {
        holder.tvItemInfo.setVisibility(View.GONE);
        holder.pbItemBar.setVisibility(View.VISIBLE);
        holder.pbItemBar.setProgress((int) (info.getReadLength() * 100 / info.getTotalLength()));
        info.setState(QDDownloadInfo.TYPE_DOWNLOAD);
        QDDownloadInfoHelper.updateStateAndReadLength(info.getMd5(), QDDownloadInfo.TYPE_DOWNLOAD, info.getReadLength());
        QDCloudFileManager.downloadFile(info, new QDFileDownLoadCallBack() {
            @Override
            public void onDownLoading(int per) {
                holder.pbItemBar.setProgress(per);
            }

            @Override
            public void onDownLoadSuccess() {
                infoList = QDSortUtil.sortDownloadInfo(QDDownloadInfoHelper.loadAll());
                notifyDataSetChanged();
            }

            @Override
            public void onDownLoadFailed(String msg) {

            }
        });
    }

    private void initCount() {
        downloadingIndex = 0;
        downloadedIndex = 0;
        for (QDDownloadInfo downloadInfo : infoList) {
            int state = downloadInfo.getState();
            if (state == QDDownloadInfo.TYPE_DOWNLOAD || state == QDDownloadInfo.TYPE_PAUSE) {
                downloadingIndex++;
            } else {
                downloadedIndex++;
            }
        }
    }

    private void showPopupWindow(final Context context, View view, final int index) {
        final List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add(context.getResources().getString(R.string.conversation_del));

        final QDCopyPopupList popupList = new QDCopyPopupList();
        popupList.init(context, view, popupMenuItemList, new QDCopyPopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                QDDownloadInfo info = getItem(index);
                if (info.getState() == QDDownloadInfo.TYPE_DOWNLOAD) {
                    QDCloudFileManager.cancel(info.getMd5());
                    File file = new File(info.getPath());
                    file.delete();
                }
                QDDownloadInfoHelper.deleteById(info.getMd5());
                infoList.remove(index);
                notifyDataSetChanged();
            }
        });
        popupList.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));
    }

}
