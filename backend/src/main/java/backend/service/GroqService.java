package backend.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final RestTemplate restTemplate;

    public GroqService() {
        this.restTemplate = new RestTemplate();
    }

    public String understandAction(String userRequest) {

        String apiKey = System.getenv("GROQ_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException(
                    "GROQ_API_KEY is not configured"
            );
        }

        String systemPrompt = """
                You are FlowPilot AI, an intelligent AI assistant.

                You have TWO main capabilities.

                CAPABILITY 1: GENERAL AI

                If the user asks a normal question, wants an explanation,
                wants help with coding, studying, writing, brainstorming,
                or any other general question, use:

                GENERAL_QUERY

                For GENERAL_QUERY, provide the answer directly in the
                "answer" field.

                Example:

                User:
                What is recursion?

                Output:
                {
                  "action": "GENERAL_QUERY",
                  "answer": "Recursion is a programming technique where..."
                }


                CAPABILITY 2: EMAIL AUTOMATION

                If the user asks FlowPilot to send, compose, or schedule
                an email, use:

                SEND_EMAIL

                Extract:

                recipient
                subject
                body
                scheduledAt

                Example:

                User:
                Send an email to test@gmail.com saying the meeting is
                tomorrow at 10 AM.

                Output:
                {
                  "action": "SEND_EMAIL",
                  "recipient": "test@gmail.com",
                  "subject": "Meeting Reminder",
                  "body": "The meeting is tomorrow at 10 AM.",
                  "scheduledAt": ""
                }


                For scheduled emails, scheduledAt must be an ISO datetime
                in this format:

                YYYY-MM-DDTHH:MM:SS

                If no scheduling information exists, use:

                "scheduledAt": ""


                IMPORTANT:

                Return ONLY valid JSON.

                Do not use markdown.

                Do not explain the JSON.

                Use exactly one of:

                GENERAL_QUERY
                SEND_EMAIL
                """;

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        headers.setAccept(
                List.of(MediaType.APPLICATION_JSON)
        );

        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody =
                Map.of(
                        "model",
                        "openai/gpt-oss-20b",

                        "messages",
                        List.of(

                                Map.of(
                                        "role",
                                        "system",
                                        "content",
                                        systemPrompt
                                ),

                                Map.of(
                                        "role",
                                        "user",
                                        "content",
                                        userRequest
                                )
                        ),

                        "temperature",
                        0.2,

                        "response_format",
                        Map.of(
                                "type",
                                "json_object"
                        )
                );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        requestBody,
                        headers
                );

        try {

            System.out.println(
                    "Calling Groq..."
            );

            Map response =
                    restTemplate.postForObject(
                            "https://api.groq.com/openai/v1/chat/completions",
                            request,
                            Map.class
                    );

            if (response == null) {
                throw new RuntimeException(
                        "Empty response from Groq"
                );
            }

            List choices =
                    (List) response.get("choices");

            if (
                    choices == null ||
                            choices.isEmpty()
            ) {
                throw new RuntimeException(
                        "Groq returned no choices: "
                                + response
                );
            }

            Map choice =
                    (Map) choices.get(0);

            Map message =
                    (Map) choice.get("message");

            if (message == null) {
                throw new RuntimeException(
                        "Groq response contained no message"
                );
            }

            String content =
                    message.get(
                            "content"
                    ).toString();

            System.out.println(
                    "RAW GROQ OUTPUT = "
                            + content
            );

            return content;

        } catch (Exception e) {

            System.out.println(
                    "GROQ ERROR = "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Groq API request failed: "
                            + e.getMessage(),
                    e
            );
        }
    }
}