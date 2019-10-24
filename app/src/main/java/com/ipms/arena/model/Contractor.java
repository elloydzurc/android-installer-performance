package com.ipms.arena.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by t-ebcruz on 3/14/2016.
 */
public class Contractor implements Parcelable
{
    private int zoneId;
    private int contractorId;
    private String zone;
    private String contractor;
    private int open;
    private int closed;
    private int rso;
    private int acknowledge;
    private int dispatched;
    private int installer;
    private int flag;
    private int position;
    private float rating;
    private boolean showMapChart = false;

    public Contractor(int id, String zone, int contractorId, String contractor, int open, int completed, int rso, int acknowledge, int dispatched, int installer, int flag)
    {
        this.zoneId = id;
        this.zone = zone;
        this.contractorId = contractorId;
        this.contractor = contractor;
        this.open = open;
        this.closed = completed;
        this.rso = rso;
        this.acknowledge = acknowledge;
        this.dispatched = dispatched;
        this.installer = installer;
        this.flag = flag;
    }

    protected Contractor(Parcel in) {
        zoneId = in.readInt();
        contractorId = in.readInt();
        zone = in.readString();
        contractor = in.readString();
        open = in.readInt();
        closed = in.readInt();
        rso = in.readInt();
        acknowledge = in.readInt();
        dispatched = in.readInt();
        position = in.readInt();
        showMapChart = in.readByte() != 0;
        rating = in.readFloat();
        installer = in.readInt();
        flag = in.readInt();
    }

    public static final Creator<Contractor> CREATOR = new Creator<Contractor>() {
        @Override
        public Contractor createFromParcel(Parcel in) {
            return new Contractor(in);
        }

        @Override
        public Contractor[] newArray(int size) {
            return new Contractor[size];
        }
    };

    public int getZoneId() {
        return zoneId;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
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

    public int getContractorId() {
        return contractorId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public boolean isShowMapChart() {
        return showMapChart;
    }

    public void setShowMapChart(boolean showMapChart) {
        this.showMapChart = showMapChart;
    }

    public int getInstaller() {
        return installer;
    }

    public void setInstaller(int installer) {
        this.installer = installer;
    }

    public int getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return "Contractor{" +
                "acknowledge=" + acknowledge +
                ", zoneId=" + zoneId +
                ", contractorId=" + contractorId +
                ", zone='" + zone + '\'' +
                ", contractor='" + contractor + '\'' +
                ", open=" + open +
                ", closed=" + closed +
                ", rso=" + rso +
                ", dispatched=" + dispatched +
                ", installer=" + installer +
                ", position=" + position +
                ", rating=" + rating +
                ", showMapChart=" + showMapChart +
                ", flag=" + flag +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contractor that = (Contractor) o;

        if (zoneId != that.zoneId) return false;
        return contractorId == that.contractorId;

    }

    @Override
    public int hashCode() {
        int result = zoneId;
        result = 31 * result + contractorId;
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
        dest.writeInt(contractorId);
        dest.writeString(zone);
        dest.writeString(contractor);
        dest.writeInt(open);
        dest.writeInt(closed);
        dest.writeInt(rso);
        dest.writeInt(acknowledge);
        dest.writeInt(dispatched);
        dest.writeInt(position);
        dest.writeFloat(rating);
        dest.writeByte((byte) (showMapChart ? 1 : 0));
        dest.writeInt(installer);
        dest.writeInt(flag);
    }
}
