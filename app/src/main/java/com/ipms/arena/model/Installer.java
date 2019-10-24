package com.ipms.arena.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by t-ebcruz on 4/6/2016.
 */
public class Installer implements Parcelable
{
    private int zoneId;
    private int contractorId;
    private int key;
    private String id;
    private String name;
    private int open;
    private int closed;
    private int rso;
    private int acknowledge;
    private int dispatched;
    private float rating;
    private boolean showMap = false;
    private boolean showChart = false;

    public Installer(int zone, int contractor, int key, String id, String name, int open, int closed, int rso, int acknowledge, int dis) {
        this.zoneId = zone;
        this.contractorId = contractor;
        this.key = key;
        this.id = id;
        this.name = name;
        this.open = open;
        this.closed = closed;
        this.rso = rso;
        this.acknowledge = acknowledge;
        this.dispatched = dis;
    }

    protected Installer(Parcel in) {
        zoneId = in.readInt();
        contractorId = in.readInt();
        key = in.readInt();
        id = in.readString();
        name = in.readString();
        open = in.readInt();
        closed = in.readInt();
        rso = in.readInt();
        acknowledge = in.readInt();
        dispatched = in.readInt();
        rating = in.readFloat();
        showMap = in.readByte() != 0;
        showChart = in.readByte() != 0;
    }

    public static final Creator<Installer> CREATOR = new Creator<Installer>() {
        @Override
        public Installer createFromParcel(Parcel in) {
            return new Installer(in);
        }

        @Override
        public Installer[] newArray(int size) {
            return new Installer[size];
        }
    };

    public int getContractorId() {
        return contractorId;
    }

    public int getZoneId() {
        return zoneId;
    }

    public int getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public int getRso() {
        return rso;
    }

    public void setRso(int rso) {
        this.rso = rso;
    }

    public int getAcknowledge() {
        return acknowledge;
    }

    public int getDispatched() {
        return dispatched;
    }

    public float getRating()
    {
        float average = 0;

        double upper = closed + rso;
        double lower = closed + rso + open + acknowledge + dispatched;

        if(upper > 0) {
            average = (float) ((upper / lower) * 100 / 20);
        }

        return average;
    }

    public boolean isShowMap() {
        return showMap;
    }

    public void setShowMap(boolean showMap) {
        this.showMap = showMap;
    }

    public boolean isShowChart() {
        return showChart;
    }

    public void setShowChart(boolean showChart) {
        this.showChart = showChart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Installer installer = (Installer) o;

        return id.equals(installer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Installer{" +
                "acknowledge=" + acknowledge +
                ", zoneId=" + zoneId +
                ", contractorId=" + contractorId +
                ", key=" + key +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", open=" + open +
                ", closed=" + closed +
                ", rso=" + rso +
                ", dispatched=" + dispatched +
                ", rating=" + rating +
                ", showMap=" + showMap +
                ", showChart=" + showChart +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(zoneId);
        dest.writeInt(contractorId);
        dest.writeInt(key);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(open);
        dest.writeInt(closed);
        dest.writeInt(rso);
        dest.writeInt(acknowledge);
        dest.writeInt(dispatched);
        dest.writeByte((byte) (showMap ? 1 : 0));
        dest.writeByte((byte) (showChart ? 1 : 0));
    }
}
