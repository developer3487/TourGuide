package com.app.tourguide.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.app.tourguide.database.entity.PackageSpots;
import com.app.tourguide.database.entity.TourPackage;

import java.util.List;


@Dao
public interface TourPackageDao {


    @Query("SELECT * FROM TourPackage")
    List<TourPackage> getAll();

    @Insert
    void insert(TourPackage task);


    @Delete
    void delete(TourPackage task);

    @Query("UPDATE TourPackage SET spots=:spots WHERE package_id=:packageId")
    void update(String packageId, String spots);


    //PACKAGE SPOTS
    @Query("SELECT * FROM PackageSpots WHERE package_id=:packageId limit 1")
    List<PackageSpots> getPackageSpots(String packageId);

    @Insert
    void insertPackageSpots(PackageSpots spots);


    @Query("UPDATE PackageSpots SET package_spots=:packageSpots WHERE package_id=:packageId")
    void updatePackageSpots(String packageId, String packageSpots);


    /*@Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user where first_name LIKE  :firstName AND last_name LIKE :lastName")
    User findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) from user")
    int countUsers();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);*/

}
