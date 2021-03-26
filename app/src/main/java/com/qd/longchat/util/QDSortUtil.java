package com.qd.longchat.util;


import com.longchat.base.dao.QDCompany;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDDownloadInfo;
import com.longchat.base.dao.QDFriend;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDLinkman;
import com.longchat.base.dao.QDUploadInfo;
import com.longchat.base.dao.QDUser;
import com.longchat.base.model.QDCollectMessage;
import com.longchat.base.model.gd.QDAccModel;
import com.longchat.base.model.gd.QDMeetingModel;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.model.QDFile;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/31 下午2:57
 */

public class QDSortUtil {

    public static List<QDCompany> sortCompany(List<QDCompany> companyList) {
        Collections.sort(companyList, new Comparator<QDCompany>() {
            @Override
            public int compare(QDCompany o1, QDCompany o2) {
                return (int) (o1.getCreateDate() - o1.getCreateDate());
            }
        });
        return companyList;
    }

    public static List<QDDept> sortDept(List<QDDept> deptList) {
        Collections.sort(deptList, new Comparator<QDDept>() {
            @Override
            public int compare(QDDept o1, QDDept o2) {
                int sort1 = o1.getSort();
                int sort2 = o2.getSort();
                if (sort1 != sort2) {
                    return sort1 - sort2;
                }
                String deptId1 = o1.getId();
                String deptId2 = o2.getId();
                return deptId1.compareToIgnoreCase(deptId2);
            }
        });
        return deptList;
    }

    public static List<QDUser> sortUser(List<QDUser> userList) {
        Collections.sort(userList, new Comparator<QDUser>() {
            @Override
            public int compare(QDUser o1, QDUser o2) {
                /**
                 * 通讯录人员排序（在线状态(可配置) > 人员排序号 > 人员名字简拼 > 人员id）
                 */
//                if (!QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_BAN_SORT_ONLINE)) {
//                    int status1 = QDClient.getInstance().getUserStatus(o1.getId());
//                    int status2 = QDClient.getInstance().getUserStatus(o2.getId());
//                    if (status1 != 0) {
//                        status1 = 1;
//                    }
//                    if (status2 != 0) {
//                        status2 = 1;
//                    }
//                    if (status1 != status2) {
//                        return status2 - status1;
//                    }
//                }

                int sort1 = o1.getIndex();
                int sort2 = o2.getIndex();
                if (sort1 != sort2) {
                    return sort1 - sort2;
                }

                String nameSP1 = o1.getNameSP();
                String nameSP2 = o2.getNameSP();
                int result1 = compareString(nameSP1, nameSP2);
                if (result1 != 0) {
                    return result1;
                }

//                String nameAP1 = o1.getNameAP();
//                String nameAP2 = o2.getNameAP();
//                int result2 = compareString(nameAP1, nameAP2);
//                if (result2 != 0) {
//                    return result2;
//                }

                return o1.getId().compareToIgnoreCase(o2.getId());
            }
        });
        return userList;
    }

    public static List<QDGroupMember> sortGroupMember(List<QDGroupMember> memberList) {
        Collections.sort(memberList, new Comparator<QDGroupMember>() {
            @Override
            public int compare(QDGroupMember o1, QDGroupMember o2) {
                int role1 = o1.getRole();
                int role2 = o2.getRole();
                if (role1 != role2) {
                    return role2 - role1;
                }

                return o1.getUid().compareToIgnoreCase(o2.getUid());
            }
        });
        return memberList;
    }

    public static List<QDContact> sortContact(List<QDContact> contactList) {
        Collections.sort(contactList, new Comparator<QDContact>() {
            @Override
            public int compare(QDContact o1, QDContact o2) {
                String nameSP1 = o1.getNameSp();
                String nameSP2 = o2.getNameSp();
                int result1 = compareString(nameSP1, nameSP2);
                if (result1 != 0) {
                    return result1;
                }
                String nameAP1 = o1.getNameAp();
                String nameAP2 = o2.getNameAp();
                int result2 = compareString(nameAP1, nameAP2);
                if (result2 != 0) {
                    return result2;
                }
                return o1.getId().compareToIgnoreCase(o2.getId());
            }
        });
        return contactList;
    }

