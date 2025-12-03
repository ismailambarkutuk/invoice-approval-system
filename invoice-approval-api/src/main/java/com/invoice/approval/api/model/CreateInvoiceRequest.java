package com.invoice.approval.api.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateInvoiceRequest", propOrder = {
    "invoiceNumber",
    "vendorName",
    "amount",
    "createdBy",
    "description"
})
public class CreateInvoiceRequest {
    
    @XmlElement(required = true)
    private String invoiceNumber;
    
    @XmlElement(required = true)
    private String vendorName;
    
    @XmlElement(required = true)
    private BigDecimal amount;
    
    @XmlElement(required = true)
    private String createdBy;
    
    private String description;
    
    // Getters and Setters
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

