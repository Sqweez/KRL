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
    public boolean isPodryadchik;
    public boolean isZakazchik;
    public boolean isEngineeringService;
    public boolean isSubPodryadchik;
    public boolean isAvtNadzor;
    public boolean isUpolnomochOrg;
    public boolean isRgu;
    public Agent(int id,
                 int idDept,
                 String nameCompany,
                 String rang,
                 String fio,
                 boolean isPodryadchik,
                 boolean isSubPodryadchik,
                 boolean isZakazchik,
                 boolean isEngineeringSerice,
                 boolean isAvtNadzor,
                 boolean isUpolnomochOrg,
                 boolean isRgu,
                 byte[] blob)
    {
        this.id = id;
        this.idDept = idDept;
        this.nameCompany = nameCompany;
        this.rang = rang;
        this.fio = fio;
        this.isPodryadchik = isPodryadchik;
        this.isZakazchik = isZakazchik;
        this.isSubPodryadchik = isSubPodryadchik;
        this.isAvtNadzor = isAvtNadzor;
        this.isUpolnomochOrg = isUpolnomochOrg;
        this.isEngineeringService = isEngineeringSerice;
        this.isRgu = isRgu;
        this.blob = blob;
    }

    protected Agent(Parcel in) {
        id = in.readInt();
        idDept = in.readInt();
        nameCompany = in.readString();
        rang = in.readString();
        fio = in.readString();
        blob = in.createByteArray();
        isPodryadchik = in.readByte() != 0;
        isRgu = in.readByte() != 0;
        isSubPodryadchik = in.readByte() != 0;
        isZakazchik = in.readByte() != 0;
        isAvtNadzor = in.readByte() != 0;
        isUpolnomochOrg = in.readByte() != 0;
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
        dest.writeByte((byte) (isPodryadchik ? 1 : 0));
        dest.writeByte((byte) (isRgu ? 1 : 0));
        dest.writeByte((byte) (isSubPodryadchik ? 1 : 0));
        dest.writeByte((byte) (isZakazchik ? 1 : 0));
        dest.writeByte((byte) (isAvtNadzor ? 1 : 0));
        dest.writeByte((byte) (isUpolnomochOrg ? 1 : 0));
        dest.writeByte((byte) (isEngineeringService ? 1 : 0));
    }
}
