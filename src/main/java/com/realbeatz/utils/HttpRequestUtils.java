package com.realbeatz.utils;

import javax.servlet.http.HttpServletRequest;

import static com.realbeatz.utils.CustomHeaders.USERNAME;

public class HttpRequestUtils {
    public static String getUsernameFromRequest(HttpServletRequest request) {
        return (String) request.getAttribute(USERNAME);
    }
}
