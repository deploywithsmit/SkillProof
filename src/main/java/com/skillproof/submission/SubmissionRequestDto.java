package com.skillproof.submission;

import jakarta.validation.constraints.Size;

public class SubmissionRequestDto {

    @Size(max = 255, message = "Repository URL must not exceed 255 characters")
    private String repoUrl;

    @Size(max = 255, message = "Project URL must not exceed 255 characters")
    private String projectUrl;

    @Size(max = 5000, message = "Explanation must not exceed 5000 characters")
    private String explanation;

    private boolean isDraft = false;

    public SubmissionRequestDto() {}

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }
}
