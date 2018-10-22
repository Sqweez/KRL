package com.gosproj.gosproject.Structures;

import android.os.Parcel;
import android.os.Parcelable;

public class Defects implements Parcelable
{
    public int id;
    public int idDept;
    public String name;

    public Defects (int id, int idDept, String name) {
        this.id = id;
        this.idDept = idDept;
        this.name = name;
    }

    protected Defects(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        name = in.readString();
    }

    public static final Creator<Defects> CREATOR = new Creator<Defects>() {
        @Override
        public Defects createFromParcel(Parcel in) {
            return new Defects(in);
        }

        @Override
        public Defects[] newArray(int size) {
            return new Defects[size];
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
        dest.writeString(name);
    }
}
