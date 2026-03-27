package com.football.footballapp.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String logo;

    @Column
    private String stadium;

    @Column
    private String manager;

    @Column
    private String country;

    @Column(unique = true)
    private Integer apiId;

    @Column
    private Integer foundedYear;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "league_id", nullable = false)
    @JsonIgnoreProperties({"teams", "fixtures"})
    private League league;

}