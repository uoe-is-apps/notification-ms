/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ed.notify.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class CorsMatcher {

    public static void setAccessControlAllowOrigin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String allowedPattern)
    {

        // origin
        String origin = httpServletRequest.getHeader("Origin");

        if (origin==null)
        {
            return;
        }

        URL originUrl = null;
        try {
            originUrl = new URL(origin);
        } catch (MalformedURLException ex) {
        }

        // hostAllowedPattern
        Pattern hostAllowedPattern = Pattern.compile(allowedPattern, Pattern.CASE_INSENSITIVE);

        // Verify host
        if (hostAllowedPattern.matcher(originUrl.getHost()).matches()) {
            httpServletResponse.addHeader("Access-Control-Allow-Origin", origin);

        } else {
            httpServletResponse.addHeader("Access-Control-Allow-Origin", "https://www.ed.ac.uk");
        }
    }
}
