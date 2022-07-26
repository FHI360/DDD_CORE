package org.fhi360.ddd.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@SQLDelete(sql = "update patient set archived = true, last_modified = current_timestamp where id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "archived = false")
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @NotNull
    private Facility facility;
    @ManyToOne
    @NotNull
    @JsonIgnore
    private CommunityPharmacy communityPharmacy;
    private String hospitalNum;
    private String uniqueId;
    private String surname;
    private String otherNames;
    private String gender;
    private LocalDate dateBirth;
    private String address;
    private String phone;
    private String uuid;
    private LocalDateTime lastModified;
    private Boolean archived = false;
    private LocalDate dateStarted;
    private String lastClinicStage;
    private double lastViralLoad;
    private LocalDate dateLastViralLoad;
    private LocalDate viralLoadDueDate;
    private String viralLoadType;
    private LocalDate dateLastRefill;
    private LocalDate dateNextRefill;
    private LocalDate dateLastClinic;
    private LocalDate dateNextClinic;
    private Long discontinued;
    private LocalDate dateDiscontinued;
    private String reasonDiscontinued;
    @Transient
    private Long pharmacyId;
    public Boolean getArchived() {
        return this.archived;
    }

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
        this.lastModified = LocalDateTime.now();

    }
    @PreUpdate
    public void preUpdate() {
        this.lastModified = LocalDateTime.now();
        this.uuid = UUID.randomUUID().toString();

    }
}
