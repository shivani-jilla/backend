package com.learnhub.dto;

public class SubmissionCreateRequest {

    private String textResponse;
    private String filePath;
    private String fileName;

    public SubmissionCreateRequest() {
    }

    public SubmissionCreateRequest(String textResponse, String filePath, String fileName) {
        this.textResponse = textResponse;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getTextResponse() {
        return textResponse;
    }

    public void setTextResponse(String textResponse) {
        this.textResponse = textResponse;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
