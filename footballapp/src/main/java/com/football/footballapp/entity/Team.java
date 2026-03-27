package com.football.footballapp.entity;
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

    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
}