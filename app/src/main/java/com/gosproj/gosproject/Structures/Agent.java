package com.gosproj.gosproject.Structures;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Agent implements Parcelable
{
    public int id;
    public int idDept;
    public String nameCompany;
    public String rang;
    public String fio;
    public byte[] blob;
    public boolean isProvider;
    public boolean isCustomer;
    public boolean isEngineeringService;

    public Agent(int id, int idDept, String nameCompany, String rang, String fio, boolean isProvider, boolean isCustomer, boolean isEngineeringSerice, byte[] blob)
    {
        this.id = id;
        this.idDept = idDept;
        this.nameCompany = nameCompany;
        this.rang = rang;
        this.fio = fio;
        this.isProvider = isProvider;
        this.isCustomer = isCustomer;
        this.isEngineeringService = isEngineeringSerice;
        this.blob = blob;
    }

    protected Agent(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        nameCompany = in.readString();
        rang = in.readString();
        fio = in.readString();
        blob = in.createByteArray();
        isProvider = in.readByte() != 0;
        isCustomer = in.readByte() != 0;
        isEngineeringService = in.readByte() != 0;
    }

    public static final Creator<Agent> CREATOR = new Creator<Agent>() {
        @Override
        public Agent createFromParcel(Parcel in) {
            return new Agent(in);
        }

        @Override
        public Agent[] newArray(int size) {
            return new Agent[size];
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
        dest.writeString(nameCompany);
        dest.writeString(rang);
        dest.writeString(fio);
        dest.writeByteArray(blob);
        dest.writeByte((byte) (isProvider ? 1 : 0));
        dest.writeByte((byte) (isCustomer ? 1 : 0));
        dest.writeByte((byte) (isEngineeringService ? 1 : 0));
    }
}
