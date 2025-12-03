package com.invoice.approval.api.soap;

import com.invoice.approval.api.model.*;
import com.invoice.approval.api.service.InvoiceService;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.SOAPBinding;

@WebService(
    name = "InvoiceWebService",
    targetNamespace = "http://api.approval.invoice.com/",
    serviceName = "InvoiceWebService",
    portName = "InvoiceWebServicePort"
)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class InvoiceWebService {
    
    @Inject
    private InvoiceService invoiceService;
    
    @WebMethod(operationName = "createInvoice")
    @WebResult(name = "createInvoiceResponse")
    public CreateInvoiceResponse createInvoice(
        @WebParam(name = "createInvoiceRequest") CreateInvoiceRequest request
    ) {
        return invoiceService.createInvoice(request);
    }
    
    @WebMethod(operationName = "approveInvoice")
    @WebResult(name = "approveInvoiceResponse")
    public ApproveInvoiceResponse approveInvoice(
        @WebParam(name = "approveInvoiceRequest") ApproveInvoiceRequest request
    ) {
        return invoiceService.approveInvoice(request);
    }
    
    @WebMethod(operationName = "rejectInvoice")
    @WebResult(name = "rejectInvoiceResponse")
    public ApproveInvoiceResponse rejectInvoice(
        @WebParam(name = "rejectInvoiceRequest") ApproveInvoiceRequest request
    ) {
        return invoiceService.rejectInvoice(request);
    }
}

