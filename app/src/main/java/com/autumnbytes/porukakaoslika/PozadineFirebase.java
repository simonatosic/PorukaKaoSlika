package com.autumnbytes.porukakaoslika;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Simona Tošić on 23-Mar-17.
 */

public class PozadineFirebase implements Parcelable {

    private String url;

    public PozadineFirebase() {
    }

    protected PozadineFirebase (Parcel parcel) {
        url = parcel.readString();
    }

    public static final Creator<PozadineFirebase> CREATOR = new Creator<PozadineFirebase>() {

        @Override
        public PozadineFirebase createFromParcel(Parcel parcel) {
            return new PozadineFirebase (parcel);
        }

        @Override
        public PozadineFirebase [] newArray(int size) {
            return new PozadineFirebase[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}