    public static List<QDFriend> sortFriend(List<QDFriend> friendList) {
        Collections.sort(friendList, new Comparator<QDFriend>() {
            @Override
            public int compare(QDFriend o1, QDFriend o2) {
                String nameSP1 = o1.getNameSP();
                String nameSP2 = o2.getNameSP();
                int result1 = compareString(nameSP1, nameSP2);
                if (result1 != 0) {
                    return result1;
                }

                String nameAP1 = o1.getNameAP();
                String nameAP2 = o2.getNameAP();
                int result2 = compareString(nameAP1, nameAP2);
                if (result2 != 0) {
                    return result2;
                }
                return o1.getId().compareToIgnoreCase(o2.getId());
            }
        });
        return friendList;
    }

    public static List<QDLinkman> sortLinkman(List<QDLinkman> friendList) {
        Collections.sort(friendList, new Comparator<QDLinkman>() {
            @Override
            public int compare(QDLinkman o1, QDLinkman o2) {
                String nameSP1 = o1.getNameSP();
                String nameSP2 = o2.getNameSP();
                int result1 = compareString(nameSP1, nameSP2);
                if (result1 != 0) {
                    return result1;
                }

                String nameAP1 = o1.getNameAP();
                String nameAP2 = o2.getNameAP();
                int result2 = compareString(nameAP1, nameAP2);
                if (result2 != 0) {
                    return result2;
                }
                return o1.getId().compareToIgnoreCase(o2.getId());
            }
        });
        return friendList;
    }


    public static List<File> sortFile(List<File> fileList) {
        Collections.sort(fileList, new Comparator<File>() {

            @Override
            public int compare(File file1, File file2) {
                if (file1.isDirectory() && file2.isDirectory()) {
                    if (file1.lastModified() == file2.lastModified()) {
                        return 0;
                    }
                    return file2.lastModified() - file1.lastModified() > 0 ? 1 : -1;
                }

                if (file1.isDirectory()) {
                    return -1;
                }

                if (file2.isDirectory()) {
                    return 1;
                }

                if (file1.lastModified() == file2.lastModified()) {
                    return 0;
                }
                return file2.lastModified() - file1.lastModified() > 0 ? 1 : -1;
            }
        });
        return fileList;
    }

