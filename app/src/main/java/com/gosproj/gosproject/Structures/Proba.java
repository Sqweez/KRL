package com.gosproj.gosproject.Structures;

import android.os.Parcel;
import android.os.Parcelable;

public class Proba implements Parcelable
{
    public int id;
    public int idDept;
    public String name;
    public int count;
    public String size;
    public String place;
    public String provider;
    public String typeWork;

    public Proba(int id, int idDept, String name, int count, String size, String place, String provider, String typeWork)
    {
        this.id = id;
        this.idDept = idDept;
        this.name = name;
        this.count = count;
        this.size = size;
        this.place = place;
        this.provider = provider;
        this.typeWork = typeWork;
    }

    protected Proba(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        name = in.readString();
        count = in.readInt();
        size = in.readString();
        place = in.readString();
        provider = in.readString();
        typeWork = in.readString();
    }

    public static final Creator<Proba> CREATOR = new Creator<Proba>() {
        @Override
        public Proba createFromParcel(Parcel in) {
            return new Proba(in);
        }

        @Override
        public Proba[] newArray(int size) {
            return new Proba[size];
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
        dest.writeInt(count);
        dest.writeString(size);
        dest.writeString(place);
        dest.writeString(provider);
        dest.writeString(typeWork);
    }
}
