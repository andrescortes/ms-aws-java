package org.myorg.domain;

import java.util.Map;
import lombok.Builder;

@Builder
public class Response {

    private int statusCode;
    private Map<String, String> headers;
    private String body;
}
