package com.qd.longchat.util;

import android.text.TextUtils;

import com.qd.longchat.activity.QDSearchActivity;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDGroupMemberHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.model.QDSearchInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/5 下午4:18
 */
public class QDSearchUtil {

    /**
     * 搜索用户
     *
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchUser(String searchText) {
        List<QDUser> userList = QDUserHelper.searchUser(searchText);
        List<QDSearchInfo> infoList = new ArrayList<>();
        for (QDUser user : userList) {
            infoList.add(createUserSearch(user));
        }
        return infoList;
    }

    private static QDSearchInfo createUserSearch(QDUser user) {
        QDSearchInfo info = new QDSearchInfo();
        info.setId(user.getId());
        info.setName(user.getName());
        info.setIcon(user.getPic());
        info.setType(QDSearchActivity.SEARCH_TYPE_USER);
        info.setSubname(user.getNote());
        return info;
    }

    /**
     * 搜索群组
     *
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchGroup(String searchText) {
        List<QDGroup> groupList = QDGroupHelper.searchGroup(searchText);
        List<QDSearchInfo> infoList = new ArrayList<>();
        for (QDGroup group : groupList) {
            infoList.add(createGroupSearch(group));
        }
        return infoList;
    }

    private static QDSearchInfo createGroupSearch(QDGroup group) {
        QDSearchInfo info = new QDSearchInfo();
        info.setId(group.getId());
        info.setName(group.getName());
        if (!TextUtils.isEmpty(group.getIcon())) {
            String fsHost;
            if (TextUtils.isEmpty(QDLanderInfo.getInstance().getFileServer())) {
                fsHost = "http://" + QDLanderInfo.getInstance().getAddress() + ":9001";
            } else {
                fsHost = QDLanderInfo.getInstance().getFileServer();
            }
            info.setIcon(fsHost + group.getIcon());
        }
        info.setType(QDSearchActivity.SEARCH_TYPE_GROUP);
        return info;
    }

    /**
     * 搜索最近会话（包括单聊，群聊和联系人）
     *
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchMessage(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        searchInfoList.addAll(searchUserForConversation(searchText));
        searchInfoList.addAll(searchGroupForConversation(searchText));
        searchInfoList.addAll(searchPersonalMessage(searchText));
        searchInfoList.addAll(searchGroupMessage(searchText));
        return searchInfoList;
    }

    private static List<QDSearchInfo> searchUserForConversation(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        List<QDUser> userList = QDUserHelper.searchUser(searchText);

        int size = userList.size();
        boolean isVisible = false;

        if (size > 3) {
            userList = userList.subList(0, 3);
            size = 3;
            isVisible = true;
        }
        for (int i = 0; i < size; i++) {
            QDUser user = userList.get(i);
            String title = "";
            String moreText = "";
            boolean isMore = false;
            if (i == 0) {
                title = "联系人";
            } else if (i == 2) {
                if (isVisible) {
                    isMore = true;
                    moreText = "查看更多联系人";
                }
            }
            searchInfoList.add(createUserSearch(user, isMore, title, moreText));
        }
        return searchInfoList;
    }

    private static List<QDSearchInfo> searchGroupForConversation(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        List<QDGroup> groupList = QDGroupHelper.searchGroup(searchText);
        int size = groupList.size();
        boolean isVisible = false;

        if (size > 3) {
            groupList = groupList.subList(0, 3);
            size = 3;
            isVisible = true;
        }
        for (int i = 0; i < size; i++) {
            QDGroup group = groupList.get(i);
            String title = "";
            String moreText = "";
            boolean isMore = false;
            if (i == 0) {
                title = "群组";
            } else if (i == 2) {
                if (isVisible) {
                    isMore = true;
                    moreText = "查看更多群组";
                }
            }
            searchInfoList.add(createGroupSearch(group, isMore, title, moreText));
        }
        return searchInfoList;
    }

    private static QDSearchInfo createUserSearch(QDUser user, boolean isMore, String title, String moreText) {
        QDSearchInfo info = new QDSearchInfo();
        info.setId(user.getId());
        info.setName(user.getName());
        info.setIcon(user.getPic());
        info.setType(QDSearchActivity.SEARCH_TYPE_CONVERSATION);
        info.setItemType(QDSearchInfo.ITEM_TYPE_USER);
        info.setSubname(user.getNote());
        info.setTitle(title);
        info.setMore(isMore);
        info.setMoreText(moreText);
        return info;
    }

    private static QDSearchInfo createGroupSearch(QDGroup group, boolean isMore, String title, String moreText) {
        QDSearchInfo info = new QDSearchInfo();
        info.setId(group.getId());
        info.setName(group.getName());
        if (!TextUtils.isEmpty(group.getIcon())) {
            String fsHost;
            if (TextUtils.isEmpty(QDLanderInfo.getInstance().getFileServer())) {
                fsHost = "http://" + QDLanderInfo.getInstance().getAddress() + ":9001";
            } else {
                fsHost = QDLanderInfo.getInstance().getFileServer();
            }
            info.setIcon(fsHost + group.getIcon());
        }
        info.setType(QDSearchActivity.SEARCH_TYPE_CONVERSATION);
        info.setItemType(QDSearchInfo.ITEM_TYPE_GROUP);
        info.setTitle(title);
        info.setMore(isMore);
        info.setMoreText(moreText);
        return info;
    }


    private static List<QDSearchInfo> searchPersonalMessage(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        List<QDMessage> messageList = QDMessageHelper.searchPersonalMessage(QDLanderInfo.getInstance().getId(), searchText);

        int size = messageList.size();
        boolean isVisible = false;

        if (size > 3) {
            messageList = messageList.subList(0, 3);
            size = 3;
            isVisible = true;
        }
        for (int i = 0; i < size; i++) {
            QDMessage message = messageList.get(i);
            String title = "";
            String moreText = "";
            boolean isMore = false;
            if (i == 0) {
                title = "聊天消息";
            } else if (i == 2) {
                if (isVisible) {
                    isMore = true;
                    moreText = "查看更多聊天记录";
                }
            }
            searchInfoList.add(createPersonalSearch(message, isMore, title, moreText));
        }
        return searchInfoList;
    }

    /**
     * 搜索全部的单聊记录
     *
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchPersonalMessageForMore(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        List<QDMessage> messageList = QDMessageHelper.searchPersonalMessage(QDLanderInfo.getInstance().getId(), searchText);
        for (QDMessage message : messageList) {
            searchInfoList.add(createPersonalSearch(message, false, "", ""));
        }
        return searchInfoList;
    }

    private static QDSearchInfo createPersonalSearch(QDMessage message, boolean isMore, String title, String moreText) {
        QDSearchInfo searchInfo = new QDSearchInfo();
        String senderId = message.getSenderId();
        String receiverId = message.getReceiverId();
        QDUser user;
        if (senderId.equals(QDLanderInfo.getInstance().getId())) {
            user = QDUserHelper.getUserById(receiverId);
        } else {
            user = QDUserHelper.getUserById(senderId);
        }
        searchInfo.setId(user.getId());
        searchInfo.setName(user.getName());
        searchInfo.setIcon(user.getPic());
        searchInfo.setCount(message.getCount());
        if (message.getCount() > 1) {
            searchInfo.setSubname(message.getCount() + "条相关消息记录");
        } else {
            searchInfo.setSubname(message.getSubject());
        }
        searchInfo.setType(QDSearchActivity.SEARCH_TYPE_CONVERSATION);
        searchInfo.setItemType(QDSearchInfo.ITEM_TYPE_PERSON_CHAT);
        searchInfo.setMore(isMore);
        searchInfo.setTitle(title);
        searchInfo.setMoreText(moreText);
        searchInfo.setTime(message.getCreateDate());
        return searchInfo;
    }

    /**
     * 搜索全部的群聊记录
     *
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchGroupMessageFroMore(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        List<QDMessage> messageList = QDMessageHelper.searchGroupMessage(searchText);
        for (QDMessage message : messageList) {
            searchInfoList.add(createGroupMessage(message, false, "", ""));
        }
        return searchInfoList;
    }

    private static List<QDSearchInfo> searchGroupMessage(String searchText) {
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        List<QDMessage> messageList = QDMessageHelper.searchGroupMessage(searchText);

        int size = messageList.size();
        boolean isVisible = false;

        if (size > 3) {
            messageList = messageList.subList(0, 3);
            size = 3;
            isVisible = true;
        }
        for (int i = 0; i < size; i++) {
            QDMessage message = messageList.get(i);
            String title = "";
            String moreText = "";
            boolean isMore = false;
            if (i == 0) {
                title = "群聊天消息";
            } else if (i == 2) {
                if (isVisible) {
                    isMore = true;
                    moreText = "查看更多群聊天记录";
                }
            }
            QDSearchInfo searchInfo = createGroupMessage(message, isMore, title, moreText);
            if (searchInfo != null) {
                searchInfoList.add(searchInfo);
            }
        }
        return searchInfoList;
    }

    private static QDSearchInfo createGroupMessage(QDMessage message, boolean isMore, String title, String moreText) {
        QDSearchInfo searchInfo = new QDSearchInfo();
        String groupId = message.getGroupId();
        QDGroup group = QDGroupHelper.getGroupById(groupId);
        if (group == null) {
            return null;
        }
        searchInfo.setId(group.getId());
        searchInfo.setName(group.getName());
        searchInfo.setIcon(group.getIcon());
        searchInfo.setCount(message.getCount());
        if (message.getCount() > 1) {
            searchInfo.setSubname(message.getCount() + "条相关消息记录");
        } else {
            searchInfo.setSubname(message.getSubject());
        }
        searchInfo.setType(QDSearchActivity.SEARCH_TYPE_CONVERSATION);
        searchInfo.setItemType(QDSearchInfo.ITEM_TYPE_GROUP_CHAT);
        searchInfo.setMore(isMore);
        searchInfo.setTitle(title);
        searchInfo.setMoreText(moreText);
        searchInfo.setTime(message.getCreateDate());
        return searchInfo;
    }

    /**
     * 搜索和某个用户的聊天记录
     *
     * @param chatId
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchUserHistory(String chatId, String searchText) {
        List<QDMessage> messageList = QDMessageHelper.searchMessageWithChatId(chatId, searchText);
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        for (QDMessage message : messageList) {
            searchInfoList.add(createPersonalSearch(chatId, message));
        }
        return searchInfoList;
    }

    private static QDSearchInfo createPersonalSearch(String chatId, QDMessage message) {
        QDSearchInfo searchInfo = new QDSearchInfo();
        QDUser user = QDUserHelper.getUserById(chatId);
        searchInfo.setId(user.getId());
        searchInfo.setName(user.getName());
        searchInfo.setIcon(user.getPic());
        searchInfo.setSubname(message.getSubject());
        searchInfo.setTime(message.getCreateDate());
        searchInfo.setType(QDSearchActivity.SEARCH_TYPE_PERSONAL_HISTORY);
        return searchInfo;
    }

    /**
     * 搜索某个群组的聊天记录
     *
     * @param groupId
     * @param searchText
     * @return
     */
    public static List<QDSearchInfo> searchGroupHistory(String groupId, String searchText) {
        List<QDMessage> messageList = QDMessageHelper.searchMessageWithGroupId(groupId, searchText);
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        for (QDMessage message : messageList) {
            searchInfoList.add(createGroupSearch(message));
        }
        return searchInfoList;
    }

