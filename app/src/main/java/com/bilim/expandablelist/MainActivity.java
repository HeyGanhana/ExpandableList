package com.bilim.expandablelist;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bilim.expandablelist.bean.BaseInfo;
import com.bilim.expandablelist.bean.ChildItem;
import com.bilim.expandablelist.bean.GroupItem;
import com.bilim.expandablelist.log.Logger;
import com.bilim.expandablelist.view.ArcDrawable;

import java.util.ArrayList;

public class MainActivity extends Activity implements ExpandableListAdapter
        .OnFileItemListener {

    public static final String TAG = "DeepCleanActivity_tag";
    private String[] GroupTypes = {"Images", "Musics", "Videos", "App Data", "Big Files", "others"};
    private int[] fileIcons = {R.drawable.ic_image, R.drawable.ic_music, R.drawable.ic_video,
            R.drawable.ic_app, R.drawable.ic_files, R.drawable.ic_other};
    private Resources resources;
    private AppBarLayout appBarLayout;
    private RecyclerView fileRecyclerView;
    private TextView showRamText, showStorageText, showPathText, divider;
    private ArcDrawable ramArcDrawable, storageArcDrawable;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ChildItem> childrenList;
    private ArrayList<BaseInfo> parentsList = new ArrayList<>();
    private ExpandableListAdapter ExpandableListAdapter;
    private ChildItem.GroupType[] GroupTypeValues = ChildItem.GroupType.values();
    private boolean[] loadComplete = {true, true, true, true, true, true};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_clean_main_view);

        resources = getResources();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        fileRecyclerView = findViewById(R.id.file_type_list);
        appBarLayout = findViewById(R.id.deep_clean_appbar);
        showRamText = findViewById(R.id.show_ram_text);
        showStorageText = findViewById(R.id.show_storage_text);
        divider = findViewById(R.id.deep_divider);
        ramArcDrawable = findViewById(R.id.ram_arc);
        storageArcDrawable = findViewById(R.id.storage_arc);

    }


    private void initData() {

        parentsList.clear();
        for (int i = 0; i < GroupTypes.length; i++) {

            GroupItem parent = new GroupItem();
            parent.setSelectStaus(ChildItem.SelectStaus.NO_ONE_SELECTED);
            parent.setGroupType(GroupTypeValues[i]);
            parent.setFileGroupName(GroupTypes[i]);
            parent.setFileGroupIcon(resources.getDrawable(fileIcons[i]));
            parent.setGroupPosition(i);
            ArrayList<ChildItem> childList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                ChildItem child = new ChildItem(parent);
                child.setChildPosition(j);
                child.setFileTitle(parent.getGroupPosition() + 1 + "group 第" + j + "个 child");
                childList.add(child);
            }
            parent.setChildItems(childList);
            parentsList.add(parent.getGroupPosition(), parent);
        }
        if (ExpandableListAdapter == null) {
            ExpandableListAdapter = new ExpandableListAdapter(this, parentsList, this);
            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            fileRecyclerView.setLayoutManager(linearLayoutManager);
            //fileRecyclerView.setItemAnimator(new FadeInDownAnimator());
            ExpandableListAdapter.setGroupLoadCompletedFlag(loadComplete);
            fileRecyclerView.setAdapter(ExpandableListAdapter);
        }
    }


    @Override
    public void onItemClick(View view, ExpandableListAdapter.ViewName viewName, int groupPosition, int
            childPosition, final int position) {
        Logger.e(TAG, "view:" + view + ",viewName:" + viewName + ",groupPosition:" + groupPosition +
                ",childPosition:" + childPosition + ",position in list:" + position);

        switch (viewName) {
            case GROUP_ITEM:
                Logger.e(TAG, "click on GROUP_ITEM ");
                break;
            case GROUP_CHECKBOX:
                Logger.e(TAG, "click on GROUP_CHECKBOX");
                break;
            case CHILD_ITEM:
                Logger.e(TAG, "click on CHILD_ITEM:" + parentsList.get(position));
                break;
            case CHILD_CHECKBOX:
                Logger.e(TAG, "click on CHILD_CHECKBOX");
                break;
            default:
                Logger.e(TAG, "unknow view name");
                break;
        }
    }

}