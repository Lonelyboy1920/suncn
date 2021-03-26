package com.qd.longchat.order;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.qd.longchat.model.QDOrderTypeModel;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/29 上午9:31
 */
public class QDOrderUtil {

    private static QDOrderUtil instance;

    private List<QDOrderTypeModel> orderTypeList;

    public static QDOrderUtil getInstance() {
        if (instance == null) {
            instance = new QDOrderUtil();
        }
        return instance;
    }


    public List<QDOrderTypeModel> getOrderTypeList() {
        return orderTypeList;
    }

    public void setOrderTypeList(List<QDOrderTypeModel> orderTypeList) {
        this.orderTypeList = orderTypeList;
    }

    public void showDialog(Activity activity, int type, List<String> list, QDOrderSelectFragment.OnSelectClickListener listener) {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag("1111");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        QDOrderSelectFragment fragment = QDOrderSelectFragment.newInstance(type, list);
        fragment.setListener(listener);
        fragment.show(activity.getFragmentManager(), "1111");
    }
}
