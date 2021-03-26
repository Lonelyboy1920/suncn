package com.qd.longchat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFAT;
import com.longchat.base.btf.IMBTFItem;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.config.QDSDKConfig;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDConversationHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.util.QDBadWordUtil;
import com.longchat.base.util.QDStringUtil;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDChatAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDChatModel;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.util.QDAtClickSpan;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDRecorderUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.qd.longchat.view.QDGridLayout;
import com.qd.longchat.widget.QDChatFunc;
import com.qd.longchat.widget.QDChatSmiley;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import id.zelory.compressor.Compressor;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/23 上午9:20
 */

public class QDChatActivity extends QDBaseActivity implements QDGridLayout.GridItemClickListener {

    private final static int REQUEST_SELECT_PIC = 100;
    private final static int REQUEST_SELECT_FILE = 101;
    private final static int REQUEST_SHOOT = 102;
    private final static int REQUEST_CAMERA = 103;
    private final static int REQUEST_LOCATION = 104;
    protected final static int REQUEST_CLEAR_HISTORY = 105;
    private final static int REQUEST_FOR_AT = 106;
    private final static int REQUEST_VIDEO = 107;
    private final static int REQUEST_AUDIO = 108;
    public final static int REQUEST_FORWARD = 109;
    public final static int REQUEST_CARD = 110;
    public final static int REQUEST_COLLECT = 111;
    public final static int MODE_DEL = 1001;

    protected final static long BAD_TIME_INTERVAL = 5 * 60 * 1000;
    protected final static long BAD_TIME_INTERVAL_1 = 60 * 60 * 1000;
    protected final static long SECRET_TIME_INTERVAL = 60 * 1000;

    @BindView(R2.id.view_chat_title)
    View viewTitle;
    @BindView(R2.id.et_chat_input)
    EditText etInput;
    @BindView(R2.id.btn_chat_record)
    Button btnRecord;
    @BindView(R2.id.iv_chat_smile)
    ImageView ivSmile;
    @BindView(R2.id.iv_chat_add)
    ImageView ivAdd;
    @BindView(R2.id.btn_chat_send)
    Button btnSend;
    @BindView(R2.id.btn_chat_voice)
    Button btnVoice;
    @BindView(R2.id.lv_chat_list)
    ListView lvMsgList;
    @BindView(R2.id.ll_chat_face_and_func)
    LinearLayout llFuncLayout;
    @BindView(R2.id.rl_chat_bottom)
    LinearLayout rlBottomLayout;
    @BindView(R2.id.tv_chat_del)
    TextView tvDel;
    @BindView(R2.id.rl_chat_parent_layout)
    RelativeLayout rlParentLayout;
    @BindView(R2.id.rl_chat_layout)
    protected SmartRefreshLayout refreshLayout;

    @BindColor(R2.color.colorBtnBlue)
    int colorBlue;
    @BindColor(R2.color.colorActivityBackground)
    int colorActivityBackground;
    @BindString(R2.string.ace_self_ban_send_file)
    String strAceSendFile;
    @BindString(R2.string.ace_self_ban_video)
    String strAceVideo;

    @BindString(R2.string.chat_file_size_beyond)
    protected String strFileSizeBeyond;
    @BindString(R2.string.ace_client_forbidden)
    protected String strAceForbidden;
    @BindString(R2.string.ace_client_bad_word)
    protected String strBadWord;

    protected String chatId;
    protected QDChatAdapter adapter;
    protected List<QDMessage> msgList;
    private QDRecorderUtil recorderUtil;

    protected QDChatSmiley chatSmiley;
    protected QDChatFunc chatFunc;

    protected QDGridLayout smileyGrid;
    protected QDGridLayout funcGrid;

    private boolean isSmileOpen, isFucOpen;

    private float startY, endY;

    private long startTime, endTime;

    private String cameraPath;

    protected int mode;

    protected boolean isGroup;

    protected boolean isHaveDraft; //是否有草稿信息

    protected List<QDAtClickSpan> spanList;

    protected int msgAce;

    protected boolean isSignOpen;

    protected long mMsgTime;

    protected boolean isBanned;

    private List<QDMessage> messageList;

    protected HashMap<String, String> draftMap;

    protected QDUser user;
    protected QDGroup group;

    protected boolean isOpenBadWord; //是否启用敏感词过滤功能

    protected int processType; //敏感词处理方案
    protected int alarmType;  // 敏感词报警方案 0:不报警 1:报警
    protected QDAlertView warnDialog;
    private boolean isShowDialog;
    private String sendMsgId;
    protected int secretStatus; //是否开启阅后即焚功能

    protected QDChatAdapter.OnEditListener listener = new QDChatAdapter.OnEditListener() {
        @Override
        public void onEdit(String content) {
            IMBTFManager manager = new IMBTFManager(content);
            List<IMBTFItem> imbtfItemList = manager.getItemList();
            for (IMBTFItem item : imbtfItemList) {
                if (item instanceof IMBTFText) {
                    etInput.append(QDChatSmiley.getInstance(context).strToSmiley(((IMBTFText) item).getContent()));
                } else if (item instanceof IMBTFAT) {
                    String info = ((IMBTFAT) item).getContent();
                    String[] infos = info.split(";");
                    String userId = infos[0];
                    String userName = infos[1];
                    SpannableString spannableString = new SpannableString("@" + userName + " ");
                    QDAtClickSpan span = new QDAtClickSpan(context, context.getResources().getColor(R.color.colorBtnBlue), userId, userName);
                    spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    etInput.append(spannableString);
                }
            }
            etInput.setSelection(etInput.length());
            etInput.requestFocus();
            QDUtil.openKeybord(etInput, context);
        }
    };