    private static QDSearchInfo createGroupSearch(QDMessage message) {
        QDSearchInfo searchInfo = new QDSearchInfo();
        QDGroup group = message.getGroup();
        searchInfo.setId(group.getId());
        searchInfo.setName(group.getName());
        searchInfo.setIcon(group.getIcon());
        searchInfo.setSubname(message.getSubject());
        searchInfo.setTime(message.getCreateDate());
        searchInfo.setType(QDSearchActivity.SEARCH_TYPE_GROUP_HISTORY);
        return searchInfo;
    }

    public static List<QDSearchInfo> searchGroupMember(String groupId, String searchText) {
        List<QDGroupMember> memberList = QDGroupMemberHelper.searchMember(groupId, searchText);
        List<QDSearchInfo> searchInfoList = new ArrayList<>();
        for (QDGroupMember member : memberList) {
            searchInfoList.add(createMemberSearch(member));
        }
        return searchInfoList;
    }

    private static QDSearchInfo createMemberSearch(QDGroupMember member) {
        QDSearchInfo searchInfo = new QDSearchInfo();
        searchInfo.setId(member.getUid());
        searchInfo.setName(member.getName());
        searchInfo.setIcon(member.getIcon());
        searchInfo.setType(QDSearchActivity.SEARCH_TYPE_GROUP_MEMBER);
        return searchInfo;
    }

}
