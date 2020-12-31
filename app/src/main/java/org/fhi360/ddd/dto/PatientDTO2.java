package org.fhi360.ddd.dto;

import lombok.Data;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.dto.PharmacyDto;

import java.time.LocalDate;

@Data
public class PatientDTO2 {
    private Long id;
    private Facility facility;
    private Long facilityId;
    private PharmacyDto communityPharmacy;
    private Long pharmacyId;
    private String hospitalNum;
    private String uniqueId;
    private String surname;
    private String otherNames;
    private String gender;
    private LocalDate dateBirth;
    private String address;
    private String phone;
    private String uuid;
    private Boolean archived = false;
    private String dateStarted;
    private String lastClinicStage;
    private double lastViralLoad;
    private String dateLastViralLoad;
    private String viralLoadDueDate;
    private String viralLoadType;
    private String dateLastRefill;
    private String dateNextRefill;
    private String dateLastClinic;
    private String dateNextClinic;
    private int discontinued;
}