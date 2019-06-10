package com.bilim.expandablelist.bean;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangdi on 12/4/18.
 */
public class GroupItem extends BaseInfo implements Parcelable {
    public static final Creator<GroupItem> CREATOR = new Creator<GroupItem>() {
        @Override
        public GroupItem createFromParcel(Parcel in) {
            return new GroupItem(in);
        }

        @Override
        public GroupItem[] newArray(int size) {
            return new GroupItem[size];
        }
    };
    private final static int[] COLORS = {
            Color.parseColor("#F8AA07"), Color.parseColor("#D81E06"),
            Color.parseColor("#2D2D2D"), Color.parseColor("#40CF0A"),
            Color.parseColor("#E2D804"), Color.parseColor("#1296DB")};
    private String fileGroupName;
    private Drawable fileGroupIcon;
    private boolean expand = false;
    private long totalSize;
    private long usedSize;
    private SelectStaus selectStaus;
    private int groupPosition = -1;
    private GroupType GroupType;
    private List<ChildItem> mChildItems = new ArrayList<>();


    public GroupItem() {
    }

    protected GroupItem(Parcel in) {
        fileGroupName = in.readString();
        expand = in.readByte() != 0;
        totalSize = in.readLong();
        usedSize = in.readLong();
        groupPosition = in.readInt();
        mChildItems = in.createTypedArrayList(ChildItem.CREATOR);
    }

    public void checkSelectStatusFromChildren() {
        setSelectStaus(getChildrenSelectStatus());
    }

    private SelectStaus getChildrenSelectStatus() {
        if (getChildItems().size() == 0) {
            return SelectStaus.NO_ONE_SELECTED;
        }
        int selectCount = 0;
        int unselectCount = 0;
        long tempUsed = 0;
        for (ChildItem child : getChildItems()) {
            if (child.isChecked()) {
                selectCount++;
                tempUsed += child.getTotalSize();
            } else {
                unselectCount++;
            }
        }
        setUsedSize(tempUsed);
        if (selectCount == getChildItems().size()) {
            return SelectStaus.ALL_SELECTED;
        } else if (unselectCount == getChildItems().size()) {
            return SelectStaus.NO_ONE_SELECTED;
        } else {
            return SelectStaus.SOME_SELECTED;
        }
    }

    public int getColorFromGroupType(GroupType type) {
        if (type == null) return 0;
        return COLORS[GroupType.getValue(type)];
    }

    public GroupType getGroupType() {
        return GroupType;
    }

    public void setGroupType(GroupType GroupType) {
        this.GroupType = GroupType;
    }

    public int getGroupPosition(){
        if(groupPosition == -1)
            throw new IllegalArgumentException("groupPosition not be initlized");
        
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        if(groupPosition < 0){
            throw new IllegalArgumentException("unexpected argument");
        }
        this.groupPosition = groupPosition;
    }

    public String getFileGroupName() {
        return fileGroupName;
    }

    public void setFileGroupName(String fileGroupName) {
        this.fileGroupName = fileGroupName;
    }

    public Drawable getFileGroupIcon() {
        return fileGroupIcon;
    }

    public void setFileGroupIcon(Drawable fileGroupIcon) {
        this.fileGroupIcon = fileGroupIcon;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public long getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(long usedSize) {
        this.usedSize = usedSize;
    }

    public long getTotalSize() {
        long temp = 0;
        for (ChildItem child : getChildItems()) {
            temp += child.getTotalSize();
        }
        return temp;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public SelectStaus getSelectStaus() {
        return selectStaus;
    }

    public void setSelectStaus(SelectStaus selectStaus) {
        this.selectStaus = selectStaus;
    }

    public List<ChildItem> getChildItems() {
        return mChildItems;
    }

    public void setChildItems(List<ChildItem> childItems) {
        this.mChildItems = childItems;
    }


    @Override
    public String toString() {
        return "GroupItem{" +
                "fileGroupName='" + fileGroupName + '\'' +
                ", fileGroupIcon=" + fileGroupIcon +
                ", expand=" + expand +
                ", totalSize=" + totalSize +
                ", usedSize=" + usedSize +
                ", selectStaus=" + selectStaus +
                ", groupPosition=" + this.groupPosition +
                ", GroupType=" + GroupType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileGroupName);
        parcel.writeByte((byte) (expand ? 1 : 0));
        parcel.writeLong(totalSize);
        parcel.writeLong(usedSize);
        parcel.writeInt(groupPosition);
        parcel.writeTypedList(mChildItems);
    }
}