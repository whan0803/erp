package org.example.document.record;

import java.time.LocalDate;

public record CancellationRequest(
        int originalNumber, LocalDate cancelDate
) {
}
