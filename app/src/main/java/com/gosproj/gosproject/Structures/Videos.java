package com.gosproj.gosproject.Structures;

import android.os.Parcel;
import android.os.Parcelable;

public class Videos implements Parcelable
{
    public int id;
    public int idDept;
    public String path;

    public Videos(int id, int idDept, String path)
    {
        this.id = id;
        this.idDept = idDept;
        this.path = path;
    }

    protected Videos(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        path = in.readString();
    }

    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
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
