package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.utils.GIMyIntent;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_SignUpActivity;
import com.suncn.ihold_zxztc.bean.MeetingViewBean;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import skin.support.content.res.SkinCompatResources;

/**
 * 会议通知详情中次会列表
 * Created by daiyy on 2017/7/14.
 */

public class ChildMeetAttendSituationLVAdapter extends BaseAdapter {
    private ArrayList<MeetingViewBean.ObjChildMtListBean> objChildMtListBeen;
    private Activity context;
    private int intModify;
    private int sign;
    private int intAttend;

    public ChildMeetAttendSituationLVAdapter(Activity context, ArrayList<MeetingViewBean.ObjChildMtListBean> objChildMtListBeen, int sign) {
        this.context = context;
        this.objChildMtListBeen = objChildMtListBeen;
        this.sign = sign;
    }

    public ArrayList<MeetingViewBean.ObjChildMtListBean> getObjChildMtListBeen() {
        return objChildMtListBeen;
    }

    @Override
    public int getCount() {
        return objChildMtListBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return objChildMtListBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
//        if (convertView == null) {
        viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.view_attend_situation, null);
        viewHolder.attendSTTag_TextView = (TextView) convertView.findViewById(R.id.tv_situation_tag);
        viewHolder.attendSTTag_TextView.setTextColor(context.getResources().getColor(R.color.font_title));
        viewHolder.attendST_TextView = (TextView) convertView.findViewById(R.id.tv_situation_value);
        viewHolder.leaveType_TextView = (TextView) convertView.findViewById(R.id.tv_leave_type_value);
        viewHolder.leaveType_LinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_leave_type);
        viewHolder.reason_TextView = (TextView) convertView.findViewById(R.id.et_leave_reason);
        viewHolder.leaveReason_LinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_leave_reason);
        convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
        viewHolder.attendST_TextView.setTag(position);
        viewHolder.leaveType_TextView.setTag(position);

        MeetingViewBean.ObjChildMtListBean objChildMtListBean = objChildMtListBeen.get(position);
        intModify = objChildMtListBean.getIntModify();
        intAttend = objChildMtListBean.getIntAttend();
        if (intModify == 0) {
            viewHolder.attendST_TextView.setCompoundDrawables(null, null, null, null);
            viewHolder.leaveType_TextView.setCompoundDrawables(null, null, null, null);
            // viewHolder.reason_TextView.setEnabled(false);
        } else {
            // viewHolder.attendST_TextView.setId(position);
            viewHolder.attendST_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    MeetingViewBean.ObjChildMtListBean objChildMtListBean = objChildMtListBeen.get(pos);
                    ArrayList<ObjFileBean> objFileBeans = new ArrayList<>();
                    //objFileBeans = objChildMtListBean.getAffix();
                    // ObjFileBean objFileBean = new ObjFileBean();
                    if (objChildMtListBean.getAffix() != null && objChildMtListBean.getAffix().size() > 0) {
                        for (int i = 0; i < objChildMtListBean.getAffix().size(); i++) {
                            ObjFileBean objFileBean = objChildMtListBean.getAffix().get(i);
                            objFileBean.setStrFile_url(Utils.formatFileUrl(context, objChildMtListBean.getAffix().get(i).getStrFile_url()));
                            objFileBean.setStrFile_Type(GIMyIntent.getMIMEType(new File(objChildMtListBean.getAffix().get(i).getStrFile_url())));
                            objFileBeans.add(objFileBean);
                        }
                    }

                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, MeetAct_SignUpActivity.class);
                    bundle.putString("headTitle", "修改参与状态");
                    bundle.putInt("sign", sign);
                    bundle.putSerializable("FileList", objFileBeans);
                    bundle.putBoolean("isFromDetail", true);//区分来自详情还是列表
                    bundle.putString("strName", objChildMtListBean.getStrChildMtName());
                    bundle.putString("strStartDate", objChildMtListBean.getStrChildMtStartDate());
                    bundle.putString("strEndDate", objChildMtListBean.getStrChildMtEndDate());
                    bundle.putString("strPlace", objChildMtListBean.getStrChildMtPlace());
                    bundle.putString("strId", objChildMtListBean.getStrChildMtId());
                    bundle.putString("strType", "");
                    bundle.putString("state", Utils.getAttendST(objChildMtListBean.getIntAttend()));//出席状态
                    bundle.putString("leaveTypeId", objChildMtListBean.getStrAbsentType());//请假类型
                    bundle.putString("strReason", objChildMtListBean.getStrReason());//请假原因

                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, 0);
                    //showMyDialog(0, pos);
                }
            });
        }
        viewHolder.attendSTTag_TextView.setText(objChildMtListBean.getStrChildMtName());

        viewHolder.attendST_TextView.setText(Utils.getAttendST(intAttend));

        String strAbsentType = objChildMtListBean.getStrAbsentType();
        if (GIStringUtil.isNotEmpty(strAbsentType))
            viewHolder.leaveType_TextView.setText(strAbsentType);


        String strReason = objChildMtListBean.getStrReason();
        viewHolder.reason_TextView.setText(strReason);
        // viewHolder.reason_TextView.addTextChangedListener(new EditChangedListener(position));


        if (intAttend == 0) { // 请假
            viewHolder.leaveType_LinearLayout.setVisibility(View.VISIBLE);
            viewHolder.leaveReason_LinearLayout.setVisibility(View.VISIBLE);
            //viewHolder.reason_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.leaveType_LinearLayout.setVisibility(View.GONE);
            //viewHolder.reason_TextView.setVisibility(View.GONE);
            viewHolder.leaveReason_LinearLayout.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView attendSTTag_TextView;
        private TextView attendST_TextView;
        private TextView leaveType_TextView;
        private LinearLayout leaveType_LinearLayout;
        private LinearLayout leaveReason_LinearLayout;
        private TextView reason_TextView;
    }

    private NormalListDialog normalListDialog;
    private String[] stringItems;
    private MeetingViewBean.ObjChildMtListBean objChild;

    /**
     * 参加情况和请假类型的弹框
     */
    private void showMyDialog(final int type, final int pos) {
        objChild = objChildMtListBeen.get(pos);
        String title;
        if (type == 0) { // 参加情况
            title = "参加情况";
            stringItems = context.getResources().getStringArray(R.array.attend_sitution);
        } else { // 请假类型
            title = "请假类型";
            stringItems = context.getResources().getStringArray(R.array.leave_type);
        }

        normalListDialog = new NormalListDialog(context, stringItems);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                String menu = stringItems[position];
                if (type == 0) {
                    if (GIStringUtil.isNotEmpty(menu)) {
                        objChild.setIntAttend(Utils.getAttendSTValue(menu));
                        objChildMtListBeen.set(pos, objChild);
                        notifyDataSetChanged();
                    }
                } else {
                    if (GIStringUtil.isNotEmpty(menu)) {
                        objChild.setStrAbsentType(menu);
                        objChildMtListBeen.set(pos, objChild);
                        notifyDataSetChanged();
                    }
                }
                normalListDialog.dismiss();
            }
        });
        normalListDialog.title(title);
        normalListDialog.titleBgColor(SkinCompatResources.getColor(context, R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }


   /* class EditChangedListener implements TextWatcher {
        private CharSequence temp; //监听的文本
        private int pos;

        public EditChangedListener(int pos) {
            super();
            this.pos = pos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            objChildMtListBeen.get(pos).setStrReason(temp.toString());
        }
    }*/

}
