package org.fhi360.ddd.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Patient;

import java.util.List;

@Dao
public interface FacilityRepository {

    @Insert
    Long save(Facility facility);

    @Update
    void update(Facility facility);


    @Query("SELECT * FROM facility where id = :id")
    Facility findOne(Long id);

    @Query("SELECT  * FROM facility c1\n" +
            " WHERE id = (SELECT MIN(id) FROM facility c2\n" +
            "    WHERE c1.name = c2.name)")
    List<Facility> findAll();


    @Query("SELECT * FROM facility LIMIT 1")
    Facility getFacility();

    @Insert
    void insertAll(Facility... facilities);
//DELETE FROM `tablename` WHERE `id` >= 3 AND `id` <= 10;

    @Query("delete from facility")
    void delete();


    @Query("DELETE FROM `facility` WHERE `id` = 3")
    void delete3();

    @Query("DELETE FROM `facility` WHERE `id` = 4")
    void delete4();

    @Query("DELETE FROM `facility` WHERE `id` = 1")
    void delete5();

    @Query("DELETE FROM `facility` WHERE `id` = 6")
    void delete6();


}
