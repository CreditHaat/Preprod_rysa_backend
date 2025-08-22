package com.lsp.web.entity;

import jakarta.persistence.*;
import java.util.Date;

import com.lsp.web.genericentity.BaseEntity;

@Entity
@Table(name = "t_journey_logger")
public class Logger extends BaseEntity{


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_log_id", nullable = false)
    private JourneyLog journeyLog;  // FK to JourneyLog table

    @Column(name = "url", length = 512)
    private String url;

    @Lob
    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Lob
    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;


    // Getters and Setters

    public JourneyLog getJourneyLog() {
        return journeyLog;
    }

    public void setJourneyLog(JourneyLog journeyLog) {
        this.journeyLog = journeyLog;
    }

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

}
