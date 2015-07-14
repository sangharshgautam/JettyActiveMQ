package com.webspherenotes.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jndi.ActiveMQInitialContextFactory;
public class RemoteSender {
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageProducer producer = null;
    public RemoteSender() {
    }
    public void sendMessage() throws NamingException {
        try {
        	final Properties env = new Properties();
//            env.put(Context.INITIAL_CONTEXT_FACTORY, ActiveMQInitialContextFactory.class.getName());
            env.put(Context.PROVIDER_URL, "remote://52.18.6.135:3700");
            
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.impl.SerialInitContextFactory");
            env.put(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
            env.put(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
            
            /*env.put(Context.SECURITY_PRINCIPAL, "user1");
            env.put(Context.SECURITY_CREDENTIALS, "pass1");*/
            Context remoteContext = new InitialContext(env);
             
            ConnectionFactory factory =                         
                (ConnectionFactory)remoteContext.lookup("jms/__defaultConnectionFactory");
        	
        	/*factory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_BROKER_URL);*/
            connection = factory.createConnection(/*"sa", "manager"*/);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("SAMPLEQUEUE");
            producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage();
            message.setText("Hello ...Sangharsh");
            producer.send(message);
            System.out.println("Sent: " + message.getText());

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
        	try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
        }

    }
    public static void main(String[] args) {
        Sender sender = new Sender();
        sender.sendMessage();
    }
}
