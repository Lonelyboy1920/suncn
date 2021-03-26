package com.qd.longchat.order;

import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.order.adapter.QDOrderSelectAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/20 下午5:37
 */
public class QDOrderSelectFragment extends DialogFragment {

    public final static int TYPE_OF_TYPE = 0;
    public final static int TYPE_OF_LEVEL = 1;
    public final static int TYPE_OF_STATUS = 2;

    @BindView(R2.id.lv_item_list)
    ListView listView;

    private int type;
    private List<String> strList;
    private OnSelectClickListener listener;
    private QDOrderSelectAdapter adapter;

    public void setListener(OnSelectClickListener listener) {
        this.listener = listener;
    }

    public interface OnSelectClickListener {
        void onSelectClick(int type, int position);
    }

    public static QDOrderSelectFragment newInstance(int type, List<String> strList) {
        QDOrderSelectFragment fragment = new QDOrderSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("strList", (ArrayList<String>) strList);
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_select, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        this.type = bundle.getInt("type");
        this.strList = bundle.getStringArrayList("strList");
        adapter = new QDOrderSelectAdapter(getActivity(), strList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onSelectClick(type, position);
                }
                dismiss();
            }
        });
    }
}
