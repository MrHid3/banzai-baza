package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
public class Location {

    public Location() {
    }

    public Location(String name, String shortname) {
        this.name = name;
        this.shortname = shortname;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String shortname;

    @Column(nullable = false)
    private Boolean isActive = true;
}
