package com.example.introduction2.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "careers")
public class Career implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fromDate;
    private LocalDate toDate;
    private String businessOutline;
    private String businessOutlineDescription;
    private String role;
    private String roleDescription;
    private String useLanguage;
    private String useDatabase;
    private String useServer;
    private String other;
    private String responsibleProcess;

    @OneToOne
    @JoinColumn(name = "job_career_id", referencedColumnName = "id")
    @JsonBackReference
    private JobCareer jobCareer;
}
