package com.qd.longchat.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.gavin.giframe.utils.GIUtil;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.QDFileManager;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDChatActivity;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.activity.QDSelectContactActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.holder.QDMassChatHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDCopyPopupList;
import com.qd.longchat.widget.QDChatSmiley;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/2/27 上午10:42
 */
public class QDMassChatAdapter extends BaseAdapter {

    private final static int TIME_INTERVAL = 60000; //消息时间显示的时间间隔

    private Context context;
    private List<QDMessage> msgList;
    private List<String> idList;
    private long lastTime;
    private int model;
    private List<String> cdataList;
    private List<String> selectedIdList;
    private OnMultiListener listener;
    private Map<String, View> viewMap;

    public interface OnMultiListener {
        void onMulti(QDMessage message);
    }

    public QDMassChatAdapter(Context context, OnMultiListener listener) {
        this.context = context;
        this.listener = listener;
        this.msgList = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.cdataList = new ArrayList<>();
        this.selectedIdList = new ArrayList<>();
        this.viewMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        return msgList.size();
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
        final QDMassChatHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_mass_chat, null);
            holder = new QDMassChatHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDMassChatHolder) convertView.getTag();
        }

        final QDMessage message = msgList.get(position);

        if (model == QDChatActivity.MODE_DEL) {
            holder.ivCheck.setVisibility(View.VISIBLE);
            if (selectedIdList.contains(message.getMsgId())) {
                holder.ivCheck.setImageResource(R.drawable.ic_round_selected);
            } else {
                holder.ivCheck.setImageResource(R.drawable.ic_round_unselected);
            }

            holder.ivCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedIdList.contains(message.getMsgId())) {
                        holder.ivCheck.setImageResource(R.drawable.ic_round_unselected);
                        selectedIdList.remove(message.getMsgId());
                        cdataList.remove(message.getCdata());
                    } else {
                        holder.ivCheck.setImageResource(R.drawable.ic_round_selected);
                        selectedIdList.add(message.getMsgId());
                        cdataList.add(message.getCdata());
                    }
                }
            });
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }
        long offsetTime = QDClient.getInstance().getLoginInfo().getSTimeOffset(); //服务器和本地的时间差
        long msgTime = (message.getCreateDate() / 1000) - offsetTime; //微秒变为毫秒
        if (position == 0) {
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvTime.setText(QDDateUtil.getMsgTime(msgTime));
        } else {
            QDMessage lastMsg = msgList.get(position - 1);
            lastTime = lastMsg.getCreateDate() / 1000 - offsetTime;
            if (msgTime - lastTime > TIME_INTERVAL) {
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.tvTime.setText(QDDateUtil.getMsgTime(msgTime));
            } else {
                holder.tvTime.setVisibility(View.GONE);
            }
        }
        QDBitmapUtil.getInstance().createAvatar(context, QDLanderInfo.getInstance().getId(), QDLanderInfo.getInstance().getName(), QDLanderInfo.getInstance().getPic(), holder.ivIcon);

        if (viewMap.get(message.getMsgId()) == null) {
            View view = getContentView(message.getMsgType(), message);
            if (view != null) {
                viewMap.put(message.getMsgId(), view);
                bindViews(holder.llContainer, view);
            }
        } else {
            bindViews(holder.llContainer, viewMap.get(message.getMsgId()));
        }
        return convertView;
    }

    private View getContentView(String type, QDMessage message) {
        switch (type) {
            case QDMessage.MSG_TYPE_TEXT:
                return createTextView(context, message);
            case QDMessage.MSG_TYPE_IMAGE:
                return createImageView(context, message);
            case QDMessage.MSG_TYPE_FILE:
                return createFileView(context, message);
        }
        return null;
    }

    private void bindViews(LinearLayout container, View view) {
        container.removeAllViews();
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null)
            viewGroup.removeAllViews();
        container.addView(view);
    }

    private View createTextView(Context context, QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_mass, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvRecv = view.findViewById(R.id.tv_recv);

        String html = "<font color='#A3A3A3'><small>" + context.getResources().getString(R.string.str_mass) + "</small></font><font color='#000000'> "
                + QDChatSmiley.getInstance(context).strToSmiley(message.getSubject()) + "</font>";
        tvTitle.setText(GIUtil.showHtmlInfo(html));
        tvRecv.setText(context.getResources().getString(R.string.str_recv) + " " + message.getExtData1());
        if (model != QDChatActivity.MODE_DEL) {
            showPopupWindow(context, view, message);
        }
        return view;
    }

    private View createImageView(final Context context, final QDMessage message) {
        final RoundedImageView imageView = new RoundedImageView(context);
        imageView.setMaxWidth(QDUtil.dp2px(context, 100));
        imageView.setMaxHeight(QDUtil.dp2px(context, 180));
        imageView.setAdjustViewBounds(true);
        imageView.setImageResource(R.mipmap.ic_download_loading);

        String content = message.getContent();
        final IMBTFFile imbtfFile = IMBTFFile.fromBTFXml(content);
        if (imbtfFile == null) {
            imageView.setImageResource(R.mipmap.ic_download_failed);
        } else {
            final String path = QDStorePath.MSG_FILE_PATH + imbtfFile.getName();

            final QDFileDownLoadCallBack callBack = new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {
                }

                @Override
                public void onDownLoadSuccess() {
                    message.setFileStatus(QDMessage.MSG_FILE_STATUS_DOWNLOADED);
                    message.setFilePath(path);
                    QDMessageHelper.updateMessage(message);
                    displayImage(context, imageView, new File(path));
                }

                @Override
                public void onDownLoadFailed(String msg) {
                    message.setFileStatus(QDMessage.MSG_FILE_STATUS_DOWNLOAD_FAILED);
                    QDMessageHelper.updateMessage(message);
                    imageView.setImageResource(R.mipmap.ic_download_failed);
                }
            };

            String filePath = message.getFilePath();
            String fsHost = imbtfFile.getFsHost();
            if (TextUtils.isEmpty(fsHost)) {
                fsHost = QDUtil.getWebFileServer();
            }
            String original = imbtfFile.getOriginal();
            String url = fsHost + original;
            if (TextUtils.isEmpty(filePath)) {
                QDFileManager.getInstance().downloadFile(message.getMsgId(), path, url, callBack);
            } else {
                File file = new File(filePath);
                if (!file.exists()) {
                    QDFileManager.getInstance().downloadFile(message.getMsgId(), path, url, callBack);
                } else {
                    displayImage(context, imageView, new File(path));
                }
            }

        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> pathList = new ArrayList<>();
                List<QDMessage> msgList = QDMessageHelper.getMassImageMessage();
                int size = msgList.size();
                int index = 0;
                for (int i=0; i<size; i++) {
                    QDMessage msg = msgList.get(i);
                    if (!TextUtils.isEmpty(msg.getFilePath()))
                        pathList.add(msg.getFilePath());
                    if (message.getCdata().equalsIgnoreCase(msg.getCdata())) {
                        index = i;
                    }
                }
                Intent intent = new Intent(context, QDPicActivity.class);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, index);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_HAVE_LONG_CLICK, true);
                context.startActivity(intent);
            }
        });
        if (model != QDChatActivity.MODE_DEL) {
            showPopupWindow(context, imageView, message);
        }
        return imageView;
    }

    private static void displayImage(Context context, ImageView view, File file) {
        try {
            if (file.getName().toLowerCase().endsWith("gif")) {
                Glide.with(context).load(file).apply(new RequestOptions().placeholder(R.mipmap.ic_download_loading)).into(view);
            } else {
                Glide.with(context).load(file).apply(new RequestOptions().placeholder(R.mipmap.ic_download_loading).override(QDUtil.dp2px(context, 100), QDUtil.dp2px(context, 180)).dontAnimate()).into(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View createFileView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_file_view, null);
        ImageView ivIcon = view.findViewById(R.id.iv_chat_img);
        TextView tvTitle = view.findViewById(R.id.tv_chat_title);
        TextView tvDesc = view.findViewById(R.id.tv_chat_desc);
        final TextView tvInfo = view.findViewById(R.id.tv_chat_info);
        final ProgressBar progress = view.findViewById(R.id.pb_chat_progress);

        progress.setMax(100);
        progress.setVisibility(View.GONE);
        view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
        tvTitle.setTextColor(Color.WHITE);
        tvDesc.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
        tvInfo.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));

        String content = message.getContent();
        final IMBTFFile imbtfFile = IMBTFFile.fromBTFXml(content);
        if (imbtfFile == null) {
            return view;
        }
        ivIcon.setImageResource(QDUtil.getFileIconByName(context, imbtfFile.getName()));
        tvTitle.setText(imbtfFile.getName());
        tvDesc.setText(QDUtil.changFileSizeToString(imbtfFile.getSize()));

        final String filePath = QDStorePath.MSG_FILE_PATH + imbtfFile.getName();

        final QDFileDownLoadCallBack callBack = new QDFileDownLoadCallBack() {
            @Override
            public void onDownLoading(int per) {
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(per);
            }

            @Override
            public void onDownLoadSuccess() {
                progress.setVisibility(View.GONE);
                tvInfo.setText(R.string.file_downloaded);
                message.setFileStatus(QDMessage.MSG_FILE_STATUS_DOWNLOADED);
                message.setFilePath(filePath);
                QDMessageHelper.updateMessage(message);
            }

            @Override
            public void onDownLoadFailed(String msg) {
                progress.setVisibility(View.GONE);
                tvInfo.setText(R.string.file_download_failed);
                message.setFileStatus(QDMessage.MSG_FILE_STATUS_DOWNLOAD_FAILED);
                QDMessageHelper.updateMessage(message);
            }
        };

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = message.getFilePath();
                String fsHost = imbtfFile.getFsHost();
                if (TextUtils.isEmpty(fsHost)) {
                    fsHost = QDUtil.getWebFileServer();
                }
                String original = imbtfFile.getOriginal();
                String url = fsHost + original;
                if (TextUtils.isEmpty(filePath)) {
                    filePath = QDStorePath.MSG_FILE_PATH + imbtfFile.getName();
                    QDFileManager.getInstance().downloadFile(message.getMsgId(), filePath, url, callBack);
                } else {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        filePath = QDStorePath.MSG_FILE_PATH + imbtfFile.getName();
                        QDFileManager.getInstance().downloadFile(message.getMsgId(), filePath, url, callBack);
                    } else {
                        QDUtil.openFile(context, filePath);
                    }
                }
            }
        });

        switch (message.getFileStatus()) {
            case QDMessage.MSG_FILE_STATUS_UPLOADED:
                tvInfo.setText(context.getResources().getString(R.string.file_load_success));
                break;
            case QDMessage.MSG_FILE_STATUS_UNDOWNLOAD:
                tvInfo.setText(context.getResources().getString(R.string.file_undownload));
                break;
            case QDMessage.MSG_FILE_STATUS_DOWNLOADED:
                tvInfo.setText(context.getResources().getString(R.string.file_downloaded));
                break;
            case QDMessage.MSG_FILE_STATUS_DOWNLOAD_FAILED:
                tvInfo.setText(context.getResources().getString(R.string.file_download_failed));
                break;
        }
        if (model != QDChatActivity.MODE_DEL) {
            showPopupWindow(context, view, message);
        }
        return view;
    }

    public void setMsgList(List<QDMessage> msgList) {
        idList.clear();
        this.msgList.clear();
        for (QDMessage message : msgList) {
            if (!idList.contains(message.getMsgId())) {
                idList.add(message.getMsgId());
                this.msgList.add(message);
            }
        }
        notifyDataSetChanged();
    }

    public void addMsgList(List<QDMessage> msgList) {
        boolean isUpdate = false;
        for (QDMessage message : msgList) {
            if (!idList.contains(message.getMsgId())) {
                isUpdate = true;
                idList.add(message.getMsgId());
                this.msgList.add(message);
            }
        }
        if (isUpdate) {
            notifyDataSetChanged();
        }
    }

    public void removeMessage(String msgId) {
        int index = idList.indexOf(msgId);
        idList.remove(index);
        this.msgList.remove(index);
        notifyDataSetChanged();
    }

    private void showPopupWindow(final Context context, View view, final QDMessage message) {
        final List<String> popupMenuItemList = new ArrayList<>();
        String msgType = message.getMsgType();
        final String strCopy = context.getResources().getString(R.string.str_copy);
        final String strForward = context.getResources().getString(R.string.cloud_forward);
        final String strDel = context.getResources().getString(R.string.conversation_del);
        final String strMultiSel = context.getResources().getString(R.string.str_multi_sel);
        if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_TEXT)) {
            popupMenuItemList.add(strCopy);
        }
        popupMenuItemList.add(strForward);
        popupMenuItemList.add(strDel);
        popupMenuItemList.add(strMultiSel);

        final QDCopyPopupList popupList = new QDCopyPopupList();
        popupList.init(context, view, popupMenuItemList, new QDCopyPopupList.OnPopupListClickListener() {

            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                String info = popupMenuItemList.get(position);
                if (info.equalsIgnoreCase(strCopy)) {
                    copyText(context, message.getSubject());
                } else if (info.equalsIgnoreCase(strForward)) {
                    toSelectContactActivity(context, message);
                } else if (info.equalsIgnoreCase(strDel)) {
                    String cdata = message.getCdata();
                    QDMessageHelper.deleteMessageByCdata(cdata);
                    removeMessage(message.getMsgId());
                } else if (info.equalsIgnoreCase(strMultiSel)) {
                    if (listener != null) {
                        listener.onMulti(message);
                    }
                }
            }
        });
    }

    private static void copyText(Context context, String subject) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", subject);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    private static void toSelectContactActivity(Context context, QDMessage message) {
        Intent intent = new Intent(context, QDSelectContactActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_SINGLE);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, true);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
        context.startActivity(intent);
    }

    public void setModel(int model) {
        this.model = model;
    }

    public void deleteMessages() {
        for (String id : selectedIdList) {
            int index = idList.indexOf(id);
            idList.remove(index);
            msgList.remove(index);
        }
    }

    public void clearSelectedList() {
        selectedIdList.clear();
        cdataList.clear();
        notifyDataSetChanged();
    }

    public void addSelectedList(QDMessage message) {
        if (!selectedIdList.contains(message.getMsgId())) {
            selectedIdList.add(message.getMsgId());
            cdataList.add(message.getCdata());
            notifyDataSetChanged();
        }
    }

    public List<String> getCdataList() {
        return cdataList;
    }
}
