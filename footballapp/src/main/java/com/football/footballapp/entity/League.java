package com.football.footballapp.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "leagues")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(unique = true)
    private Integer apiId;

    @Column
    private String season;

    @Column
    private String logo;

    @OneToMany(mappedBy = "league")
    @JsonIgnore  // ✅ ADD THIS
    private List<Team> teams;

    @OneToMany(mappedBy = "league")
    @JsonIgnore  // ✅ ADD THIS
    private List<Fixture> fixtures;

}