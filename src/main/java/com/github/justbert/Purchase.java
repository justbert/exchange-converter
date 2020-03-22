package com.github.justbert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Currency;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {
    private LocalDate date;
    private Float cost;
    private Currency currency;
}
