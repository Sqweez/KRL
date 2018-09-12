package com.gosproj.gosproject.Structures;

import android.os.Parcel;
import android.os.Parcelable;

public class Scan implements Parcelable
{
    public int id;
    public int idDept;
    public String path;
    public int docType;
    public int rgu_id;

    public Scan(int id, int idDept, String path, int docType, int rgu_id)
    {
        this.id = id;
        this.rgu_id = rgu_id;
        this.idDept = idDept;
        this.path = path;
        this.docType = docType;
    }

    protected Scan(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        docType =  in.readInt();
        path = in.readString();
        rgu_id = in.readInt();
    }

    public static final Creator<Scan> CREATOR = new Creator<Scan>() {
        @Override
        public Scan createFromParcel(Parcel in) {
            return new Scan(in);
        }

        @Override
        public Scan[] newArray(int size) {
            return new Scan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(rgu_id);
        dest.writeInt(idDept);
        dest.writeInt(docType);
        dest.writeString(path);
    }
}
