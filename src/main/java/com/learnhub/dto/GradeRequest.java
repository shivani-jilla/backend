package com.learnhub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class GradeRequest {

    @NotNull(message = "Marks awarded is required")
    @DecimalMin(value = "0.00", message = "Marks cannot be negative")
    private BigDecimal marksAwarded;

    private String feedback;

    public GradeRequest() {
    }

    public GradeRequest(BigDecimal marksAwarded, String feedback) {
        this.marksAwarded = marksAwarded;
        this.feedback = feedback;
    }

    public BigDecimal getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(BigDecimal marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