    private QDAlertView.OnButtonClickListener alertListener = new QDAlertView.OnButtonClickListener() {
        @Override
        public void onClick(boolean isSure) {
            if (isSure) {
                QDMessageHelper.deleteMessageByIdList(adapter.getSelectedMsgId());
                if (isGroup) {
                    msgList = QDMessageHelper.getMessageWithGroupId(chatId);
                } else {
                    msgList = QDMessageHelper.getMessageWithUserId(chatId);
                }
                adapter.setMsgList(msgList);
                if (msgList == null || msgList.size() == 0) {
                    QDConversationHelper.deleteConversationById(chatId);
                }
                cancelDelUI();
            }
        }
    };

    private QDAlertView.OnButtonClickListener warnListener = new QDAlertView.OnButtonClickListener() {
        @Override
        public void onClick(boolean isSure) {
//            QDUtil.openKeybord(etInput, context);
            etInput.setText("");
            setShowDialog(false);
        }
    };

    private QDAlertView alertView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true, false);
        setContentView(R.layout.im_activity_chat);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        findViewById(R.id.view_place).setVisibility(View.GONE);
        chatId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID);
        mMsgTime = getIntent().getLongExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE_TIME, 0);
        isOpenBadWord = QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_BAD_WORD);
        processType = QDStringUtil.strToInt(QDClient.getInstance().getLoginInfo().getConfig("seword_process_type"));
        alarmType = QDStringUtil.strToInt(QDClient.getInstance().getLoginInfo().getConfig("seword_alarm_type"));
        adapter = new QDChatAdapter(context, chatId);
        lvMsgList.setAdapter(adapter);
        chatSmiley = new QDChatSmiley(this);
        smileyGrid = new QDGridLayout(this, QDGridLayout.PAGE_DEFAULT_SMILEY, chatSmiley.getSmileyList(), null);
        smileyGrid.setOnGridItemClickListener(this);
        llFuncLayout.addView(smileyGrid.getGridView(), 0);
        msgAce = QDLanderInfo.getInstance().getMsgAce();
        draftMap = new HashMap<>();
        initFunc(initFunIds());
        initAlertView();
        initWarnDialog();

        lvMsgList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideSmileAndFucLayout();
                QDUtil.closeKeybord(etInput, context);

                if (scrollState == SCROLL_STATE_IDLE) {
                    Glide.with(context).resumeRequests();
                } else {
                    Glide.with(context).pauseRequestsRecursive();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (totalItemCount > visibleItemCount) {
//                    lvMsgList.setStackFromBottom(true);
//                } else {
//                    lvMsgList.setStackFromBottom(false);
//                }
            }
        });

        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE_DEL) {
                    cancelDelUI();
                } else {
                    onBackPressed();
                }
            }
        });

        if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_WATERMARK)) {
            rlParentLayout.setBackground(QDBitmapUtil.addWaterMark(context, QDLanderInfo.getInstance().getName(), colorActivityBackground));
        } else {
            rlParentLayout.setBackground(null);
        }

        EventBus.getDefault().register(this);
    }

    protected ArrayList<Integer> initFunIds() {
        ArrayList<Integer> funIds = new ArrayList<>();

        funIds.add(R.mipmap.im_chat_func_photo);
        funIds.add(R.mipmap.im_chat_func_image);
        funIds.add(R.mipmap.im_chat_func_file);
        if (!chatId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            funIds.add(R.mipmap.im_chat_func_shoot);
            //funIds.add(R.mipmap.im_chat_func_location);
//            if (!isGroup) {
//                if (!QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_BAN_AUDIO)) {
//                    funIds.add(R.mipmap.im_chat_func_voice);
//                }
//            if (!QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_BAN_VIDEO)) {
//                funIds.add(R.mipmap.im_chat_func_video);
//            }
//            }
            // funIds.add(R.mipmap.im_chat_func_card);
        }
        // funIds.add(R.mipmap.im_chat_func_collect);
        return funIds;
    }

    protected void initFunc(ArrayList<Integer> ids) {
        if (funcGrid != null) {
            llFuncLayout.removeView(funcGrid.getGridView());
        }
        chatFunc = new QDChatFunc(this, ids);
        funcGrid = new QDGridLayout(this, QDGridLayout.PAGE_FUNCTION, chatFunc.getFuncList(), chatFunc.getFuncInfoList());
        funcGrid.setOnGridItemClickListener(this);
        llFuncLayout.addView(funcGrid.getGridView());
    }

    public void scrollListViewToBottom() {
        lvMsgList.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvMsgList.setSelection(adapter.getCount() - 1);
            }
        }, 100);
    }

    public void scrollListViewToSelection(final int selection) {
        lvMsgList.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvMsgList.setSelection(selection - 1);
            }
        }, 100);
    }

    @OnTextChanged(value = R2.id.et_chat_input, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void OnTextChange(CharSequence text, int start, int before, int count) {
        if (text.length() > 0) {
            ivAdd.setVisibility(View.GONE);
            btnSend.setVisibility(View.VISIBLE);
        } else {
            ivAdd.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.GONE);
        }
        if (isGroup) {
            if (text.length() > 0) {
                if (isHaveDraft) {
                    isHaveDraft = false;
                    return;
                }
                String c = text.subSequence(start, start + count).toString();
                if (c.equalsIgnoreCase("@")) {
                    Intent intent = new Intent(context, QDGroupMemberActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, chatId);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER_MODE, QDGroupMemberActivity.MODE_FOR_AT);
                    startActivityForResult(intent, REQUEST_FOR_AT);
                }
            }

            if (before != 0) {
                QDAtClickSpan[] spans = etInput.getText().getSpans(0, etInput.length(), QDAtClickSpan.class);
                for (QDAtClickSpan span : spans) {
                    int end = etInput.getText().getSpanEnd(span);
                    if (end == start) {
                        int spanStart = etInput.getText().getSpanStart(span);
                        etInput.getText().delete(spanStart, end);
                        spanList.remove(span);
                    }
                }
            }
        }
    }

    @OnTextChanged(value = R2.id.et_chat_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable editable) {
        setDraft();
    }

    @OnFocusChange(R2.id.et_chat_input)
    public void onFocusChange(boolean isFocus) {
        if (isFocus) {
            hideSmileAndFucLayout();
        }
    }

    @OnClick({R2.id.btn_chat_voice, R2.id.iv_chat_add, R2.id.iv_chat_smile, R2.id.et_chat_input, R2.id.tv_chat_del})
    public void OnClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_chat_voice) {
            if (isBanned) {
                return;
            }
            if (QDSDKConfig.getInstance().getIsForbidden()) {
                int forbiddenType = QDSDKConfig.getInstance().getForbiddenType();
                long offset = System.currentTimeMillis() - QDSDKConfig.getInstance().getLastBadTime();
                if (forbiddenType == 0) {
                    if (offset > BAD_TIME_INTERVAL) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                } else {
                    if (offset > BAD_TIME_INTERVAL_1) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL_1 - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL_1 - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                }
            }
            changeBottomLayout();

        } else if (i == R.id.et_chat_input) {
            hideSmileAndFucLayout();

        } else if (i == R.id.iv_chat_add) {
            if (isBanned) {
                return;
            }
            if (QDSDKConfig.getInstance().getIsForbidden()) {
                int forbiddenType = QDSDKConfig.getInstance().getForbiddenType();
                long offset = System.currentTimeMillis() - QDSDKConfig.getInstance().getLastBadTime();
                if (forbiddenType == 0) {
                    if (offset > BAD_TIME_INTERVAL) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                } else {
                    if (offset > BAD_TIME_INTERVAL_1) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL_1 - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL_1 - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                }
            }
            QDUtil.closeKeybord(etInput, context);
            if (!isFucOpen) {
                isFucOpen = true;
                showFucLayout();
            } else {
                isFucOpen = false;
                llFuncLayout.setVisibility(View.GONE);
                ivAdd.setImageResource(R.mipmap.im_chat_bottom_fun_closed);
            }

        } else if (i == R.id.iv_chat_smile) {
            if (isBanned) {
                return;
            }
            if (QDSDKConfig.getInstance().getIsForbidden()) {
                int forbiddenType = QDSDKConfig.getInstance().getForbiddenType();
                long offset = System.currentTimeMillis() - QDSDKConfig.getInstance().getLastBadTime();
                if (forbiddenType == 0) {
                    if (offset > BAD_TIME_INTERVAL) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                } else {
                    if (offset > BAD_TIME_INTERVAL_1) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL_1 - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL_1 - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                }
            }
            QDUtil.closeKeybord(etInput, context);
            if (!isSmileOpen) {
                isSmileOpen = true;
                showSmileLayout();
            } else {
                isSmileOpen = false;
                llFuncLayout.setVisibility(View.GONE);
                ivSmile.setImageResource(R.mipmap.im_chat_bottom_smiley_closed);
            }

        } else if (i == R.id.tv_chat_del) {
            if (adapter.getSelectedMsgId().size() == 0) {
                QDUtil.showToast(context, context.getResources().getString(R.string.chat_had_not_selected_delete_item));
                return;
            }
            alertView.show();

        }
    }

    @OnItemClick(R2.id.lv_chat_list)
    public void onItemClick() {
        hideSmileAndFucLayout();
        QDUtil.closeKeybord(etInput, context);
    }

    @OnTouch({R2.id.btn_chat_record, R2.id.rl_chat_layout})
    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() == R.id.rl_chat_layout) {
            hideSmileAndFucLayout();
            QDUtil.closeKeybord(etInput, context);
        } else {
            if (isBanned) {
                return true;
            }
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_SEND_FILE)) {
                QDUtil.showToast(context, strAceSendFile);
                return true;
            }
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startY = event.getRawY();
                    startTime = System.currentTimeMillis();
                    if (recorderUtil == null) {
                        recorderUtil = new QDRecorderUtil(context);
                    }
                    if (!AndPermission.hasPermissions(context, Permission.RECORD_AUDIO)
                            || !AndPermission.hasPermissions(context, Permission.WRITE_EXTERNAL_STORAGE)) {
                        QDUtil.getPermission(context, Permission.RECORD_AUDIO);
                        QDUtil.getPermission(context, Permission.WRITE_EXTERNAL_STORAGE);
                    } else {
                        recorderUtil.start();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    endY = event.getRawY();
                    if (Math.abs(endY - startY) > 50) {
                        recorderUtil.updateImage(true);
                    } else {
                        recorderUtil.updateImage(false);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (AndPermission.hasPermissions(context, Permission.RECORD_AUDIO)
                            && AndPermission.hasPermissions(context, Permission.WRITE_EXTERNAL_STORAGE)) {
                        if (recorderUtil.isStop()) {
                            break;
                        }
                        endY = event.getRawY();
                        recorderUtil.stopRecord();
                        endTime = System.currentTimeMillis();
                        if (endTime - startTime < 1000) {
                            QDUtil.showToast(context, context.getResources().getString(R.string.time_less_than_1s));
                            recorderUtil.deleteRecordFile();
                        } else {
                            if (Math.abs(endY - startY) > 50) {
                                recorderUtil.deleteRecordFile();
                            } else {
                                String path = recorderUtil.getFilePath();
                                File file = new File(path);
                                if (file.exists()) {
                                    doSendFile(file, context.getResources().getString(R.string.voice), QDMessage.MSG_TYPE_VOICE);
                                }
                            }
                        }
                    }


                    break;

            }
        }
        return true;
    }

    /**
     * 显示表情布局
     */
    private void showSmileLayout() {
        if (llFuncLayout.getVisibility() == View.GONE) {
            llFuncLayout.setVisibility(View.VISIBLE);
        }
        if (funcGrid.getGridView().getVisibility() == View.VISIBLE) {
            funcGrid.getGridView().setVisibility(View.GONE);
        }

        if (btnRecord.getVisibility() == View.VISIBLE) {
            btnRecord.setVisibility(View.GONE);
            etInput.setVisibility(View.VISIBLE);
            btnVoice.setBackgroundResource(R.drawable.btn_voice);
        }
        smileyGrid.getGridView().setVisibility(View.VISIBLE);
        ivSmile.setImageResource(R.mipmap.im_chat_bottom_smiley_opened);
        ivAdd.setImageResource(R.mipmap.im_chat_bottom_fun_closed);
        isFucOpen = false;
    }

    /**
     * 显示功能布局
     */
    private void showFucLayout() {
        if (llFuncLayout.getVisibility() == View.GONE) {
            llFuncLayout.setVisibility(View.VISIBLE);
        }
        if (smileyGrid.getGridView().getVisibility() == View.VISIBLE) {
            smileyGrid.getGridView().setVisibility(View.GONE);
        }

        if (btnRecord.getVisibility() == View.VISIBLE) {
            btnRecord.setVisibility(View.GONE);
            etInput.setVisibility(View.VISIBLE);
            btnVoice.setBackgroundResource(R.drawable.btn_voice);
        }
        funcGrid.getGridView().setVisibility(View.VISIBLE);
        ivAdd.setImageResource(R.mipmap.im_chat_bottom_fun_opened);
        ivSmile.setImageResource(R.mipmap.im_chat_bottom_smiley_closed);
        isSmileOpen = false;
    }

    /**
     * 隐藏表情和功能选择布局
     */
    protected void hideSmileAndFucLayout() {
        if (llFuncLayout.getVisibility() == View.VISIBLE) {
            llFuncLayout.setVisibility(View.GONE);
            isSmileOpen = false;
            isFucOpen = false;
            ivSmile.setImageResource(R.mipmap.im_chat_bottom_smiley_closed);
            ivAdd.setImageResource(R.mipmap.im_chat_bottom_fun_closed);
        }
    }

    /**
     * 判断是语音输入还是文字输入
     */
    private void changeBottomLayout() {
        if (btnRecord.getVisibility() == View.GONE) {
            hideSmileAndFucLayout();
            btnVoice.setBackgroundResource(R.drawable.btn_text);
            btnRecord.setVisibility(View.VISIBLE);
            etInput.setVisibility(View.GONE);
        } else {
            btnVoice.setBackgroundResource(R.drawable.btn_voice);
            btnRecord.setVisibility(View.GONE);
            etInput.setVisibility(View.VISIBLE);
        }
        QDUtil.closeKeybord(etInput, context);
    }

    /**
     * 更新消息发送状态
     *
     * @param messages
     * @param msgId
     * @param status
     */
    protected void updateMessageStatus(final List<QDMessage> messages, String msgId, int status) {
        messageList = new ArrayList<>();
        messageList.addAll(messages);
        int size = messages.size();
        for (int i = 0; i < size; i++) {
            QDMessage message = messageList.get(i);
            if (message.getMsgId().equalsIgnoreCase(msgId)) {
                messageList.remove(i);
                messageList.add(i, QDMessageHelper.getMessageById(msgId));
                break;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setMsgList(messageList);
            }
        });
    }

    /**
     * 更新消息的文件状态
     *
     * @param messages
     * @param msgId
     * @param status
     */
    protected void updateMessageFileStatus(final List<QDMessage> messages, String msgId, int status) {
        messageList = new ArrayList<>();
        messageList.addAll(messages);
        for (QDMessage message : messageList) {
            if (message.getMsgId().equalsIgnoreCase(msgId)) {
                message.setFileStatus(status);
                QDMessageHelper.updateMessage(message);
                break;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setMsgList(messageList);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            hideSmileAndFucLayout();
            QDUtil.closeKeybord(etInput, context);
            switch (requestCode) {
                case REQUEST_SELECT_PIC:
                    List<String> pathList = data.getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS);
                    boolean isArtwork = data.getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_ARTWORK, false);
                    for (String path : pathList) {
                        File file;
                        File newFile = null;
                        if (isArtwork) {
                            file = new File(path);
                        } else {
                            File actualImageFile = new File(path);
                            try {
                                file = new Compressor(this).setQuality(75).compressToFile(actualImageFile);
                                String name = path.substring(path.lastIndexOf("/"));
                                String newPath = QDStorePath.IMG_PATH + name + "_tmp";

                                newFile = new File(newPath);
                                QDUtil.copyFile(file.getPath(), newPath);
                                file.delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                                newFile = actualImageFile;
                            }
                        }
                        doSendFile(newFile, context.getResources().getString(R.string.image), QDMessage.MSG_TYPE_IMAGE);
                    }
                    break;
                case REQUEST_SELECT_FILE:
                    File file = (File) data.getSerializableExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_FILE);
                    String path = file.getPath();
                    if (!path.contains(".")) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
                        return;
                    }
                    if (file.length() == 0) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
                        return;
                    }
                    doSendFile(file, "[文件]", QDMessage.MSG_TYPE_FILE);
