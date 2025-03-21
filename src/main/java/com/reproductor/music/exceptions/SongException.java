package com.reproductor.music.exceptions;

import java.io.IOException;

public class SongException {

    public static class SongNotFoundException extends RuntimeException {
        public SongNotFoundException(String message) {
            super(message);
        }
    }

    public static class ArtistNotFoundException extends RuntimeException {
        public ArtistNotFoundException(String message) {
            super(message);
        }
    }

    public static class AlbumNotFoundException extends RuntimeException {
        public AlbumNotFoundException(String message) {
            super(message);
        }
    }

}
