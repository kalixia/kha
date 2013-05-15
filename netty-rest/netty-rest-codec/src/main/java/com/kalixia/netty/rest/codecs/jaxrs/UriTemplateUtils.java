package com.kalixia.netty.rest.codecs.jaxrs;

import java.util.regex.Pattern;

public class UriTemplateUtils {
    private static final Pattern uriTemplatePattern = Pattern.compile("\\{(.*)\\}");

    /**
     * Extract a Java regex pattern from a given JAX-RS URI template.
     * @param uriTemplate
     * @return
     */
    public static Pattern extractRegexPattern(String uriTemplate) {
        // strip last '/' if present
        if (uriTemplate.endsWith("/"))
            uriTemplate = uriTemplate.substring(0, uriTemplate.length() - 1);

        return Pattern.compile('^' + uriTemplatePattern.matcher(uriTemplate).replaceAll("(.*)") + "/?$");
    }

}
