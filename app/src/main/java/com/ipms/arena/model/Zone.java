package com.ipms.arena.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by t-ebcruz on 3/14/2016.
 */
public class Zone implements Parcelable
{
    private int zoneId;
    private String zone;
    private int contractors;
    private int organic;
    private boolean contractorsOnLoad = false;

    public Zone(int id, String zone, int count) {
        this.zoneId = id;
        this.zone = zone;
        this.contractors = count;
    }

    public Zone(int id, String zone, int count, int organic)
    {
        this.zoneId = id;
        this.zone = zone;
        this.contractors = count;
        this.organic = organic;
    }

    protected Zone(Parcel in) {
        zoneId = in.readInt();
        zone = in.readString();
        contractors = in.readInt();
        organic = in.readInt();
        contractorsOnLoad = in.readByte() != 0;
    }

    public static final Creator<Zone> CREATOR = new Creator<Zone>() {
        @Override
        public Zone createFromParcel(Parcel in) {
            return new Zone(in);
        }

        @Override
        public Zone[] newArray(int size) {
            return new Zone[size];
        }
    };

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int id) {
        this.zoneId = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getContractors() {
        return contractors;
    }

    public void setContractors(int contractors) {
        this.contractors = contractors;
    }

    public boolean isContractorsOnLoad() {
        return contractorsOnLoad;
    }

    public void setContractorsOnLoad(boolean contractorsOnLoad) {
        this.contractorsOnLoad = contractorsOnLoad;
    }

    public int getOrganic() {
        return organic;
    }

    public void setOrganic(int organic) {
        this.organic = organic;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zone='" + zone + '\'' +
                ", zoneId=" + zoneId +
                ", contractors=" + contractors +
                ", organic=" + organic +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zone zone1 = (Zone) o;

        if (zoneId != zone1.zoneId) return false;
        return !(zone != null ? !zone.equals(zone1.zone) : zone1.zone != null);

    }

    @Override
    public int hashCode() {
        int result = zoneId;
        result = 31 * result + (zone != null ? zone.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(zoneId);
        dest.writeString(zone);
        dest.writeInt(contractors);
        dest.writeInt(organic);
        dest.writeByte((byte) (contractorsOnLoad ? 1 : 0));
    }
}
