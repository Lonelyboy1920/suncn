package com.qd.longchat.acc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.model.gd.QDAccArticleModel;
import com.longchat.base.notify.QDNFAppInfo;
import com.longchat.base.util.QDGson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.acc.activity.QDAccChatActivity;
import com.qd.longchat.acc.holder.QDAccChatHolder;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/27 下午5:25
 */
public class QDAccChatAdapter extends BaseAdapter {

    private final static int TYPE_SINGLE_TELETEXT = 0;
    private final static int TYPE_ALL_TEXT = 1;
    private final static int TYPE_MULTI_TELETEXT_TOP = 2;
    private final static int TYPE_MULTI_TELETEXT = 3;

    private Context context;
    private List<QDMessage> messageList;
    private Map<String, View> viewMap;

    public QDAccChatAdapter(Context context, List<QDMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
        viewMap = new HashMap<>();
    }

    public void addMessageList(List<QDMessage> msgList) {
        this.messageList.addAll(msgList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messageList.size();
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
        QDAccChatHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_acc_chat, null);
            holder = new QDAccChatHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDAccChatHolder) convertView.getTag();
        }
        QDMessage message = messageList.get(position);
        holder.tvMsgTime.setText(QDDateUtil.getMsgTime(message.getCreateDate()/1000));
        View view = viewMap.get(message.getMsgId());

        QDNFAppInfo info = QDGson.getGson().fromJson(message.getContent(), QDNFAppInfo.class);
        String article = info.getArticles();
        String test = QDUtil.decodeString(article);
        //blockType 1 图文 2 单文本
        List<QDAccArticleModel> articleList = QDGson.getGson().fromJson(test, new TypeToken<List<QDAccArticleModel>>(){}.getType());
        holder.llContainerLayout.removeAllViews();
        if (info.getBlockType() == 1) {
            int size = articleList.size();
            if (size == 1) {
                String id = articleList.get(0).getId();
                getContentView(holder, id, TYPE_SINGLE_TELETEXT, articleList.get(0), message.getCreateDate(), info.getUrlMobile());
            } else {
                for (int i=0; i<size; i++) {
                    QDAccArticleModel model = articleList.get(i);
                    String id = model.getId();
                    if (i == 0) {
                        getContentView(holder, id, TYPE_MULTI_TELETEXT_TOP, model, 0, info.getUrlMobile());
                    } else {
                        getContentView(holder, id, TYPE_MULTI_TELETEXT, model, 0, info.getUrlMobile());
                    }
                }
            }
        } else {
            String id = articleList.get(0).getId();
            getContentView(holder, id, TYPE_ALL_TEXT, articleList.get(0), message.getCreateDate(), "");
        }
        return convertView;
    }


    private void getContentView(QDAccChatHolder holder, String id, int type, QDAccArticleModel model, long msgTime, String articleUrl) {
        View view = viewMap.get(id);
        if (view == null) {
            switch (type) {
                case TYPE_SINGLE_TELETEXT:
                    view = addSingleView(model, msgTime, articleUrl);
                    break;
                case TYPE_ALL_TEXT:
                    view = addSingleTextView(model, msgTime);
                    break;
                case TYPE_MULTI_TELETEXT:
                    view = addMultiView(model, articleUrl);
                    break;
                case TYPE_MULTI_TELETEXT_TOP:
                    view = addMultiTopView(model, articleUrl);
                    break;
            }
            viewMap.put(id, view);
        }
        ViewGroup viewParent = (ViewGroup) view.getParent();
        if (viewParent != null) {
            viewParent.removeAllViews();
        }
        holder.llContainerLayout.addView(view);
    }

    /**
     * 创建单图文view
     * @param model
     * @param msgTime
     */
    private View addSingleView(final QDAccArticleModel model, long msgTime, final String articleUrl) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_acc_single_teletext, null);
        TextView tvTitle = view.findViewById(R.id.tv_acc_title);
        TextView tvTime = view.findViewById(R.id.tv_acc_time);
        ImageView ivPhoto = view.findViewById(R.id.iv_acc_image);
        TextView tvDesc = view.findViewById(R.id.tv_acc_desc);

        tvTitle.setText(model.getTitle());
        tvTime.setText(QDDateUtil.longToString(msgTime / 1000, QDDateUtil.FILE_FORMAT));
        String url = model.getCover();
        ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(url), ivPhoto, QDApplication.options);
        tvDesc.setText(model.getIntro());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webUrl = QDUtil.replaceWebServerAndToken(articleUrl).replace("[id]", model.getId());
                ((QDAccChatActivity) context).toWebActivity(webUrl);
            }
        });
        return view;
    }

    /**
     * 创建纯文本view
     * @param model
     * @param msgTime
     */
    private View addSingleTextView(QDAccArticleModel model, long msgTime) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_acc_all_text, null);
        TextView tvTitle = view.findViewById(R.id.tv_acc_title);
        TextView tvTime = view.findViewById(R.id.tv_acc_time);
        WebView webView = view.findViewById(R.id.tv_acc_webview);
        ImageView ivLine = view.findViewById(R.id.tv_acc_line);
        RelativeLayout rlBottomLayout = view.findViewById(R.id.tv_bottom_layout);
        final String content = model.getContent();
        String data = "<div style=\"word-break:break-all\">" + content + "</div>";
        webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
        tvTitle.setText(model.getTitle());
        tvTime.setText(QDDateUtil.longToString(msgTime / 1000, QDDateUtil.FILE_FORMAT));
        final String url = model.getUrl();
        if (TextUtils.isEmpty(url)) {
            ivLine.setVisibility(View.GONE);
            rlBottomLayout.setVisibility(View.GONE);
        } else {
            ivLine.setVisibility(View.VISIBLE);
            rlBottomLayout.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((QDAccChatActivity) context).toWebActivity(url);
                }
            });
        }
        return view;
    }

    /**
     * 创建多图文顶部view
     * @param model
     * @param articleUrl
     * @return
     */
    private View addMultiTopView(final QDAccArticleModel model, final String articleUrl) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_acc_multi_top, null);
        ImageView ivImage = view.findViewById(R.id.iv_acc_image);
        TextView tvTitle= view.findViewById(R.id.tv_acc_title);
        String url = model.getCover();
        ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(url), ivImage, QDApplication.options);
        tvTitle.setText(model.getTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webUrl = QDUtil.replaceWebServerAndToken(articleUrl).replace("[id]", model.getId());
                ((QDAccChatActivity) context).toWebActivity(webUrl);
            }
        });
        return view;
    }

    private View addMultiView(final QDAccArticleModel model, final String articleUrl) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_acc_multi, null);
        ImageView ivImage = view.findViewById(R.id.iv_acc_image);
        TextView tvTitle= view.findViewById(R.id.tv_acc_title);
        String url = model.getCover();
        ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(url), ivImage, QDApplication.options);
        tvTitle.setText(model.getTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webUrl = QDUtil.replaceWebServerAndToken(articleUrl).replace("[id]", model.getId());
                ((QDAccChatActivity) context).toWebActivity(webUrl);
            }
        });
        return view;
    }
}
