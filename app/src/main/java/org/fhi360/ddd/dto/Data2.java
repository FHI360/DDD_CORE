package org.fhi360.ddd.dto;

import lombok.Data;
import org.fhi360.ddd.domain.District;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Regimen;
import org.fhi360.ddd.domain.State;

import java.util.List;
@Data
public class Data2 {
    private Facility facility;
    private List<State> state;
    private List<District> district;
    private List<Regimen> regimens;
    private PharmacyDto pharmacy;
    private List<PatientDto> patients;
    private String message;
    private List<PharmacyDto> communityPharmacies;
}
