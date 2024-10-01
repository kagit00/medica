package com.medica.util;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class DefaultValuesPopulator {
    private DefaultValuesPopulator() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static String getCurrentTimestamp() {
        return OffsetDateTime.now(ZoneOffset.UTC).toString();
    }

    public static String getUid() {
        return UUID.randomUUID().toString();
    }
}
