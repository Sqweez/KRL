package com.gosproj.gosproject.Structures;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable
{
    public int id;
    public int idDept;
    public String path;

    public Photo(int id, int idDept, String path)
    {
        this.id = id;
        this.idDept = idDept;
        this.path = path;
    }

    protected Photo(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        path = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idDept);
        dest.writeString(path);
    }
}
