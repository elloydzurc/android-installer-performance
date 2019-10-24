package com.ipms.arena.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by t-ebcruz on 5/26/2016.
 */
public class ContractorPerformance implements Parcelable {

    private int user;
    private int zoneId;
    private int contractorCode;
    private String serviceOrder;
    private String statusDate;
    private String statusTime;
    private int type;

    public ContractorPerformance(int user, int zoneId, int contractorCode, String serviceOrder, String statusDate, String statusTime, int type)
    {
        this.contractorCode = contractorCode;
        this.serviceOrder = serviceOrder;
        this.statusDate = statusDate;
        this.statusTime = statusTime;
        this.user = user;
        this.zoneId = zoneId;
        this.type = type;
    }

    protected ContractorPerformance(Parcel in) {
        user = in.readInt();
        zoneId = in.readInt();
        contractorCode = in.readInt();
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

    public int getContractorCode() {
        return contractorCode;
    }

    public void setContractorCode(int contractorCode) {
        this.contractorCode = contractorCode;
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

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContractorPerformance that = (ContractorPerformance) o;

        if (user != that.user) return false;
        if (zoneId != that.zoneId) return false;
        if (contractorCode != that.contractorCode) return false;
        if (type != that.type) return false;
        return !(serviceOrder != null ? !serviceOrder.equals(that.serviceOrder) : that.serviceOrder != null);

    }

    @Override
    public int hashCode() {
        int result = user;
        result = 31 * result + zoneId;
        result = 31 * result + contractorCode;
        result = 31 * result + (serviceOrder != null ? serviceOrder.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    @Override
    public String toString() {
        return "ContractorPerformance{" +
                "contractorCode=" + contractorCode +
                ", user=" + user +
                ", zoneId=" + zoneId +
                ", serviceOrder='" + serviceOrder + '\'' +
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
        dest.writeInt(contractorCode);
        dest.writeString(serviceOrder);
        dest.writeString(statusDate);
        dest.writeString(statusTime);
        dest.writeInt(type);
    }
}
