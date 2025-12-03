package com.invoice.approval.api.service;

import com.invoice.approval.api.model.*;
import com.invoice.approval.data.entity.Invoice;
import com.invoice.approval.data.entity.InvoiceStatus;
import com.invoice.approval.data.repository.InvoiceRepository;
import com.invoice.approval.messaging.model.InvoiceMessage;
import com.invoice.approval.messaging.producer.InvoiceMessageProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class InvoiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    
    @Inject
    private InvoiceRepository invoiceRepository;
    
    @Inject
    private InvoiceMessageProducer messageProducer;
    
    @Transactional
    public CreateInvoiceResponse createInvoice(CreateInvoiceRequest request) {
        CreateInvoiceResponse response = new CreateInvoiceResponse();
        
        try {
            // Check if invoice number already exists
            Optional<Invoice> existingInvoice = invoiceRepository.findByInvoiceNumber(request.getInvoiceNumber());
            if (existingInvoice.isPresent()) {
                response.setSuccess(false);
                response.setMessage("Invoice with number " + request.getInvoiceNumber() + " already exists");
                return response;
            }
            
            // Create new invoice
            Invoice invoice = new Invoice(
                request.getInvoiceNumber(),
                request.getVendorName(),
                request.getAmount(),
                request.getCreatedBy()
            );
            
            if (request.getDescription() != null) {
                invoice.setDescription(request.getDescription());
            }
            
            invoice.setStatus(InvoiceStatus.IN_APPROVAL);
            invoice = invoiceRepository.save(invoice);
            
            // Send message to approval queue
            InvoiceMessage message = new InvoiceMessage(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getVendorName(),
                invoice.getAmount(),
                invoice.getCreatedBy(),
                "CREATE"
            );
            message.setDescription(invoice.getDescription());
            messageProducer.sendInvoiceForApproval(message);
            
            response.setSuccess(true);
            response.setInvoiceId(invoice.getId());
            response.setInvoiceNumber(invoice.getInvoiceNumber());
            response.setMessage("Invoice created successfully and sent for approval");
            
            logger.info("Invoice created: {}", invoice.getInvoiceNumber());
            
        } catch (Exception e) {
            logger.error("Error creating invoice", e);
            response.setSuccess(false);
            response.setMessage("Error creating invoice: " + e.getMessage());
        }
        
        return response;
    }
    
    @Transactional
    public ApproveInvoiceResponse approveInvoice(ApproveInvoiceRequest request) {
        ApproveInvoiceResponse response = new ApproveInvoiceResponse();
        
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(request.getInvoiceId());
            
            if (invoiceOpt.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Invoice not found with ID: " + request.getInvoiceId());
                return response;
            }
            
            Invoice invoice = invoiceOpt.get();
            
            if (invoice.getStatus() == InvoiceStatus.APPROVED) {
                response.setSuccess(false);
                response.setMessage("Invoice is already approved");
                response.setStatus(invoice.getStatus().name());
                return response;
            }
            
            // Approve invoice
            invoice.setStatus(InvoiceStatus.APPROVED);
            invoice.setApprovedBy(request.getApproverName());
            invoice.setApprovedAt(java.time.LocalDateTime.now());
            invoice = invoiceRepository.save(invoice);
            
            // Send approval message
            InvoiceMessage message = new InvoiceMessage(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getVendorName(),
                invoice.getAmount(),
                request.getApproverName(),
                "APPROVE"
            );
            messageProducer.sendInvoiceForApproval(message);
            
            response.setSuccess(true);
            response.setInvoiceId(invoice.getId());
            response.setStatus(invoice.getStatus().name());
            response.setMessage("Invoice approved successfully");
            
            logger.info("Invoice approved: {} by {}", invoice.getInvoiceNumber(), request.getApproverName());
            
        } catch (Exception e) {
            logger.error("Error approving invoice", e);
            response.setSuccess(false);
            response.setMessage("Error approving invoice: " + e.getMessage());
        }
        
        return response;
    }
    
    @Transactional
    public ApproveInvoiceResponse rejectInvoice(ApproveInvoiceRequest request) {
        ApproveInvoiceResponse response = new ApproveInvoiceResponse();
        
        try {
            Optional<Invoice> invoiceOpt = invoiceRepository.findById(request.getInvoiceId());
            
            if (invoiceOpt.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Invoice not found with ID: " + request.getInvoiceId());
                return response;
            }
            
            Invoice invoice = invoiceOpt.get();
            invoice.setStatus(InvoiceStatus.REJECTED);
            invoice = invoiceRepository.save(invoice);
            
            // Send rejection message
            InvoiceMessage message = new InvoiceMessage(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getVendorName(),
                invoice.getAmount(),
                request.getApproverName(),
                "REJECT"
            );
            messageProducer.sendInvoiceForApproval(message);
            
            response.setSuccess(true);
            response.setInvoiceId(invoice.getId());
            response.setStatus(invoice.getStatus().name());
            response.setMessage("Invoice rejected");
            
            logger.info("Invoice rejected: {} by {}", invoice.getInvoiceNumber(), request.getApproverName());
            
        } catch (Exception e) {
            logger.error("Error rejecting invoice", e);
            response.setSuccess(false);
            response.setMessage("Error rejecting invoice: " + e.getMessage());
        }
        
        return response;
    }
}

