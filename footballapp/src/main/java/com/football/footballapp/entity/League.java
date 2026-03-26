package com.football.footballapp.entity;
import jakarta.persistence.*;
import lombok.Data;

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

    @Column
    private String season;

    @Column
    private String logo;

}