//                    try {
//                        String path;
//                        if (Build.VERSION.SDK_INT >= 19) {
//                            path = QDUtil.handleImageOnKitKat(context, data);
//                        } else {
//                            path = QDUtil.handleImageBeforeKitKat(context, data);
//                        }
//                        if (!path.contains(".")) {
//                            QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
//                            return;
//                        }
//                        File file = new File(path);
//                        if (file.length() == 0) {
//                            QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
//                            return;
//                        }
//                        doSendFile(new File(path), context.getResources().getString(R.string.file), QDMessage.MSG_TYPE_FILE);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    break;
                case REQUEST_SHOOT:
                    String filePath = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_SHOOT_PATH);
                    File shootFile = new File(filePath);
                    doSendFile(shootFile, context.getResources().getString(R.string.shoot), QDMessage.MSG_TYPE_SHOOT);
                    break;
                case REQUEST_CAMERA:
                    QDUtil.createBitmapFromCamera(cameraPath);
                    doSendFile(new File(cameraPath), context.getResources().getString(R.string.image), QDMessage.MSG_TYPE_IMAGE);
                    break;
                case REQUEST_LOCATION:
                    QDLocationBean locationBean = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_LOCATION);
                    String locInfo = locationBean.getLatitude() + ";" + locationBean.getLongitude() + ";"
                            + locationBean.getDetailLocationInfo() + ";" + locationBean.getSampleLocationInfo();
                    doSendLocationMessage(locInfo);
                    break;
                case REQUEST_CLEAR_HISTORY:
                    msgList.clear();
                    adapter.setMsgList(msgList);
                    finish();
                    break;
                case REQUEST_FOR_AT:
                    etInput.getText().delete(etInput.length() - 1, etInput.length());
                    etInput.setMovementMethod(LinkMovementMethod.getInstance());
                    final QDGroupMember member = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER);
                    SpannableString spannableString = new SpannableString("@" + member.getNickName());
                    QDAtClickSpan span = new QDAtClickSpan(context, colorBlue, member.getUid(), member.getNickName());
                    spanList.add(span);
                    spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    etInput.append(spannableString);
                    etInput.append(" ");
                    break;
                case REQUEST_VIDEO:

                    break;
                case REQUEST_FORWARD:
                    QDMessage message = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
                    adapter.addMsg(message);
                    break;
                case REQUEST_CARD:
                    QDUser user = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_USER);
                    doSendCard(user);
                    break;
                case REQUEST_COLLECT:
                    QDMessage msg = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
                    QDMessage sendMsg;

                    QDResultCallBack callBack = new QDResultCallBack() {
                        @Override
                        public void onError(String errorMsg) {
                            updateMessageStatus(adapter.getMsgList(), sendMsgId, 0);
                        }

                        @Override
                        public void onSuccess(Object o) {
                            updateMessageStatus(adapter.getMsgList(), sendMsgId, 0);
                        }
                    };

                    if (isGroup) {
                        sendMsg = QDClient.getInstance().forwardGMessage(group, msg, callBack);
                    } else {
                        if (secretStatus == 1) {
                            msg.setMsgFlag(QDMessage.MSGFLAG_SECRET);
                        }
                        sendMsg = QDClient.getInstance().forwardMessage(this.user, msg, callBack);
                    }
                    sendMsgId = sendMsg.getMsgId();
                    adapter.addMsg(sendMsg);
                    break;
            }
        }
    }

    protected void doSendFile(File file, String subject, String type) {

    }

    protected void doSendLocationMessage(String locInfo) {

    }

    protected void doSendCard(QDUser user) {

    }

    @Subscribe
    public void onModeChange(final QDChatModel model) {

        if (model.getMode() == MODE_DEL) {
            QDUtil.closeKeybord(etInput, context);
            mode = model.getMode();
            adapter.setMode(model.getMode());
            adapter.addDelMessage(model.getMessage());
            tvTitleRight.setVisibility(View.INVISIBLE);
            tvTitleBack.setText(R.string.cancel);
            tvTitleBack.setCompoundDrawables(null, null, null, null);
            tvDel.setVisibility(View.VISIBLE);
            hideSmileAndFucLayout();
            rlBottomLayout.setVisibility(View.GONE);
        } else {
            QDClient.getInstance().dropGroupMessage(model.getMessage(), new QDResultCallBack() {
                @Override
                public void onError(String errorMsg) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.drop_message_error));
                }

                @Override
                public void onSuccess(Object o) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.removeMsg(model.getMessage().getMsgId());
                        }
                    });
                }
            });

        }
    }

    @Subscribe
    public void onRecordFinished(File file) {
        doSendFile(file, context.getResources().getString(R.string.voice), QDMessage.MSG_TYPE_VOICE);
    }

    @Subscribe
    public void onDeleteMessage(QDMessage message) {
        int index = adapter.getIndexOfMsgId(message.getMsgId());
        if (index != -1 && index == adapter.getCount() - 1) {
            QDConversation conversation = QDConversationHelper.getConversationById(chatId);
            if (conversation != null) {
                conversation.setTime(System.currentTimeMillis() * 1000);
                if (index != 0) {
                    QDMessage msg = adapter.getItem(index - 1);
                    conversation.setSubname(msg.getSubject());
                } else {
                    conversation.setSubname("");
                }
                QDConversationHelper.updateConversation(conversation);
            }
        }
        for (QDMessage msg1 : adapter.getMsgList()) {
            if (msg1.getMsgId().equalsIgnoreCase(message.getMsgId())) {
                msgList.remove(msg1);
                adapter.setMsgList(msgList);
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        QDUtil.closeKeybord(etInput, context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDRecorderUtil.stopPlay();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onGridItemClickListener(int mode, int index, boolean isDeleteItem) {
        if (mode == QDGridLayout.PAGE_DEFAULT_SMILEY) {
            if (isDeleteItem) {
                deleteAChar();
            } else {
                insertCharsToEditText(chatSmiley.getSmileyByIndex(index));
            }
        } else if (mode == QDGridLayout.PAGE_FUNCTION) {
            Integer id = chatFunc.getFuncID(index);
            onFuncItemClick(id);
        }
    }

    protected void deleteAChar() {
        etInput.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    protected void insertCharsToEditText(CharSequence text) {
        int start = etInput.getSelectionStart();
        int end = etInput.getSelectionEnd();
        if (start != end) {
            etInput.getText().delete(start, end);
        }
        if (start < 0) {
            start = 0;
        }
        etInput.getText().insert(start, text);
        etInput.setSelection(start + text.length());
    }

    protected void onFuncItemClick(Integer itemID) {
        if (itemID == R.mipmap.im_chat_func_photo) {
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_SEND_FILE)) {
                QDUtil.showToast(context, strAceSendFile);
                return;
            }
            HiPermission.create(activity).checkSinglePermission(Permission.CAMERA, new PermissionCallback() {
                @Override
                public void onClose() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onDeny(String permission, int position) {

                }

                @Override
                public void onGuarantee(String permission, int position) {
                    cameraPath = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
                    File file = new File(cameraPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    QDUtil.startTakePhoto((Activity) context, REQUEST_CAMERA, file);
                }
            });
        } else if (itemID == R.mipmap.im_chat_func_image) {
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_SEND_FILE)) {
                QDUtil.showToast(context, strAceSendFile);
                return;
            }
            Intent imageIntent = new Intent(context, QDSelectPhotoActivity.class);
            startActivityForResult(imageIntent, REQUEST_SELECT_PIC);
        } else if (itemID == R.mipmap.im_chat_func_file) {
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_SEND_FILE)) {
                QDUtil.showToast(context, strAceSendFile);
                return;
            }
            Intent fileIntent = new Intent(context, QDFileActivity.class);
            startActivityForResult(fileIntent, REQUEST_SELECT_FILE);
        } else if (itemID == R.mipmap.im_chat_func_voice) {
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_VIDEO)) {
                QDUtil.showToast(context, strAceVideo);
                return;
            }
            if (QDClient.getInstance().getUserStatus(chatId) != 1) {
                QDUtil.showToast(context, context.getResources().getString(R.string.cannot_request_call_chat_offline));
                return;
            }
            if (!com.longchat.base.util.QDUtil.isNetworkAvailable(context)) {
                QDUtil.showToast(context, context.getResources().getString(R.string.cannot_request_call_network_bad));
                return;
            }
            if (AndPermission.hasPermissions(context, Permission.CAMERA, Permission.RECORD_AUDIO)) {
                toVideoActivity(true);
            } else {
                String[] strings = new String[]{Permission.CAMERA, Permission.RECORD_AUDIO};
                getPermission(strings, itemID);
            }
        } else if (itemID == R.mipmap.im_chat_func_video) {
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_VIDEO)) {
                QDUtil.showToast(context, strAceVideo);
                return;
            }
