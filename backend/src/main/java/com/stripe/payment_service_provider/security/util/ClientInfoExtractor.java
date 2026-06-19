package com.stripe.payment_service_provider.security.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ClientInfoExtractor {

    private final Parser uaParser;

    public ClientInfoExtractor() {
        this.uaParser = new Parser();
    }

    public Map<String, String> getClientInfo(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("user-agent");
        Client client = uaParser.parse(userAgent);

        Map<String, String> clientInfo = new HashMap<>();
        clientInfo.put("os_family", client.os.family);
        clientInfo.put("device_family", client.device.family);
        clientInfo.put("userAgent_family", client.userAgent.family);
        clientInfo.put("remote_address", ipAddress);

        return clientInfo;
    }
}

