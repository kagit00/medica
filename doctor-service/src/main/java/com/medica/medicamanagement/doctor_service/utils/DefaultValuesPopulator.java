package com.medica.medicamanagement.doctor_service.utils;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

public final class DefaultValuesPopulator {
    private DefaultValuesPopulator() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static String getCurrentTimestamp() {
        return OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).toString();
    }
}
