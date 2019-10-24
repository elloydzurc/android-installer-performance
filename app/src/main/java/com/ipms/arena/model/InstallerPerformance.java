package com.ipms.arena.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by t-ebcruz on 5/26/2016.
 */
public class InstallerPerformance implements Parcelable {

    private int user;
    private int zoneId;
    private int techKey;
    private int contractorId;
    private String serviceOrder;
    private String statusDate;
    private String statusTime;
    private int type;

    public InstallerPerformance(int user, int zoneId, int contractorId, int techKey, String serviceOrder, String statusDate, String statusTime, int type)
    {
        this.user = user;
        this.zoneId = zoneId;
        this.contractorId = contractorId;
        this.techKey = techKey;
        this.serviceOrder = serviceOrder;
        this.statusDate = statusDate;
        this.statusTime = statusTime;
        this.type = type;
    }

    protected InstallerPerformance(Parcel in) {
        user = in.readInt();
        zoneId = in.readInt();
        contractorId = in.readInt();
        techKey = in.readInt();
        serviceOrder = in.readString();
        statusDate = in.readString();
        statusTime = in.readString();
        type = in.readInt();
    }

    public static final Creator<ContractorPerformance> CREATOR = new Creator<ContractorPerformance>() {
        @Override
        public ContractorPerformance createFromParcel(Parcel in) {
            return new ContractorPerformance(in);
        }

        @Override
        public ContractorPerformance[] newArray(int size) {
            return new ContractorPerformance[size];
        }
    };

    public int getContractorId() {
        return contractorId;
    }

    public void setContractorId(int contractorId) {
        this.contractorId = contractorId;
    }

    public String getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(String serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getTechKey() {
        return techKey;
    }

    public void setTechKey(int techKey) {
        this.techKey = techKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstallerPerformance that = (InstallerPerformance) o;

        if (zoneId != that.zoneId) return false;
        if (techKey != that.techKey) return false;
        if (contractorId != that.contractorId) return false;
        if (type != that.type) return false;
        return !(serviceOrder != null ? !serviceOrder.equals(that.serviceOrder) : that.serviceOrder != null);

    }

    @Override
    public int hashCode() {
        int result = zoneId;
        result = 31 * result + techKey;
        result = 31 * result + contractorId;
        result = 31 * result + (serviceOrder != null ? serviceOrder.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    @Override
    public String toString() {
        return "InstallerPerformance{" +
                "serviceOrder='" + serviceOrder + '\'' +
                ", user=" + user +
                ", zoneId=" + zoneId +
                ", techKey=" + techKey +
                ", contractorId='" + contractorId + '\'' +
                ", statusDate='" + statusDate + '\'' +
                ", statusTime='" + statusTime + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(user);
        dest.writeInt(zoneId);
        dest.writeInt(techKey);
        dest.writeInt(contractorId);
        dest.writeString(serviceOrder);
        dest.writeString(statusDate);
        dest.writeString(statusTime);
        dest.writeInt(type);
    }
}
