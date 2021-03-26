package com.qd.longchat.util;

import android.text.TextUtils;

import com.longchat.base.dao.QDCompany;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDFriend;
import com.longchat.base.dao.QDLinkman;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDCompanyHelper;
import com.longchat.base.databases.QDFriendHelper;
import com.longchat.base.databases.QDFriendInviteHelper;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDLinkmanHelper;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.model.QDContact;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/1 上午9:19
 */

public class QDContactUtil {

    public static QDContact createGroupContact() {
        QDContact rootContact = new QDContact();
        rootContact.setType(QDContact.TYPE_GROUP);
        int count = QDGroupHelper.getGroupCount();
        rootContact.setFuncName(count + "");
        rootContact.setName("工作群组");
        return rootContact;
    }

    public static QDContact createNewFriendContact() {
        QDContact rootContact = new QDContact();
        rootContact.setType(QDContact.TYPE_NEW_FRIEND);
        rootContact.setName("新的好友");
        int count = QDFriendInviteHelper.getUnreadCount();
        if (count != 0) {
            rootContact.setFuncName(QDFriendInviteHelper.getUnreadCount() + "");
        }
        return rootContact;
    }

    public static QDContact createSelfDeptContact() {
        QDContact rootContact = new QDContact();
        rootContact.setType(QDContact.TYPE_SELF_DEPT);
        rootContact.setName("我的部门");
        String deptInfo = QDLanderInfo.getInstance().getSelfDeptInfo();
        if (!TextUtils.isEmpty(deptInfo)) {
            try {
                if (deptInfo.contains(";")) {
                    String[] depts = deptInfo.split(";");
                    String[] infos = depts[0].split(",");
                    rootContact.setFuncName(infos[2]);
                    rootContact.setId(infos[1]);
                } else {
                    String[] infos = deptInfo.split(",");
                    rootContact.setFuncName(infos[2]);
                    rootContact.setId(infos[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootContact;
    }

    public static QDContact createContact() {
        QDContact rootContact = new QDContact();
        rootContact.setType(QDContact.TYPE_CONTACT);
        rootContact.setName("通讯录");
        return rootContact;
    }

    public static List<QDContact> createRootContactUsers() {
        List<QDFriend> friendList = QDFriendHelper.getAllFriend();
        List<QDLinkman> linkmanList = QDLinkmanHelper.getAll();
        List<QDContact> contactList = new ArrayList<>();
        for (QDFriend friend : friendList) {
            contactList.add(createFriendContact(friend));
        }

        if (!QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET)) {
            for (QDLinkman linkman : linkmanList) {
                contactList.add(createLinkmanContact(linkman, true));
            }
        } else {
            List<String> ids = QDUserHelper.getUserIdsLtSelfLevel();
            for (QDLinkman linkman : linkmanList) {
                if (ids.contains(linkman.getId())) {
                    contactList.add(createLinkmanContact(linkman, false));
                } else {
                    contactList.add(createLinkmanContact(linkman, true));
                }
            }
        }
        return contactList;
    }

    public static QDContact createFriendContact(QDFriend friend) {
        QDContact contact = new QDContact();
        contact.setId(friend.getId());
        contact.setName(friend.getName());
        contact.setIcon(friend.getIcon());
        contact.setSubname(friend.getNote());
        contact.setNameSp(friend.getNameSP());
        contact.setNameAp(friend.getNameAP());
        if (TextUtils.isEmpty(friend.getNameSP())) {
            contact.setFuncName(QDUtil.getInitial(friend.getName()));
        } else {
            contact.setFuncName(QDUtil.getInitial(friend.getNameSP()));
        }
        contact.setType(QDContact.TYPE_FRIEND);
        contact.setCode(friend.getMobile());
        return contact;
    }

    public static QDContact createLinkmanContact(QDLinkman linkman, boolean isVisible) {
        QDContact contact = new QDContact();
        contact.setId(linkman.getId());
        contact.setName(linkman.getName());
        contact.setIcon(linkman.getIcon());
        contact.setSubname(linkman.getNote());
        contact.setNameSp(linkman.getNameSP());
        contact.setNameAp(linkman.getNameAP());
        if (TextUtils.isEmpty(linkman.getNameSP())) {
            contact.setFuncName(QDUtil.getInitial(linkman.getName()));
        } else {
            contact.setFuncName(QDUtil.getInitial(linkman.getNameSP()));
        }
        contact.setType(QDContact.TYPE_LINKMAN);
        if (isVisible) {
            contact.setCode(linkman.getMobile());
        }
        return contact;
    }

    public static QDContact createDeptContact(QDDept dept) {
        QDContact contact = new QDContact();
        contact.setId(dept.getId());
        contact.setName(dept.getName());
        contact.setType(QDContact.TYPE_DEPT);
        contact.setCode(dept.getCode());
        contact.setCid(dept.getCid());
        return contact;
    }

    public static QDContact createUserContact(QDUser user) {
        QDContact contact = new QDContact();
        contact.setId(user.getId());
        contact.setName(user.getName());
        contact.setSubname(user.getNote());
        contact.setType(QDContact.TYPE_USER);
        contact.setIcon(user.getPic());
        contact.setLevel(user.getLevel());
        return contact;
    }

    public static QDContact createCompanyContact(QDCompany company) {
        QDContact contact = new QDContact();
        contact.setId(company.getId());
        contact.setType(QDContact.TYPE_COMPANY);
        contact.setName(company.getName());
        contact.setFuncName(QDCompanyHelper.getUserCountByCid(company.getId()) + "");
        return contact;
    }
}
