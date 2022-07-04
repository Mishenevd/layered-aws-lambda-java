package com.mishenev;

import com.amazonaws.util.StringUtils;

/**
 * CreateBookRequestValidator.
 * Validates the {@link CreateBookRequest}.
 *
 * @author Dmitrii_Mishenev
 */
public class CreateBookRequestValidator {

    public void validateRequest(CreateBookRequest request) {
        if (request == null
                || StringUtils.isNullOrEmpty(request.getDescription())
                || StringUtils.isNullOrEmpty(request.getName())
                || StringUtils.isNullOrEmpty(request.getTittle())) {
            throw new IllegalArgumentException("Create book request body is invalid. Request: " + request);
        }
    }
}