package com.ipms.arena.model;

import java.io.Serializable;

/**
 * Created by t-ebcruz on 3/3/2016.
 */
public class ServiceLocation implements Serializable
{
    private int id;
    private int tech;
    private String serviceNumber;
    private String pid;
    private String status;
    private String serviceType;
    private String orderType;
    private String ngeType;
    private String serviceAddress;

    private double latitude;
    private double longtitude;

    public ServiceLocation() {
    }

    public ServiceLocation(int tech, String serviceNumber, String pid, String status,  String serviceType, String orderType, String ngeType,  String serviceAddress) {
        this.ngeType = ngeType;
        this.orderType = orderType;
        this.pid = pid;
        this.serviceAddress = serviceAddress;
        this.serviceNumber = serviceNumber;
        this.serviceType = serviceType;
        this.status = status;
        this.tech = tech;
    }

    public String getNgeType() {
        return ngeType;
    }

    public void setNgeType(String ngeType) {
        this.ngeType = ngeType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTech() {
        return tech;
    }

    public void setTech(int tech) {
        this.tech = tech;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceLocation that = (ServiceLocation) o;

        if (tech != that.tech) return false;
        if (serviceNumber != null ? !serviceNumber.equals(that.serviceNumber) : that.serviceNumber != null)
            return false;
        return !(pid != null ? !pid.equals(that.pid) : that.pid != null);

    }

    @Override
    public int hashCode() {
        int result = tech;
        result = 31 * result + (serviceNumber != null ? serviceNumber.hashCode() : 0);
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServiceLocation{" +
                "id=" + id +
                ", tech=" + tech +
                ", serviceNumber='" + serviceNumber + '\'' +
                ", pid='" + pid + '\'' +
                ", status='" + status + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", ngeType='" + ngeType + '\'' +
                ", serviceAddress='" + serviceAddress + '\'' +
                ", latitude=" + latitude +
                ", longtitude=" + longtitude +
                '}';
    }
}
