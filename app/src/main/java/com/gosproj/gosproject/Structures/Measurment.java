package com.gosproj.gosproject.Structures;

import android.media.MediaExtractor;
import android.os.Parcel;
import android.os.Parcelable;

public class Measurment implements Parcelable
{
    public int id;
    public int idDept;
    public String name;

    public Measurment (int id, int idDept, String name) {
        this.id = id;
        this.idDept = idDept;
        this.name = name;
    }

    protected Measurment (Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        name = in.readString();
    }

    public static final Creator<Measurment> CREATOR = new Creator<Measurment>() {
        @Override
        public Measurment createFromParcel(Parcel in) {
            return new Measurment(in);
        }

        @Override
        public Measurment[] newArray(int size) {
            return new Measurment[size];
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
