package com.medica.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.exception.InternalServerErrorException;
import com.medica.model.Error;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.PrintWriter;

public final class ErrorUtility {
    private static final Logger logger = LoggerFactory.getLogger(ErrorUtility.class);
    private static final ObjectMapper om = new ObjectMapper();

    private ErrorUtility() {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * Gets error.
     *
     * @param errorMsg the error msg
     * @param status   the status
     * @return the error
     */
    public static Error getError(String errorMsg, HttpStatus status) {
        Error error = new Error();
        error.setErrorMsg(errorMsg);
        error.setStatus(status);
        error.setTimestamp(DefaultValuesPopulator.getCurrentTimestamp());
        error.setUid(DefaultValuesPopulator.getUid());
        return error;
    }

    /**
     * Print error.
     *
     * @param ex       the ex
     * @param response the response
     */
    public static void printError(String ex, HttpServletResponse response) {
        try {
            Error error = getError(ex, HttpStatus.valueOf(response.getStatus()));
            String str = om.writeValueAsString(error);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(str);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
