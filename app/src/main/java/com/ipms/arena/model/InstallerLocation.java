package com.ipms.arena.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by t-ebcruz on 5/30/2016.
 */
public class InstallerLocation implements Parcelable{

    private Installer installer;
    private String serviceOrder;
    private String projectId;
    private String status;
    private String serviceType;
    private String orderType;
    private String neType;
    private String address;
    private String address2;
    private double lat;
    private double lon;

    public InstallerLocation(Installer installer, String serviceOrder, String projectID, String status, String serviceType, String orderType, String neType, String address, String address2)
    {
        this.installer = installer;
        this.serviceOrder = serviceOrder;
        this.projectId = projectID;
        this.status = status;
        this.serviceType = serviceType;
        this.orderType = orderType;
        this.neType = neType;
        this.address = address;
        this.address2 = address2;
    }

    protected InstallerLocation(Parcel in)
    {
        installer =  in.readParcelable(Installer.class.getClassLoader());
        serviceOrder = in.readString();
        projectId = in.readString();
        status = in.readString();
        serviceType = in.readString();
        orderType = in.readString();
        neType = in.readString();
        address = in.readString();
        address2 = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    public static final Creator<InstallerLocation> CREATOR = new Creator<InstallerLocation>() {
        @Override
        public InstallerLocation createFromParcel(Parcel in) {
            return new InstallerLocation(in);
        }

        @Override
        public InstallerLocation[] newArray(int size) {
            return new InstallerLocation[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public Installer getInstaller() {
        return installer;
    }

    public void setInstaller(Installer installer) {
        this.installer = installer;
    }

    public String getNeType() {
        return neType;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getServiceOrder() {
        return serviceOrder;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getStatus() {
        return status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress2() {
        return address2;
    }

    @Override
    public String toString() {
        return "InstallerLocation{" +
                "address2='" + address2 + '\'' +
                ", installer=" + installer +
                ", serviceOrder='" + serviceOrder + '\'' +
                ", projectId='" + projectId + '\'' +
                ", status='" + status + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", neType='" + neType + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(installer, flags);
        dest.writeString(serviceOrder);
        dest.writeString(projectId);
        dest.writeString(status);
        dest.writeString(serviceType);
        dest.writeString(orderType);
        dest.writeString(neType);
        dest.writeString(address);
        dest.writeString(address2);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }
}
