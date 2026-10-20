package backend.controller;

import backend.model.JobResult;
import backend.service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/search")
    public List<JobResult> searchJobs(
            @RequestParam String role,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "") String skills
    ) {

        return jobService.searchJobs(
                role,
                location,
                skills
        );
    }
}