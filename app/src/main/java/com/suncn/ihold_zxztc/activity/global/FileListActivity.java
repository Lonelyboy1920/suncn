package com.suncn.ihold_zxztc.activity.global;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gavin.giframe.service.GIDownloadService;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.FileAdapter;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;


public class FileListActivity extends BaseActivity {
    @BindView(id = R.id.listview)
    private ListView listView;
    private FileAdapter fileAdapter;
    private ArrayList<ObjFileBean> objfiles;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_file_list);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("附件");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            objfiles = (ArrayList<ObjFileBean>) bundle.getSerializable("fileList");
            String strTitle = bundle.getString("strTitle");
            if (GIStringUtil.isNotBlank(strTitle)) {
                setHeadTitle(strTitle);
            }
        }
        fileAdapter = new FileAdapter(this, objfiles);
        fileAdapter.setShowImg(true);
        listView.setAdapter(fileAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, GIDownloadService.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", Utils.formatFileUrl(activity, objfiles.get(i).getStrFile_url()));
                bundle.putString("filename", GIStringUtil.nullToEmpty(objfiles.get(i).getStrFile_name()));
                bundle.putInt("smallIcon", R.mipmap.ic_launcher);
                intent.putExtras(bundle);
                activity.startService(intent);
            }
        });
    }
}
