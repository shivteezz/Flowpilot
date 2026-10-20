package backend.service;

import backend.model.JobResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JobService {

    private final RestTemplate restTemplate;

    public JobService() {
        this.restTemplate = new RestTemplate();
    }

    public List<JobResult> searchJobs(
            String role,
            String location,
            String skills
    ) {

        System.out.println("=================================");
        System.out.println("JOB SEARCH STARTED");
        System.out.println("Role: " + role);
        System.out.println("Location: " + location);
        System.out.println("Skills: " + skills);
        System.out.println("=================================");

        String apiUrl =
                "https://www.arbeitnow.com/api/job-board-api";

        try {

            Map response =
                    restTemplate.getForObject(
                            apiUrl,
                            Map.class
                    );

            if (response == null) {
                throw new RuntimeException(
                        "Empty response from job API"
                );
            }

            List<Map<String, Object>> jobs =
                    (List<Map<String, Object>>) response.get("data");

            if (jobs == null) {
                throw new RuntimeException(
                        "Job API returned no data"
                );
            }

            List<JobResult> exactMatches =
                    new ArrayList<>();

            List<JobResult> broaderMatches =
                    new ArrayList<>();

            String roleText =
                    role == null ? "" : role.toLowerCase();

            String locationText =
                    location == null ? "" : location.toLowerCase();

            String skillsText =
                    skills == null ? "" : skills.toLowerCase();

            for (Map<String, Object> job : jobs) {

                String title =
                        getString(job, "title");

                String company =
                        getString(job, "company_name");

                String jobLocation =
                        getString(job, "location");

                String url =
                        getString(job, "url");

                String description =
                        getString(job, "description");

                boolean remote =
                        Boolean.TRUE.equals(
                                job.get("remote")
                        );

                String searchableText =
                        (
                                title + " " +
                                        company + " " +
                                        jobLocation + " " +
                                        description
                        ).toLowerCase();

                boolean roleMatches =
                        roleText.isBlank()
                                ||
                                searchableText.contains(roleText);

                boolean skillsMatch = true;

                if (!skillsText.isBlank()) {

                    String[] requiredSkills =
                            skillsText.split(",");

                    for (String skill : requiredSkills) {

                        String cleanSkill =
                                skill.trim().toLowerCase();

                        if (
                                !cleanSkill.isBlank()
                                        &&
                                        !searchableText.contains(cleanSkill)
                        ) {
                            skillsMatch = false;
                            break;
                        }
                    }
                }

                boolean locationMatches =
                        locationText.isBlank()
                                ||
                                jobLocation
                                        .toLowerCase()
                                        .contains(locationText)
                                ||
                                searchableText
                                        .contains(locationText);

                if (roleMatches && skillsMatch) {

                    JobResult result =
                            new JobResult(
                                    title,
                                    company,
                                    jobLocation,
                                    url,
                                    cleanDescription(description),
                                    "Arbeitnow",
                                    remote
                            );

                    if (locationMatches) {
                        exactMatches.add(result);
                    } else if (remote) {
                        broaderMatches.add(result);
                    }
                }
            }

            /*
             * Prefer exact location matches.
             * If none exist, show matching remote jobs.
             */
            List<JobResult> results =
                    !exactMatches.isEmpty()
                            ? exactMatches
                            : broaderMatches;

            if (results.size() > 10) {
                results =
                        results.subList(0, 10);
            }

            System.out.println(
                    "JOB RESULTS FOUND: "
                            + results.size()
            );

            return results;

        } catch (Exception e) {

            System.out.println(
                    "JOB SEARCH ERROR: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Job search failed: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private String getString(
            Map<String, Object> job,
            String key
    ) {

        Object value = job.get(key);

        return value == null
                ? ""
                : value.toString();
    }

    private String cleanDescription(
            String description
    ) {

        if (description == null) {
            return "";
        }

        return description
                .replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}