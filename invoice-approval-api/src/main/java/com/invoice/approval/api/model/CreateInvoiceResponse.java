package com.invoice.approval.api.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateInvoiceResponse", propOrder = {
    "success",
    "invoiceId",
    "invoiceNumber",
    "message"
})
public class CreateInvoiceResponse {
    
    @XmlElement(required = true)
    private boolean success;
    
    private Long invoiceId;
    
    @XmlElement(required = true)
    private String invoiceNumber;
    
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
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

