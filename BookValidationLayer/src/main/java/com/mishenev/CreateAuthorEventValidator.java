package com.mishenev;

import com.mishenev.create_author.CreateAuthorEvent;

import com.amazonaws.util.StringUtils;

/**
 * CreateAuthorEventValidator.
 *
 * @author Dmitrii_Mishenev
 */
public class CreateAuthorEventValidator {

    public static void validate(CreateAuthorEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("Create book event is invalid. Request: " + null);
        }

        if (StringUtils.isNullOrEmpty(event.getName()) || StringUtils.isNullOrEmpty(event.getSurname())) {
            throw new IllegalArgumentException("Create book event is invalid. Request: " + event);
        }
    }
}