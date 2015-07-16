package com.wishnuu.photoweaver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.wishnuu.photoweaver.flickr.api.Photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Album implements java.io.Serializable, Parcelable {
    private String id;
    private String title;
    private String description;
    private String coverImageUrl;
    private int likesCount;
    private int commentsCount;
    private String link;
    private ArrayList<Photo> photoList;

    public Album(){

    }

    public Album(String id, String title, String description, String coverImageUrl, int like_count, int comment_count, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.likesCount = like_count;
        this.commentsCount = comment_count;
        this.link = link;
        this.photoList = new ArrayList<Photo>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverImage(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return this.id;
    }

    public String getLink() {
        return this.link;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public int getLikesCount() {
    	return this.likesCount;
    }

    public int getCommentsCount() {
        return this.commentsCount;
    }

    public List<Photo> getPhotoList() {
        return this.photoList;
    }

    public Album(Parcel parcel) {
        this.id = parcel.readString();
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.coverImageUrl = parcel.readString();
        this.likesCount = parcel.readInt();
        this.commentsCount = parcel.readInt();
        this.link = parcel.readString();
        Photo[] photoArray = parcel.createTypedArray(Photo.CREATOR);
        this.photoList = new ArrayList<Photo>();
        this.photoList.addAll(Arrays.asList(photoArray));
    }

    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.description);
        parcel.writeString(this.coverImageUrl);
        parcel.writeInt(this.likesCount);
        parcel.writeInt(this.commentsCount);
        parcel.writeString(this.link);
        Photo[] photoArray = this.photoList.toArray(new Photo[this.photoList.size()]);
        parcel.writeTypedArray(photoArray, parcelableFlags);
    }

    public static Creator<Album> CREATOR = new Creator<Album>() {
        public Album createFromParcel(Parcel parcel) {
            return new Album(parcel);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public int describeContents() {
        return 0;  // TODO: Customise this generated block
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Album: {");
        sb.append("id='").append(this.id).append('\'');
        sb.append(", title='").append(this.title).append('\'');
        sb.append(", description='").append(this.description).append('\'');
        sb.append(", coverImage=").append(this.coverImageUrl);
        sb.append(", photosCount=").append(this.photoList == null ? "null" : this.photoList.size());
        sb.append(", likesCount=").append(this.likesCount);
        sb.append(", commentsCount=").append(this.commentsCount);
        sb.append(", link=").append(this.link);
        sb.append('}');
        return sb.toString();
    }
}
