package com.invoice.approval.messaging.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

@ApplicationScoped
public class ActiveMQConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(ActiveMQConfig.class);
    
    private static final String BROKER_URL = System.getenv().getOrDefault(
        "ACTIVE_MQ_BROKER_URL", "tcp://localhost:61616");
    private static final String QUEUE_NAME = "invoice.approval.queue";
    
    private ConnectionFactory connectionFactory;
    private Connection connection;
    
    @PostConstruct
    public void init() {
        try {
            connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
            logger.info("ActiveMQ connection established successfully");
        } catch (JMSException e) {
            logger.error("Failed to establish ActiveMQ connection", e);
            throw new RuntimeException("Failed to initialize ActiveMQ", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("ActiveMQ connection closed");
            } catch (JMSException e) {
                logger.error("Error closing ActiveMQ connection", e);
            }
        }
    }
    
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
    
    public Connection getConnection() throws JMSException {
        return connectionFactory.createConnection();
    }
    
    public String getQueueName() {
        return QUEUE_NAME;
    }
}

