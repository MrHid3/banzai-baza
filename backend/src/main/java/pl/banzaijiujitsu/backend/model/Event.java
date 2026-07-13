package pl.banzaijiujitsu.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

//@Entity
//@Getter
//@Setter
//public class Event {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Setter(AccessLevel.NONE)
//    private Long id;
//
//    @Column(nullable = false)
//    private LocalTime startTime;
//
//    @Column(nullable = false)
//    private LocalTime endTime;
//
//    @ManyToOne(optional = false)
//    private Location location;
//
//    private boolean recurring;
//}
