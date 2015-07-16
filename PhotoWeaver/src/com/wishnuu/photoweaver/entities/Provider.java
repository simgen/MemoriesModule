package com.wishnuu.photoweaver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.wishnuu.photoweaver.providers.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;

public class Provider implements Parcelable {

    private String name;
    private String title;
    private int logoUrl;
    private int logoTextUrl;
    private String image1_url; // TODO this should move to Albums and be read from there
    private String image2_url; // TODO this should move to Albums and be read from there
    private String image3_url; // TODO this should move to Albums and be read from there
    private ArrayList<Album> albumList;
    private DataProvider dataProvider;

    public Provider(DataProvider dataProvider, String name, String title, int logoUrl, int logoTextUrl, String image1_url, String image2_url, String image3_url) {
        this.name = name;
        this.title = title;
        this.logoUrl = logoUrl;
        this.logoTextUrl = logoTextUrl;
        this.image1_url = image1_url;
        this.image2_url = image2_url;
        this.image3_url = image3_url;
        this.albumList = new ArrayList<Album>();
        this.dataProvider = dataProvider;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public int getLogoUrl() {
        return this.logoUrl;
    }

    public int getLogoTextUrl() {
        return this.logoTextUrl;
    }

    public String getImage1_url() {
        return this.image1_url;
    }

    public String getImage2_url() {
        return this.image2_url;
    }

    public String getImage3_url() {
        return this.image3_url;
    }

    public ArrayList<Album> getAlbumList() {
        return this.albumList;
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public Provider(Parcel parcel) {
        this.name = parcel.readString();
        this.title = parcel.readString();
        this.logoUrl = parcel.readInt();
        this.logoTextUrl = parcel.readInt();
        this.image1_url = parcel.readString();
        this.image2_url = parcel.readString();
        this.image3_url = parcel.readString();
        Album[] albumArray = parcel.createTypedArray(Album.CREATOR);
        this.albumList = new ArrayList<Album>();
        this.albumList.addAll(Arrays.asList(albumArray));
    }

    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeString(this.name);
        parcel.writeString(this.title);
        parcel.writeInt(this.logoUrl);
        parcel.writeInt(this.logoTextUrl);
        parcel.writeString(this.image1_url);
        parcel.writeString(this.image2_url);
        parcel.writeString(this.image3_url);
        Album[] albumArray = this.albumList.toArray(new Album[this.albumList.size()]);
        parcel.writeTypedArray(albumArray, parcelableFlags);
    }

    public static Creator<Provider> CREATOR = new Creator<Provider>() {
        public Provider createFromParcel(Parcel parcel) {
            return new Provider(parcel);
        }

        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };

    public int describeContents() {
        return 0;  // TODO: Customise this generated block
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Provider: {");
        sb.append("name='").append(this.name).append('\'');
        sb.append(", title='").append(this.title).append('\'');
        sb.append(", logoUrl='").append(this.logoUrl).append('\'');
        sb.append(", logoTextUrl=").append(this.logoTextUrl);
        sb.append(", image1_url=").append(this.image1_url);
        sb.append(", image2_url=").append(this.image2_url);
        sb.append(", image3_url=").append(this.image3_url);
        sb.append(", albumCount=").append(this.albumList == null ? "null" : this.albumList.size());
        sb.append('}');
        return sb.toString();
    }
}
