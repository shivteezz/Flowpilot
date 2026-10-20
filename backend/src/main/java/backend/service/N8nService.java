package backend.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class N8nService {

    private final RestTemplate restTemplate;

    public N8nService() {
        this.restTemplate = new RestTemplate();
    }

    public String sendEmail(
            String recipient,
            String subject,
            String body,
            String scheduledAt
    ) {

        String webhookUrl =
                "http://localhost:5678/webhook/flowpilot-email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> data = Map.of(
                "recipient", recipient,
                "subject", subject,
                "body", body,
                "scheduledAt", scheduledAt == null ? "" : scheduledAt
        );

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(data, headers);

        return restTemplate.postForObject(
                webhookUrl,
                request,
                String.class
        );
    }
}