    private static int compareString(String name1, String name2) {
        try {
            return name1.compareToIgnoreCase(name2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<QDCollectMessage> sortCollect(List<QDCollectMessage> messageList) {
        Collections.sort(messageList, new Comparator<QDCollectMessage>() {
            @Override
            public int compare(QDCollectMessage o1, QDCollectMessage o2) {
                return (int) (o2.getCreateDate() - o1.getCreateDate());
            }
        });
        return messageList;
    }

    public static List<QDFile> sorSelfFile(List<QDFile> fileList) {
        Collections.sort(fileList, new Comparator<QDFile>() {
            @Override
            public int compare(QDFile o1, QDFile o2) {
                long date1 = o1.getDate();
                long date2 = o2.getDate();
                if (date1 == date2) {
                    return 0;
                }

                return date2 - date1 > 0 ? 1 : -1;
            }
        });
        return fileList;
    }

    public static List<QDCloud> sortCloud(List<QDCloud> cloudList) {
        Collections.sort(cloudList, new Comparator<QDCloud>() {
            @Override
            public int compare(QDCloud o1, QDCloud o2) {
                int type1 = o1.getType();
                int type2 = o2.getType();
                return type1 - type2;
            }
        });
        return cloudList;
    };

    public static List<QDCloud> sortCloudByTimeDown(List<QDCloud> cloudList) {
        Collections.sort(cloudList, new Comparator<QDCloud>() {
            @Override
            public int compare(QDCloud o1, QDCloud o2) {
                int type1 = o1.getType();
                int type2 = o2.getType();
                if (type1 != type2) {
                    return type1 - type2;
                } else {
                    if (type1 == QDCloud.TYPE_FILE) {
                        long time1 = o1.getCreateTime();
                        long time2 = o2.getCreateTime();
                        if (time1 == time2) {
                            return 0;
                        }
                        return time2 - time1 > 0 ? 1 : -1;
                    }
                    return 0;
                }
            }
        });
        return cloudList;
    };

    public static List<QDCloud> sortCloudByTimeUp(List<QDCloud> cloudList) {
        Collections.sort(cloudList, new Comparator<QDCloud>() {
            @Override
            public int compare(QDCloud o1, QDCloud o2) {
                int type1 = o1.getType();
                int type2 = o2.getType();
                if (type1 != type2) {
                    return type1 - type2;
                } else {
                    if (type1 == QDCloud.TYPE_FILE) {
                        long time1 = o1.getCreateTime();
                        long time2 = o2.getCreateTime();
                        if (time1 == time2) {
                            return 0;
                        }
                        return time1 - time2 > 0 ? 1 : -1;
                    }
                    return 0;
                }
            }
        });
        return cloudList;
    };

    public static List<QDCloud> sortCloudBySizeDown(List<QDCloud> cloudList) {
        Collections.sort(cloudList, new Comparator<QDCloud>() {
            @Override
            public int compare(QDCloud o1, QDCloud o2) {
                int type1 = o1.getType();
                int type2 = o2.getType();
                if (type1 != type2) {
                    return type1 - type2;
                } else {
                    if (type1 == QDCloud.TYPE_FILE) {
                        long size1 = o1.getSize();
                        long size2 = o2.getSize();
                        if (size1 == size2) {
                            return 0;
                        }
                        return size2 - size1 > 0 ? 1 : -1;
                    }
                    return 0;
                }
            }
        });
        return cloudList;
    };

    public static List<QDCloud> sortCloudBySizeUp(List<QDCloud> cloudList) {
        Collections.sort(cloudList, new Comparator<QDCloud>() {
            @Override
            public int compare(QDCloud o1, QDCloud o2) {
                int type1 = o1.getType();
                int type2 = o2.getType();
                if (type1 != type2) {
                    return type1 - type2;
                } else {
                    if (type1 == QDCloud.TYPE_FILE) {
                        long size1 = o1.getSize();
                        long size2 = o2.getSize();
                        if (size1 == size2) {
                            return 0;
                        }
                        return size1 - size2 > 0 ? 1 : -1;

                    }
                    return 0;
                }
            }
        });
        return cloudList;
    };

    public static List<QDDownloadInfo> sortDownloadInfo(List<QDDownloadInfo> downloadInfoList) {
        Collections.sort(downloadInfoList, new Comparator<QDDownloadInfo>() {
            @Override
            public int compare(QDDownloadInfo o1, QDDownloadInfo o2) {
                int state1 = o1.getState();
                int state2 = o2.getState();
                if (state1 == state2) {
                    return 0;
                }
                if (state1 != QDDownloadInfo.TYPE_COMPLETE && state2 != QDDownloadInfo.TYPE_COMPLETE) {
                    return 0;
                } else {
                    return state1 - state2;
                }
            }
        });
        return downloadInfoList;
    }

    public static List<QDUploadInfo> sortUploadInfo(List<QDUploadInfo> uploadInfoList) {
        Collections.sort(uploadInfoList, new Comparator<QDUploadInfo>() {
            @Override
            public int compare(QDUploadInfo o1, QDUploadInfo o2) {
                int state1 = o1.getState();
                int state2 = o2.getState();
                if (state1 == state2) {
                    return 0;
                }
                if (state1 != QDUploadInfo.TYPE_COMPLETE && state2 != QDUploadInfo.TYPE_COMPLETE) {
                    return 0;
                } else {
                    return state1 - state2;
                }
            }
        });
        return uploadInfoList;
    }

    public static List<QDAccModel> sortAcc(List<QDAccModel> accModelList) {
        Collections.sort(accModelList, new Comparator<QDAccModel>() {
            @Override
            public int compare(QDAccModel o1, QDAccModel o2) {
                int follow1 = o1.getIsFollow();
                int follow2 = o2.getIsFollow();



                return follow2 - follow1;
            }
        });
        return accModelList;
    }

    public static List<QDMeetingModel> sortMeeting(List<QDMeetingModel> modelList) {
        Collections.sort(modelList, new Comparator<QDMeetingModel>() {
            @Override
            public int compare(QDMeetingModel o1, QDMeetingModel o2) {
                long startTime1 = o1.getStartTime();
                long startTime2 = o2.getStartTime();
                return (int) (startTime2 - startTime1);
            }
        });
        return modelList;
    }
}
