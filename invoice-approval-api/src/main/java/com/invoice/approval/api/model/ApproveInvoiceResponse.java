package com.invoice.approval.api.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproveInvoiceResponse", propOrder = {
    "success",
    "invoiceId",
    "status",
    "message"
})
public class ApproveInvoiceResponse {
    
    @XmlElement(required = true)
    private boolean success;
    
    @XmlElement(required = true)
    private Long invoiceId;
    
    @XmlElement(required = true)
    private String status;
    
    @XmlElement(required = true)
    private String message;
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Long getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

