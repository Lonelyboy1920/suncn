package com.qd.longchat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFAT;
import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.btf.IMBTFItem;
import com.longchat.base.btf.IMBTFLoc;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.model.QDCollectMessage;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDCollectAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.qd.longchat.view.QDCopyPopupList;
import com.qd.longchat.widget.QDChatSmiley;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/9 下午2:09
 */
public class QDCollectActivity extends QDBaseActivity {

    @BindView(R2.id.view_collect_title)
    View viewTitle;
    @BindView(R2.id.iv_collect_list)
    ListView listView;

    @BindString(R2.string.self_collect)
    String strCollect;
    @BindString(R2.string.cloud_forward)
    String strForward;
    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.image)
    String strImage;
    @BindString(R2.string.voice)
    String strVoice;
    @BindString(R2.string.file)
    String strFile;
    @BindString(R2.string.shoot)
    String strShoot;
    @BindString(R2.string.location)
    String strLocation;
    @BindString(R2.string.collect_get_error)
    String strCollectError;

    private QDCollectAdapter adapter;

    private long lastClick;

    private boolean isForward;

    private String chatId;

    private String chatName;

    private boolean isGroup;

    private QDResultCallBack<List<QDCollectMessage>> callBack = new QDResultCallBack<List<QDCollectMessage>>() {
        @Override
        public void onError(String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strCollectError);
                }
            });
        }

        @Override
        public void onSuccess(final List<QDCollectMessage> messageList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWarningDailog().dismiss();
                    adapter.setMessageList(QDSortUtil.sortCollect(messageList));
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        isForward = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, false);
        chatId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID);
        chatName = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME);
        isGroup = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_GROUP, false);
        tvTitleName.setText(strCollect);
        adapter = new QDCollectAdapter(context);
        listView.setAdapter(adapter);
        if (QDClient.getInstance().isOnline()) {
            getWarningDailog().show();
            QDClient.getInstance().fetchCollectMsg(callBack);
            showPopupWindow(context, listView);
        }
    }

    @OnItemClick(R2.id.iv_collect_list)
    public void onItemClick(int position) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClick < 1000) {
            return;
        }
        lastClick = currentTime;
        final QDCollectMessage message = adapter.getItem(position);
        if (isForward) {
            String content = "";
            String msgType = message.getMsgType();
            switch (msgType) {
                case QDMessage.MSG_TYPE_TEXT:
                    IMBTFManager manager = new IMBTFManager(message.getContent());
                    List<IMBTFItem> imbtfItemList = manager.getItemList();
                    for (IMBTFItem item : imbtfItemList) {
                        if (item instanceof IMBTFText) {
                            content += QDChatSmiley.getInstance(context).strToSmiley(((IMBTFText) item).getContent());
                        } else if (item instanceof IMBTFAT) {
                            String info = ((IMBTFAT) item).getContent();
                            String[] infos = info.split(";");
                            String userId = infos[0];
                            String userName = infos[1];
                            content += "@" + userName;
                        }
                    }
                    break;
                case QDMessage.MSG_TYPE_FILE:
                    IMBTFFile imageFile = IMBTFFile.fromBTFXml(message.getContent());
                    content = imageFile.getName();
                    break;
                case QDMessage.MSG_TYPE_IMAGE:
                    content = QDStorePath.COLLECT_PATH + message.getGid() + ".png";
                    break;
                case QDMessage.MSG_TYPE_VOICE:
                    content = context.getResources().getString(R.string.voice);
                    break;
                case QDMessage.MSG_TYPE_SHOOT:
                    content = context.getResources().getString(R.string.shoot);
                    break;
                case QDMessage.MSG_TYPE_LOCATION:
                    content = context.getResources().getString(R.string.location);
                    break;
            }
            String warn = "";

            if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_IMAGE)) {
                warn = content;
                content = "";
            }

            Object bitmap;
            if (QDBitmapUtil.getInstance().getBitmapFromCache(chatId) == null) {
                if (isGroup) {
                    bitmap = R.mipmap.im_recent_group_icon;
                } else {
                    if (chatId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                        bitmap = R.mipmap.im_recent_my_pc_icon;
                    } else {
                        bitmap = QDBitmapUtil.getInstance().createNameAvatar(context, chatId, chatName);
                    }
                }
            } else {
                bitmap = QDBitmapUtil.getInstance().getBitmapFromCache(chatId);
            }

            QDAlertView dialog = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Send)
                    .setUserIcon(bitmap)
                    .setTitle(chatName)
                    .setContent(content)
                    .setWarning(warn)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            if (isSure) {

                                QDMessage msg = getMsgFromCollectMsg(message);

                                Intent intent = new Intent();
                                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, msg);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    })
                    .build();

            dialog.show();
        } else {
            switch (message.getMsgType()) {
                case QDMessage.MSG_TYPE_VOICE:
                case QDMessage.MSG_TYPE_IMAGE:
                case QDMessage.MSG_TYPE_SHOOT:
                case QDMessage.MSG_TYPE_TEXT: {
                    Intent intent = new Intent(context, QDCollectDetailActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_COLLECT_MESSAGE, message);
                    startActivity(intent);
                }
                break;
                case QDMessage.MSG_TYPE_LOCATION: {
                    String content = message.getContent();
                    IMBTFLoc loc = IMBTFLoc.fromBTFXml(content);
                    String locInfo = loc.getContent();
                    final String[] infos = locInfo.split(";");
                    final QDLocationBean locationBean = new QDLocationBean();
                    locationBean.setLatitude(Double.valueOf(infos[0]));
                    locationBean.setLongitude(Double.valueOf(infos[1]));
                    locationBean.setSampleLocationInfo(infos[2]);
                    locationBean.setDetailLocationInfo(infos[3]);

                    Intent intent = new Intent(context, QDBaiduLocationInfoActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_LOCATION, locationBean);
                    context.startActivity(intent);
                }
                break;
                case QDMessage.MSG_TYPE_FILE: {
                    IMBTFFile file = IMBTFFile.fromBTFXml(message.getContent());
                    String path = QDStorePath.COLLECT_PATH + message.getGid() + QDUtil.getSuffix(file.getName());
                    QDUtil.openFile(context, path);
//                Intent intent = new Intent(context, QDFileDisplayActivity.class);
//                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, path);
//                startActivity(intent);
                }
                break;
            }
        }
    }

    private void showPopupWindow(final Context context, View view) {
        final List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add(strForward);
        popupMenuItemList.add(strDel);

        final QDCopyPopupList popupList = new QDCopyPopupList();
        popupList.init(context, view, popupMenuItemList, new QDCopyPopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                if (position == 0) {
                    forwardMsg(adapter.getItem(contextPosition));
                } else {
                    QDCollectMessage message = adapter.getItem(contextPosition);
                    QDClient.getInstance().deleteCollectMsg(message.getGid());
                    adapter.remove(contextPosition);
                }
            }
        });
        popupList.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));
    }

    private QDMessage getMsgFromCollectMsg(QDCollectMessage message) {
        QDMessage msg = null;
        String content = message.getContent();
        String msgType = message.getMsgType();
        String subject = "";
        switch (msgType) {
            case QDMessage.MSG_TYPE_TEXT:
                IMBTFText text = IMBTFText.fromBTFXml(content);
                subject = text.getContent();
                msg = QDClient.getInstance().createMessageFromCollectMsg(message, subject, "");
                break;
            case QDMessage.MSG_TYPE_LOCATION:
                msg = QDClient.getInstance().createMessageFromCollectMsg(message, strLocation, "");
                break;
            default:
                IMBTFFile file = IMBTFFile.fromBTFXml(content);
                String attachment = file.getName() + "/" + file.getSize() + "/" + message.getFileIds();
                if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_IMAGE)) {
                    subject = strImage;
                } else if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_FILE)) {
                    subject = strFile;
                } else if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_VOICE)) {
                    subject = strVoice;
                } else if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_SHOOT)) {
                    subject = strShoot;
                }
                msg = QDClient.getInstance().createMessageFromCollectMsg(message, subject, attachment);
                break;
        }
        return msg;
    }


    public void forwardMsg(QDCollectMessage message) {
        QDMessage msg = getMsgFromCollectMsg(message);
        Intent intent = new Intent(context, QDSelectContactActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_SINGLE);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, true);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, msg);
        context.startActivity(intent);
    }

}
