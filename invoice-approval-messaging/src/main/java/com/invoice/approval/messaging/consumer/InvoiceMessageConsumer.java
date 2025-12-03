package com.invoice.approval.messaging.consumer;

import com.invoice.approval.messaging.config.ActiveMQConfig;
import com.invoice.approval.messaging.model.InvoiceMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InvoiceMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceMessageConsumer.class);
    
    @Inject
    private ActiveMQConfig activeMQConfig;
    
    private MessageConsumer consumer;
    private Session session;
    private Connection connection;
    
    public void startConsumer(MessageListener listener) {
        try {
            connection = activeMQConfig.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(activeMQConfig.getQueueName());
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(listener);
            
            logger.info("Invoice message consumer started on queue: {}", activeMQConfig.getQueueName());
        } catch (JMSException e) {
            logger.error("Error starting invoice message consumer", e);
            throw new RuntimeException("Failed to start message consumer", e);
        }
    }
    
    public InvoiceMessage receiveMessage(long timeout) {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        
        try {
            connection = activeMQConfig.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(activeMQConfig.getQueueName());
            consumer = session.createConsumer(queue);
            
            Message message = consumer.receive(timeout);
            
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                InvoiceMessage invoiceMessage = (InvoiceMessage) objectMessage.getObject();
                logger.info("Received invoice message: {}", invoiceMessage);
                return invoiceMessage;
            }
            
            return null;
        } catch (JMSException e) {
            logger.error("Error receiving invoice message", e);
            throw new RuntimeException("Failed to receive message from ActiveMQ", e);
        } finally {
            closeResources(consumer, session, connection);
        }
    }
    
    public void stopConsumer() {
        closeResources(consumer, session, connection);
        logger.info("Invoice message consumer stopped");
    }
    
    private void closeResources(MessageConsumer consumer, Session session, Connection connection) {
        try {
            if (consumer != null) {
                consumer.close();
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

