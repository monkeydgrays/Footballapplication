package com.football.footballapp.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String nationality;

    @Column
    private Integer age;

    @Column
    private String position;

    @Column
    private Integer jerseyNumber;

    @Column
    private String photo;

    // Stats
    @Column
    private Integer goals;

    @Column
    private Integer assists;

    @Column
    private Integer yellowCards;

    @Column
    private Integer redCards;

    @Column
    private Integer appearances;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
