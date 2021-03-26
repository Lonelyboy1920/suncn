package com.qd.longchat.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xxw on 2016/7/20.
 */
public class QDChatFunc {

    private ArrayList<Integer> funcIDs;
    private final Context context;
    private List<Drawable> funcList;
    private List<String> funcInfoList;

    public QDChatFunc(Context context, ArrayList<Integer> ids) {
        this.context = context;
        funcIDs = ids;
        initDrawable();
        initInfos();
    }

    private void initDrawable() {
        funcList = new ArrayList<>(funcIDs.size());
        for (Integer i : funcIDs) {
            Drawable dr = context.getResources().getDrawable(i);
            funcList.add(dr);
        }
    }

    private void initInfos() {
        String[] infos = context.getResources().getStringArray(R.array.im_chat_function_item_info);
        funcInfoList = new ArrayList<>(funcIDs.size());
        for (Integer id : funcIDs) {
            if (id == R.mipmap.im_chat_func_photo) {
                funcInfoList.add(infos[0]);
            } else if (id == R.mipmap.im_chat_func_image) {
                funcInfoList.add(infos[1]);
            } else if (id == R.mipmap.im_chat_func_file) {
                funcInfoList.add(infos[2]);
            }
//            else if (id == R.mipmap.im_chat_func_voice) {
//                funcInfoList.add(infos[5]);
//            } else if (id == R.mipmap.im_chat_func_video) {
//                funcInfoList.add(infos[6]);
//            }
//            else if (id == R.mipmap.im_chat_func_sgin) {
//                funcInfoList.add(infos[0]);
//            }
            else if (id == R.mipmap.im_chat_func_shoot) {
                funcInfoList.add(infos[3]);
            }
//            else if (id == R.mipmap.im_chat_func_video) {
//                funcInfoList.add(infos[4]);
//            }
//            else if (id == R.mipmap.im_chat_func_sign_off) {
//                funcInfoList.add(infos[4]);
//            }
//            else if (id == R.mipmap.im_chat_func_location) {
//                funcInfoList.add(infos[5]);
//            } else
            if (id == R.mipmap.im_chat_func_vote) {
                funcInfoList.add(infos[5]);
            }
//            else if (id == R.mipmap.im_chat_func_card) {
//                funcInfoList.add(infos[10]);
//            } else if (id == R.mipmap.im_chat_func_collect) {
//                funcInfoList.add(infos[11]);
//
//
//            }
        }

    }

    public Integer getFuncID(int index) {
        if (index > funcIDs.size() || index < 0) {
            return null;
        } else {
            return funcIDs.get(index);
        }
    }

    public List<Drawable> getFuncList() {
        return funcList;
    }

    public List<String> getFuncInfoList() {
        return funcInfoList;
    }
}
