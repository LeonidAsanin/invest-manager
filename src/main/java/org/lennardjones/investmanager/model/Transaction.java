package org.lennardjones.investmanager.model;

import java.time.LocalDate;

public record Transaction(int amount, LocalDate date) {
}
