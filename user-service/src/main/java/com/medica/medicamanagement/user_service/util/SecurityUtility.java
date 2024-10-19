package com.medica.medicamanagement.user_service.util;

import org.apache.commons.lang.RandomStringUtils;

public final class SecurityUtility {

    private SecurityUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static String getComplexPassword() {
        return RandomStringUtils.randomAlphanumeric(15);
    }
}
