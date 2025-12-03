package com.invoice.approval.messaging.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long invoiceId;
    private String invoiceNumber;
    private String vendorName;
    private BigDecimal amount;
    private String createdBy;
    private LocalDateTime createdAt;
    private String description;
    private String action; // "CREATE", "APPROVE", "REJECT"
    
    public InvoiceMessage() {
    }
    
    public InvoiceMessage(Long invoiceId, String invoiceNumber, String vendorName, 
                         BigDecimal amount, String createdBy, String action) {
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.vendorName = vendorName;
        this.amount = amount;
        this.createdBy = createdBy;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
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
    
    public String getVendorName() {
        return vendorName;
    }
    
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    @Override
    public String toString() {
        return "InvoiceMessage{" +
                "invoiceId=" + invoiceId +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", amount=" + amount +
                ", createdBy='" + createdBy + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}

