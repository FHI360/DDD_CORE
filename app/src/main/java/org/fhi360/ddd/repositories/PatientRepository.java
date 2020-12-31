package org.fhi360.ddd.repositories;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.fhi360.ddd.domain.ARV;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.domain.PreLoadRegimen;

import java.util.List;

@Dao
public interface PatientRepository {

    @Query("SELECT * FROM patient order by id DESC")
    List<Patient> findByAll();

    @Query("SELECT * FROM patient where date_started BETWEEN  :to AND  :from")
    List<Patient> dateRange(String to, String from);

    @Query("SELECT * FROM patient")
    Cursor getAllPatient();


    @Query("SELECT * FROM patient where id = :id")
    Patient findOne(Long id);

    @Query("SELECT count(*) FROM patient where gender =:femaleGender")
    int genderCount(String femaleGender);

    @Query("SELECT count(*) FROM patient where pharmacy_id =:pharmacyId order by id DESC")
    int count(Long pharmacyId);


    @Query("SELECT * FROM patient where hospital_num = :hospitalNum and facility_id =:facilityId")
    boolean findHospitalNum(String hospitalNum, Long facilityId);


    @Query("SELECT * FROM patient where pharmacy_id =:pharmacyId order by id DESC")
    List<Patient> findByPharmacyId(Long pharmacyId);


    @Query("SELECT * FROM patient WHERE  id =:id")
    Patient findByPatient(Long id);


    @Query("SELECT * FROM patient order by id DESC")
    List<Patient> findByAll1();

    @Insert
    void save(Patient patients);

    @Update
    void update(Patient patients);

    @Update
    void updateUsers(Patient... patients);


    @Insert
    void insertAll(Patient... patients);

    @Query("SELECT  *  FROM patient WHERE CURRENT_TIME > date_next_refill ORDER BY surname DESC")
    List<Patient> getDefaulters();

    @Query("SELECT  count(*)  FROM patient WHERE CURRENT_TIME > date_next_refill ORDER BY surname DESC")
    int getDefaultersCount();

    @Query("SELECT  count(*)  FROM patient WHERE facility_id =:facilityId")
    int total(Long facilityId);

    @Delete
    void delete(Patient model);

    @Query("SELECT  *  FROM patient WHERE hospital_num =:hosNum")
    Patient checkIfClientExist(String hosNum);

    @Query("SELECT  *  FROM patient WHERE hospital_num =:hosNum OR unique_id =:uniqueId")
    Patient checkIfClientExistOrUniqueId(String hosNum, String uniqueId);

    @Query("SELECT  *  FROM patient WHERE  unique_id =:uniqueId")
    Patient checkIfPatientExistByUniqueId(String uniqueId);


//
//
//    @Query("Update patient set discontinued =:discontinued, date_discontinue =:dateDiscontinue where id =:id")
//    void updateDiscontinue(int discontinued, String dateDiscontinue, Long id);

    @Query("SELECT  *  FROM patient WHERE CURRENT_TIME - date_next_refill >:period and surname LIKE :name OR other_names LIKE :name ORDER BY surname ASC")
    List<Patient> getDefaulters(String period, String name);

    @Query("UPDATE patient set date_next_refill = :dateNextRefill where id =:id")
    void updateDateNextRefil(String dateNextRefill, Long id);


    @Query("SELECT  *  FROM patient WHERE CURRENT_TIME > date_next_refill ORDER BY id DESC")
    boolean getDefaulter1();

    @Query("SELECT  *  FROM patient WHERE CURRENT_TIME > date_next_refill ORDER BY id DESC")
    List<Patient> getDefaulter();

}