//            if (QDClient.getInstance().getUserStatus(chatId) != 1) {
//                QDUtil.showToast(context, context.getResources().getString(R.string.cannot_request_call_chat_offline));
//                return;
//            }
            if (!com.longchat.base.util.QDUtil.isNetworkAvailable(context)) {
                QDUtil.showToast(context, context.getResources().getString(R.string.cannot_request_call_network_bad));
                return;
            }
            List<PermissionItem> permissionItems = new ArrayList<>();
            permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, "录音", R.drawable.permission_ic_storage));
            permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "相机", R.drawable.permission_ic_camera));

            HiPermission.create(activity).permissions(permissionItems)
                    .style(R.style.PermissionBlueStyle).checkMutiPermission(new PermissionCallback() {
                @Override
                public void onClose() {

                }

                @Override
                public void onFinish() {
                    toVideoActivity(false);
                }

                @Override
                public void onDeny(String permission, int position) {
                }

                @Override
                public void onGuarantee(String permission, int position) {
                    //toVideoActivity(false);
                }
            });
        } else if (itemID == R.mipmap.im_chat_func_sgin) {
//                Animation animation = AnimationUtils.loadAnimation(context, R.anim.my_test);
//                rlParentLayout.startAnimation(animation);
            ArrayList<Integer> ids = new ArrayList<>(1);
            ids.add(R.mipmap.im_chat_func_sign_off);
            aboutSign(ids);
            etInput.setHint(R.string.im_text_sign_enter);
            isSignOpen = true;
        } else if (itemID == R.mipmap.im_chat_func_sign_off) {
            aboutSign(initFunIds());
            etInput.setHint("");
            isSignOpen = false;
        } else if (itemID == R.mipmap.im_chat_func_shoot) {
            if (QDUtil.isHaveSelfAce(msgAce, QDRolAce.ACE_MA_BAN_SEND_FILE)) {
                QDUtil.showToast(context, strAceSendFile);
                return;
            }
            List<PermissionItem> permissionItems = new ArrayList<>();
            permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, "录音", R.drawable.permission_ic_storage));
            permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "相机", R.drawable.permission_ic_camera));
            permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写", R.drawable.permission_ic_camera));

            HiPermission.create(activity).permissions(permissionItems)
                    .style(R.style.PermissionBlueStyle).checkMutiPermission(new PermissionCallback() {
                @Override
                public void onClose() {

                }

                @Override
                public void onFinish() {

                    toShootActivity();
                }

                @Override
                public void onDeny(String permission, int position) {
                }

                @Override
                public void onGuarantee(String permission, int position) {
                }
            });

        } else if (itemID == R.mipmap.im_chat_func_location) {
            HiPermission.create(activity).checkSinglePermission(Permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
                @Override
                public void onClose() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onDeny(String permission, int position) {

                }

                @Override
                public void onGuarantee(String permission, int position) {
                    Intent locationIntent = new Intent(context, QDGaoDeLocationActivity.class);
                    startActivityForResult(locationIntent, REQUEST_LOCATION);
                }
            });

        } else if (itemID == R.mipmap.im_chat_func_vote) {
        } else if (itemID == R.mipmap.im_chat_func_card) {
            Intent cardIntent = new Intent(context, QDContactActivity.class);
            cardIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_SINGLE);
            startActivityForResult(cardIntent, REQUEST_CARD);
        } else if (itemID == R.mipmap.im_chat_func_collect) {
            Intent collectIntent = new Intent(context, QDCollectActivity.class);
            collectIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, true);
            collectIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, chatId);
            collectIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, tvTitleName.getText().toString());
            collectIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_GROUP, isGroup);
            startActivityForResult(collectIntent, REQUEST_COLLECT);
        }


    }

    private void aboutSign(ArrayList<Integer> ids) {
        if (isFucOpen)
            hideSmileAndFucLayout();
        initFunc(ids);
        showFucLayout();
    }

    private void getPermission(final String[] permission, final Integer itemId) {
        AndPermission.with(context)
                .runtime()
                .permission(permission)
                .rationale(rationale)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        getPermission(permission, itemId);
                    }
                })
                .start();
    }

    private Rationale rationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, final RequestExecutor executor) {
            final QDAlertView alertDialog = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(getString(R.string.permission_hint))
                    .isHaveCancelBtn(false)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            executor.execute();
                        }
                    })
                    .build();
            alertDialog.show();

        }
    };

    private void toShootActivity() {
        Intent intent = new Intent(context, QDShootActivity.class);
        startActivityForResult(intent, REQUEST_SHOOT);
    }

    private void toVideoActivity(boolean isAudio) {
        Intent avIntent = new Intent(context, QDAVActivity.class);
        avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_AV_IS_OUT, true);
        avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, chatId);
        avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_AUDIO, isAudio);
        startActivity(avIntent);
