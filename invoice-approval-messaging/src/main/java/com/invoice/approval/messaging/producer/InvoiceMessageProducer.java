package com.invoice.approval.messaging.producer;

import com.invoice.approval.messaging.config.ActiveMQConfig;
import com.invoice.approval.messaging.model.InvoiceMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InvoiceMessageProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceMessageProducer.class);
    
    @Inject
    private ActiveMQConfig activeMQConfig;
    
    public void sendInvoiceForApproval(InvoiceMessage message) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        
        try {
            connection = activeMQConfig.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(activeMQConfig.getQueueName());
            producer = session.createProducer(queue);
            
            ObjectMessage objectMessage = session.createObjectMessage(message);
            producer.send(objectMessage);
            
            logger.info("Invoice message sent to queue: {}", message);
        } catch (JMSException e) {
            logger.error("Error sending invoice message to queue", e);
            throw new RuntimeException("Failed to send message to ActiveMQ", e);
        } finally {
            closeResources(producer, session, connection);
        }
    }
    
    private void closeResources(MessageProducer producer, Session session, Connection connection) {
        try {
            if (producer != null) {
                producer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            logger.error("Error closing JMS resources", e);
        }
    }
}

