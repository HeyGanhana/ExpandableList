package com.bilim.expandablelist.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangdi on 12/3/18.
 */
public class ChildItem extends BaseInfo implements Parcelable {

    public static final Creator<ChildItem> CREATOR = new Creator<ChildItem>() {
        @Override
        public ChildItem createFromParcel(Parcel in) {
            return new ChildItem(in);
        }

        @Override
        public ChildItem[] newArray(int size) {
            return new ChildItem[size];
        }
    };
    private String fileTitle;
    private Drawable fileIcon;
    private long totalSize = 0l;
    private boolean isChecked = false;
    private int groupPosition;
    private int childPosition;
    private GroupItem groupItem;
    private String filePath;
    private long appDataSize = 0l;
    private long appCodeSize = 0l;
    private long appCacheSize = 0l;


    public ChildItem(GroupItem groupItem) {
        if (groupItem == null) {
            throw new NullPointerException("GroupItem cant be null");
        }
        this.groupItem = groupItem;
        this.groupPosition = groupItem.getGroupPosition();
    }

    protected ChildItem(Parcel in) {
        fileTitle = in.readString();
        totalSize = in.readLong();
        isChecked = in.readByte() != 0;
        groupPosition = in.readInt();
        childPosition = in.readInt();
        groupItem = in.readParcelable(GroupItem.class.getClassLoader());
        filePath = in.readString();
        appDataSize = in.readLong();
        appCodeSize = in.readLong();
        appCacheSize = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileTitle);
        dest.writeLong(totalSize);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(groupPosition);
        dest.writeInt(childPosition);
        dest.writeParcelable(groupItem, flags);
        dest.writeString(filePath);
        dest.writeLong(appDataSize);
        dest.writeLong(appCodeSize);
        dest.writeLong(appCacheSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public GroupItem getGroupItem() {
        return groupItem;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        groupItem.checkSelectStatusFromChildren();
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }


    public Drawable getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(Drawable fileIcon) {
        this.fileIcon = fileIcon;
    }

    public long getAppCodeSize() {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            return this.appCodeSize;
        }
        return 0l;
    }

    public void setAppCodeSize(long appCodeSize) {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            this.appCodeSize = appCodeSize;
        }
    }

    public long getAppDataSize() {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            return this.appDataSize;
        }
        return 0l;
    }

    public void setAppDataSize(long appDataSize) {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            this.appDataSize = appDataSize;
        }
    }

    public long getAppCacheSize() {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            return this.appCacheSize;
        }
        return 0l;
    }

    public void setAppCacheSize(long appCacheSize) {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            this.appCacheSize = appCacheSize;
        }
    }

    public long getTotalSize() {
        if (GroupType.APP_DATA.equals(groupItem.getGroupType())) {
            return (appCodeSize + appDataSize + appCacheSize);
        }
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }


    @Override
    public String toString() {
        return "ChildItem{" +
                "fileTitle='" + fileTitle + '\'' +
                ", fileIcon=" + fileIcon +
                ", totalSize=" + totalSize +
                ", isChecked=" + isChecked +
                ", groupPosition=" + this.groupPosition +
                ", childPosition=" + childPosition +
                ", filePath=" + filePath +
                '}';
    }

}
