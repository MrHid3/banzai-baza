package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

public enum PaymentType {
    MONTHLY_FEE,
    STARTING_FEE
}
