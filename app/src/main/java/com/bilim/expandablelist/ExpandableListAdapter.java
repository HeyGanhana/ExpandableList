package com.bilim.expandablelist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilim.expandablelist.bean.BaseInfo;
import com.bilim.expandablelist.bean.ChildItem;
import com.bilim.expandablelist.bean.GroupItem;
import com.bilim.expandablelist.log.Logger;
import com.bilim.expandablelist.util.MemoryUtil;

import java.util.List;

/**
 * Created by zhangdi on 12/3/18.
 */
public class ExpandableListAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ExpandableListAdapter";

    private final static int GROUP_ITEM = 1;
    private final static int CHILD_ITEM = 2;

    private Context mContext;
    private List<BaseInfo> dataLists;
    private LayoutInflater layoutInflater;
    private OnFileItemListener fileItemListener;

    private ViewName viewName = null;
    private boolean[] groupLoadCompleted;
    private long totalRom = 0l;

    public ExpandableListAdapter(Context context, List<BaseInfo> dataLists,
                                 OnFileItemListener fileItemListener) {
        this.mContext = context;
        this.fileItemListener = fileItemListener;
        this.dataLists = dataLists;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (GROUP_ITEM == viewType) {
            itemView = layoutInflater.inflate(R.layout.deep_group_list_view, parent, false);
            return new GroupViewHolder(itemView);
        } else if (CHILD_ITEM == viewType) {
            itemView = layoutInflater.inflate(R.layout.deep_file_child_item_view, parent, false);
            return new ChildViewHolder(itemView);
        } else
            return null;
    }

    public void onRefresh() {
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (dataLists.get(position) instanceof GroupItem) {
            GroupViewHolder groupHolder = (GroupViewHolder) holder;
            GroupItem groupItem = (GroupItem) dataLists.get(position);
            if (groupItem.isExpand()) {
                groupHolder.arrow.setImageResource(R.drawable.arrow_up);
            } else {
                groupHolder.arrow.setImageResource(R.drawable.arrow_down);
            }

            groupHolder.groupIcon.setImageDrawable(groupItem.getFileGroupIcon());
            groupHolder.groupTitle.setText(groupItem.getFileGroupName());
            groupHolder.groupSummmary.setText(MemoryUtil.getFormatSize(groupItem.getUsedSize()) +
                    "/" + MemoryUtil.getFormatSize(groupItem.getTotalSize()));
            //holder.fileArcDrawable.
            groupHolder.groupCheckBox.setVisibility(View.GONE);
            groupHolder.middleCheck.setVisibility(View.GONE);

            int colorFromType = groupItem.getColorFromGroupType(groupItem.getGroupType());
            setHoriProgressBarColor(groupHolder.horiProgressbar, colorFromType);
            groupHolder.horiProgressbar.setProgress(0);
            if (!groupLoadCompleted[groupItem.getGroupPosition()]) {
                Logger.e("zhangdi88", "group " + groupItem.getGroupPosition() + " is not loaded!");
                groupHolder.progressBar.setVisibility(View.VISIBLE);
            } else {//finished
                Logger.e("zhangdi88", "group " + groupItem.getGroupPosition() + " is load finish!");
                groupHolder.progressBar.setVisibility(View.GONE);
                groupHolder.groupCheckBox.setVisibility(View.VISIBLE);

                if (totalRom == 0l)
                    groupHolder.horiProgressbar.setProgress(0);
                else {
                    Logger.e("zhangdi99", "progress:" + MemoryUtil.getPercentInt(groupItem
                            .getTotalSize(), totalRom));
                    groupHolder.horiProgressbar.setProgress(MemoryUtil.getPercentInt(groupItem
                            .getTotalSize(), totalRom));
                }

                if (ChildItem.SelectStaus.ALL_SELECTED.equals(groupItem.getSelectStaus())) {
                    groupHolder.groupCheckBox.setChecked(true);
                } else if (ChildItem.SelectStaus.NO_ONE_SELECTED.equals(groupItem
                        .getSelectStaus())) {
                    groupHolder.groupCheckBox.setChecked(false);
                } else if (ChildItem.SelectStaus.SOME_SELECTED.equals(groupItem
                        .getSelectStaus())) {
                    //holder.fileCheckBox.setBackground();
                    groupHolder.groupCheckBox.setVisibility(View.GONE);
                    groupHolder.middleCheck.setVisibility(View.VISIBLE);
                }
            }

            groupHolder.itemView.setTag(position);
            groupHolder.groupCheckBox.setTag(position);
            groupHolder.middleCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewName = ViewName.GROUP_CHECKBOX;
                    onViewClick(holder, view, viewName);
                }
            });

            groupHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewName = ViewName.GROUP_ITEM;
                    onViewClick(holder, view, viewName);
                }
            });
            groupHolder.groupCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewName = ViewName.GROUP_CHECKBOX;
                    onViewClick(holder, view, viewName);
                }
            });
        } else if (dataLists.get(position) instanceof ChildItem) {
            ChildViewHolder childHolder = (ChildViewHolder) holder;
            ChildItem childItem = (ChildItem) dataLists.get(position);
            childHolder.itemView.setTag(position);
            childHolder.childCheckbox.setTag(position);
            childHolder.childTitle.setText(childItem.getFileTitle());
            childHolder.childIcon.setImageDrawable(childItem.getFileIcon());
            childHolder.childCheckbox.setChecked(childItem.isChecked());
            childHolder.childSize.setText(MemoryUtil.getFormatSize(childItem.getTotalSize()));

            childHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewName = ViewName.CHILD_ITEM;
                    onViewClick(holder, view, viewName);
                }
            });

            childHolder.childCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewName = ViewName.CHILD_CHECKBOX;
                    onViewClick(holder, view, viewName);
                }
            });
        }
    }

    public void setTotalRomSize(long totalRom) {
        this.totalRom = totalRom;
    }

    private void setHoriProgressBarColor(ProgressBar progressBar, int color) {
        if (progressBar == null) return;

        //Background
        ClipDrawable bgClipDrawable = new ClipDrawable(new ColorDrawable(Color.GRAY), Gravity
                .LEFT, ClipDrawable.HORIZONTAL);
        bgClipDrawable.setLevel(10000);
        //Progress
        ClipDrawable progressClip = new ClipDrawable(new ColorDrawable(color), Gravity
                .LEFT, ClipDrawable.HORIZONTAL);
        //Setup LayerDrawable and assign to progressBar
        Drawable[] progressDrawables = {bgClipDrawable, progressClip/*second*/, progressClip};

        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);

        progressBar.setProgressDrawable(progressLayerDrawable);
    }

    @Override
    public int getItemViewType(int position) {
        if (dataLists.get(position) instanceof GroupItem) {
            return GROUP_ITEM;
        } else if (dataLists.get(position) instanceof ChildItem) {
            return CHILD_ITEM;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    public void setGroupLoadCompletedFlag(boolean[] flag) {
        this.groupLoadCompleted = flag;
    }

    private boolean isAllGroupLoadCompleted() {
        boolean result = groupLoadCompleted[0];
        for (boolean flag : groupLoadCompleted) {
            result &= flag;
        }
        return result;
    }

    public void onViewClick(RecyclerView.ViewHolder holder, View view, ViewName viewName) {
        if (!isAllGroupLoadCompleted()) {
            Logger.e(TAG, "datas have not loaded complete and cancel click this time.");
            return;
        }
        int groupPosition = -1;
        int childPosition = -1;
        int position = holder.getLayoutPosition();//获取当前布局中实际的position
        Logger.e(TAG, "onclick view position:" + position);

        if (dataLists.get(position) instanceof GroupItem) {//判断是否为父
            GroupItem groupItem = (GroupItem) dataLists.get(position);
            GroupViewHolder groupHolder = (GroupViewHolder) holder;
            groupPosition = groupItem.getGroupPosition();
            childPosition = -1;
            if (view instanceof ImageView) {
                ImageView middleCheck = (ImageView) view;
                if (groupItem.getSelectStaus().equals(BaseInfo.SelectStaus.SOME_SELECTED)) {
                    groupItem.setSelectStaus(BaseInfo.SelectStaus.ALL_SELECTED);
                    middleCheck.setVisibility(View.GONE);
                    selectAllChildCheckbox(position, true);
                }
            } else if (view instanceof CheckBox) {//group checkbox clicked
                CheckBox groupCheckbox = (CheckBox) view;
                if (groupItem.getSelectStaus().equals(BaseInfo.SelectStaus.NO_ONE_SELECTED)) {
                    groupItem.setSelectStaus(BaseInfo.SelectStaus.ALL_SELECTED);
                    groupCheckbox.setChecked(true);
                    selectAllChildCheckbox(position, true);
                } else if (groupItem.getSelectStaus().equals(BaseInfo.SelectStaus.ALL_SELECTED)) {
                    groupItem.setSelectStaus(BaseInfo.SelectStaus.NO_ONE_SELECTED);
                    groupCheckbox.setChecked(false);
                    selectAllChildCheckbox(position, false);
                }

            } else if (view instanceof RelativeLayout) {
                if ((position + 1) == dataLists.size()) {//判断是否为最后一个元素
                    if (groupItem.getChildItems().size() > 0) {
                        addAllChild(groupItem.getChildItems(), position + 1);
                        groupItem.setExpand(true);
                    } else {
                        groupItem.setExpand(!groupItem.isExpand());
                    }
                } else {
                    if (dataLists.get(position + 1) instanceof GroupItem) {//如果是父并且为折叠状态，需要添加儿子
                        if (groupItem.getChildItems().size() > 0) {
                            addAllChild(groupItem.getChildItems(), position + 1);
                            groupItem.setExpand(true);
                        } else {
                            groupItem.setExpand(!groupItem.isExpand());
                        }

                    } else if (dataLists.get(position + 1) instanceof ChildItem){//如果是儿子并且为展开状态时，需要删除儿子
                        groupItem.setExpand(false);
                        deleteAllChild(position + 1, groupItem.getChildItems().size());
                    }
                }
                Logger.e(TAG, "position:" + position + ",fileInfoItemsize:" + groupItem
                        .getChildItems().size());
                Logger.e(TAG, "groupItem.isExpand():" + groupItem.isExpand());
                if (groupItem.isExpand()) {
                    groupHolder.arrow.setImageResource(R.drawable.arrow_up);
                } else {
                    groupHolder.arrow.setImageResource(R.drawable.arrow_down);
                }
            }

        } else if (dataLists.get(position) instanceof ChildItem) {
            Logger.e(TAG, "onclick view CHILD_ITEM" + position);
            ChildItem childItem = (ChildItem) dataLists.get(position);
            Logger.e(TAG, "click child item:" + childItem);
            groupPosition = childItem.getGroupPosition();
            childPosition = childItem.getChildPosition();
            if (view instanceof CheckBox) {
                //group checkbox clicked
                CheckBox childCheckbox = (CheckBox) view;
                if (childItem.isChecked()) {
                    childItem.setChecked(false);
                    childCheckbox.setChecked(false);
                } else {
                    childItem.setChecked(true);
                    childCheckbox.setChecked(true);
                }
                notifyDataSetChanged();
            } else if (view instanceof RelativeLayout) {
                Logger.e(TAG, "I am a child Item and do nothing!");

            }
        }
        fileItemListener.onItemClick(view, viewName, groupPosition, childPosition, position);
    }

    /*
     * select all child item or not
     * */
    private void selectAllChildCheckbox(int groupPosition, boolean isCheck) {
        BaseInfo baseInfo = dataLists.get(groupPosition);
        if (baseInfo instanceof GroupItem) {//ensure current item is father
            GroupItem groupItem = (GroupItem) baseInfo;
            for (ChildItem child : groupItem.getChildItems()) {
                child.setChecked(isCheck);
            }
            notifyDataSetChanged();
        }
    }


    public void addAllChild(List<? extends BaseInfo> lists, int position) {
        dataLists.addAll(position, lists);
        notifyItemRangeInserted(position, lists.size());
    }

    public void deleteAllChild(int position, int childCount) {
        for (int i = 0; i < childCount; i++) {
            dataLists.remove(position);
        }
        notifyItemRangeRemoved(position, childCount);
    }


    public enum ViewName {
        GROUP_ITEM,
        GROUP_CHECKBOX,
        CHILD_ITEM,
        CHILD_CHECKBOX
    }

    public interface OnFileItemListener {
        void onItemClick(View view, ViewName viewName, int groupPosition, int childPosition, int
                position);
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        public ImageView arrow;
        public ImageView groupIcon;
        public TextView groupTitle, groupSummmary;
        public CheckBox groupCheckBox;
        public ImageView middleCheck;
        public ProgressBar progressBar, horiProgressbar;

        public GroupViewHolder(View itemView) {
            super(itemView);
            arrow = itemView.findViewById(R.id.iv_arrow);
            groupIcon = itemView.findViewById(R.id.file_list_image);
            groupTitle = itemView.findViewById(R.id.file_list_title);
            groupSummmary = itemView.findViewById(R.id.file_list_summary);
            groupCheckBox = itemView.findViewById(R.id.file_list_check_box);
            middleCheck = itemView.findViewById(R.id.middle_check);
            progressBar = itemView.findViewById(R.id.progressBar);
            horiProgressbar = itemView.findViewById(R.id.hori_progress);
        }

    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        public ImageView childIcon;
        public TextView childTitle, childSize;
        public CheckBox childCheckbox;

        public ChildViewHolder(View itemView) {
            super(itemView);
            childIcon = itemView.findViewById(R.id.file_details_item_image);
            childTitle = itemView.findViewById(R.id.file_detail_item_file_title);
            childSize = itemView.findViewById(R.id.file_detail_item_file_size);
            childCheckbox = itemView.findViewById(R.id.file_detail_item_file_cb);
        }
    }
}