package com.app.tourguide.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class PackageSpots implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "package_id")
    private String packageId;

    //@TypeConverters(DataConverter.class)
    @ColumnInfo(name = "package_spots")
    private String packageSpots;

    public PackageSpots(String packageId, String packageSpots) {
        this.packageId = packageId;
        this.packageSpots = packageSpots;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageSpots() {
        return packageSpots;
    }

    public void setPackageSpots(String packageSpots) {
        this.packageSpots = packageSpots;
    }

}
