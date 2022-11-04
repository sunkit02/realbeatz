package com.realbeatz.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PostUtils {
    public static final int MAX_CONTENT_LENGTH = 280;
    public static final int MAX_SONG_TITLE_LENGTH = 100;
    public static final int MAX_ARTISTS_LENGTH = 100;
    private static final Map<String, Predicate<Object>> postContentChecks = new HashMap<>();

    /**
     * Returns a hashmap containing all the checks needed for
     * validating each field of a post mapped to the name
     * of the field being validated
     * @return hashmap containing all the checks
     */
    public static Map<String, Predicate<Object>> getPostChecks() {
        if (postContentChecks.isEmpty()) {
            initializeChecks();
        }
        return postContentChecks;
    }

    /**
     * Adds the appropriate input validation for each field of a post
     */
    private static void initializeChecks() {

        Predicate<Object> contentCheck = content -> {
            String contentString = (String) content;
            return contentString.length() <= MAX_CONTENT_LENGTH;
        };
        Predicate<Object> songTitleCheck = songTitle -> {
            String songTitleString = (String) songTitle;
            return songTitleString.length() <= MAX_SONG_TITLE_LENGTH;
        };
        Predicate<Object> artistsCheck = artists -> {
            String artistsString = (String) artists;
            return artistsString.length() <= MAX_ARTISTS_LENGTH;
        };

        postContentChecks.put("content", contentCheck);
        postContentChecks.put("songTitle", songTitleCheck);
        postContentChecks.put("artists", artistsCheck);
    }
}
