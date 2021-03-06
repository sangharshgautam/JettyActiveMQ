package com.webspherenotes.jms;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagePublishingServlet extends HttpServlet {
	public static final String JMS_CONNECTION_FACTORY = "java:comp/env/jms/ConnectionFactory";
	Logger logger = LoggerFactory.getLogger(MessagePublishingServlet.class);
	Connection connection;
	Queue queue;

	@Override
	public void init() throws ServletException {
		logger.debug("Entering MessagePublishingServlet.init()");
		try {
			InitialContext context = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) context
					.lookup(JMS_CONNECTION_FACTORY);
			logger.debug("Connection Factory " + connectionFactory);
			try {
				connection = connectionFactory.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			queue = (Queue) context.lookup("jms/FooQueue");
			logger.debug("After looking up the queue " + queue);
		} catch (Exception e) {
			logger.error("Error occured in MessagePublishingServlet.init() "
					+ e.getMessage(), e);
		}
		logger.debug("Exiting MessagePublishingServlet.init()");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("Entering MessagePublishingServlet.doGet()");
		resp.setContentType("text/html");
		resp.getWriter().println("Hello from MessagePublishingServlet.doGet()");
		try {
			test();
			System.out.println("Connection: "+connection);
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			TextMessage textMessage = session.createTextMessage();
			textMessage.setText(req.getParameter("message"));
			MessageProducer queueSender = session.createProducer(queue);
			queueSender.send(textMessage);
		} catch (JMSException e) {
			logger.error("Error occured in MessagePublishingServlet.doGet() "
					+ e.getMessage(), e);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		logger.debug("Exiting MessagePublishingServlet.doGet()");
	}

	private void test() throws NamingException {
		InitialContext c = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory) c
				.lookup("java:comp/env/jms/ConnectionFactory");
		System.out.println("CF "+cf);
	}
}