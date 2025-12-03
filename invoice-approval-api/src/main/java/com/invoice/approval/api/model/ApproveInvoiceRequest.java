package com.invoice.approval.api.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApproveInvoiceRequest", propOrder = {
    "invoiceId",
    "approverName",
    "comments"
})
public class ApproveInvoiceRequest {
    
    @XmlElement(required = true)
    private Long invoiceId;
    
    @XmlElement(required = true)
    private String approverName;
    
    private String comments;
    
    // Getters and Setters
    public Long getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public String getApproverName() {
        return approverName;
    }
    
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
}