//        Intent intent = new Intent(context, QDAudioActivity.class);
//        startActivity(intent);
    }

    private void cancelDelUI() {
//        lvMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mode = 0;
        adapter.clearSelectMessage();
        adapter.setMode(0);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleBack.setText(R.string.title_back);
        Drawable drawable = getResources().getDrawable(R.drawable.arrow_left);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvTitleBack.setCompoundDrawables(drawable, null, null, null);
        adapter.clearSelectMessage();
        adapter.notifyDataSetChanged();
        rlBottomLayout.setVisibility(View.VISIBLE);
        tvDel.setVisibility(View.GONE);
    }

    private void initAlertView() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Alert)
                .setContent(context.getResources().getString(R.string.delete_selected_messages))
                .isHaveCancelBtn(true)
                .setOnButtonClickListener(alertListener)
                .setSureText(context.getResources().getString(R.string.conversation_del))
                .build();
    }

    private void initWarnDialog() {
        warnDialog = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Alert)
                .setTitle(context.getResources().getString(R.string.im_text_reminder))
                .setContent(strBadWord)
                .setOnButtonClickListener(warnListener)
                .build();
    }

    /**
     * 读取草稿
     */
    protected void readDraft() {
        String content = QDUtil.readDraftFile();
        if (!TextUtils.isEmpty(content)) {
            draftMap = QDUtil.stringToMap(content);
            String draft = draftMap.get(chatId);
            if (!TextUtils.isEmpty(draft)) {
                isHaveDraft = true;
                etInput.setText(draft);
                etInput.setSelection(etInput.length());
            }
        }
    }

    /**
     * 设置草稿
     */
    private void setDraft() {
        String content = etInput.getText().toString();
        QDConversation conversation = QDConversationHelper.getConversationById(chatId);
        if (!TextUtils.isEmpty(content)) {
            draftMap.put(chatId, content);
            QDUtil.writeDraftFile(QDUtil.mapToString(draftMap));
        } else {
            draftMap.remove(chatId);
            QDUtil.writeDraftFile(QDUtil.mapToString(draftMap));
        }
        if (conversation == null) {
            conversation = new QDConversation();
            conversation.setId(chatId);
            if (isGroup) {
                conversation.setName(group.getName());
                conversation.setIcon(group.getIcon());
                conversation.setType(QDConversation.TYPE_GROUP);
            } else {
                conversation.setName(user.getName());
                conversation.setIcon(user.getPic());
                if (chatId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                    conversation.setType(QDConversation.TYPE_SELF);
                } else {
                    conversation.setType(QDConversation.TYPE_PERSONAL);
                }
            }
            conversation.setTime(System.currentTimeMillis());
            QDConversationHelper.insertConversation(conversation);
        } else {
            QDConversationHelper.updateConversationTime(chatId, System.currentTimeMillis());
        }
    }

    protected void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }

    protected void showBadTimeDialog() {
        setShowDialog(true);
        QDUtil.closeKeybord(etInput, context);
        HideSoftInput();
        if (processType == 0) {
            warnDialog.setContent(strBadWord);
            QDSDKConfig.getInstance().setLastBadTime(System.currentTimeMillis());
        } else {
            long lastBadTime = QDSDKConfig.getInstance().getLastBadTime();
            long offset = System.currentTimeMillis() - lastBadTime;
            if (offset > BAD_TIME_INTERVAL) {
                warnDialog.setContent(strBadWord);
                QDSDKConfig.getInstance().setLastBadTime(System.currentTimeMillis());
            } else {
                if (processType == 1) {
                    int index = (int) (((BAD_TIME_INTERVAL - offset) / 1000) / 60);
                    if ((((BAD_TIME_INTERVAL - offset) / 1000) % 60) != 0) {
                        index += 1;
                    }

                    warnDialog.setContent(String.format(strAceForbidden, index));
                    QDSDKConfig.getInstance().setIsForbidden(true);
                    QDSDKConfig.getInstance().setForbiddenType(0);
                } else if (processType == 2) {
                    QDSDKConfig.getInstance().setLastBadTime(System.currentTimeMillis());
                    QDSDKConfig.getInstance().setIsForbidden(true);
                    QDSDKConfig.getInstance().setForbiddenType(1);
                    warnDialog.setContent(String.format(strAceForbidden, 60));
                }
            }
        }
        warnDialog.show();
    }

    /**
     * 获取发送敏感词警报的参数
     *
     * @param content
     * @return
     */
    protected String getBadData(String content) {
        Set<String> set = QDBadWordUtil.getBadWord(content);
        JsonObject data = new JsonObject();
        data.addProperty("send_id", QDLanderInfo.getInstance().getId());
        data.addProperty("send_name", QDLanderInfo.getInstance().getName());
        String selfDeptInfo = QDLanderInfo.getInstance().getSelfDeptInfo();
        int index = selfDeptInfo.lastIndexOf(",");
        data.addProperty("send_dept", selfDeptInfo.substring(index + 1));
        StringBuilder subject = new StringBuilder();
        for (String str : set) {
            subject.append(str).append("、");
        }
        String s = subject.toString();
        data.addProperty("send_subject", s.substring(0, s.length() - 1));
        data.addProperty("send_content", content);
        data.addProperty("recv_id", chatId);
        data.addProperty("recv_name", tvTitleName.getText().toString());
        if (isGroup) {
            data.addProperty("service_type", 1);
        } else {
            data.addProperty("service_type", 0);
        }
        return data.toString();
    }

    @Override
    public void onBackPressed() {
        if (!isShowDialog)
            super.onBackPressed();
    }
}
