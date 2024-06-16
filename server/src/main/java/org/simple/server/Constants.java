package org.simple.server;

import org.springframework.util.MimeType;

public class Constants {
    public static final String CUSTOM_HEADER = "custom-header";
    public static final MimeType CUSTOM_HEADER_MIMETYPE = MimeType.valueOf("messaging/" + CUSTOM_HEADER);
}
