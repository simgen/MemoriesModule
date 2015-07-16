package com.wishnuu.photoweaver.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo2 implements java.io.Serializable, Parcelable {

    private String id;
    private String title;
    private String description;
    private int largeImage;
    private String mediumImage;
    private String smallImage;
    private String thumbImage;
    private int likesCount;
    private int commentsCount;
    private String link;

    public Photo2(String id, String title, String description, int largeImage, String mediumImage, String smallImage, String thumbImage,
                 int like_count, int comment_count, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.largeImage = largeImage;
        this.mediumImage = mediumImage;
        this.smallImage = smallImage;
        this.thumbImage = thumbImage;
        this.likesCount = like_count;
        this.commentsCount = comment_count;
        this.link = link;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return description;
    }

    public int getLargeImage() {
        return this.largeImage;
    }

    public String getMediumImage() {
        return this.mediumImage;
    }

    public String getSmallImage() {
        return this.smallImage;
    }

    public String getThumbImage() {
        return this.thumbImage;
    }

    public int getLikesCount() {
        return this.likesCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public String getLink() {
        return this.link;
    }

    public Photo2(Parcel parcel) {
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.largeImage = parcel.readInt();
        this.mediumImage = parcel.readString();
        this.smallImage = parcel.readString();
        this.thumbImage = parcel.readString();
        this.likesCount = parcel.readInt();
        this.commentsCount = parcel.readInt();
        this.link = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.description);
        parcel.writeInt(this.largeImage);
        parcel.writeString(this.mediumImage);
        parcel.writeString(this.smallImage);
        parcel.writeString(this.thumbImage);
        parcel.writeInt(this.likesCount);
        parcel.writeInt(this.commentsCount);
        parcel.writeString(this.link);
    }

    public static Creator<Photo2> CREATOR = new Creator<Photo2>() {
        public Photo2 createFromParcel(Parcel parcel) {
            return new Photo2(parcel);
        }

        public Photo2[] newArray(int size) {
            return new Photo2[size];
        }
    };

    public int describeContents() {
        return 0;  // TODO: Customise this generated block
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Photo: {");
        sb.append("id='").append(this.id).append('\'');
        sb.append(", title='").append(this.title).append('\'');
        sb.append(", description='").append(this.description).append('\'');
        sb.append(", largeImage=").append(this.largeImage);
        sb.append(", mediumImage=").append(this.mediumImage);
        sb.append(", smallImage=").append(this.smallImage);
        sb.append(", thumbImage=").append(this.thumbImage);
        sb.append(", likesCount=").append(this.likesCount);
        sb.append(", commentsCount=").append(this.commentsCount);
        sb.append(", link=").append(this.link);
        sb.append('}');
        return sb.toString();
    }
}
