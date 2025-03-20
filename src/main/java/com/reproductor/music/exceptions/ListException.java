package com.reproductor.music.exceptions;

public class ListException {
    public static class ListNotFoundException extends RuntimeException {
        public ListNotFoundException(String message) {
            super(message);
        }
    }
}
