package com.mishenev;

public class CountBookByNameRequestValidator {

    public void validateRequest(String bookName) {
        if (bookName == null || bookName.isEmpty()) {
            throw new IllegalArgumentException("Count Book By Name \"bookName\" path parameter should not be empty.");
        }
    }
}