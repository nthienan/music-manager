package agu.thesis2015.jms.producer;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import agu.thesis2015.jms.message.Message;
import agu.thesis2015.model.Response;

public abstract class AbstractProducer {
	
	protected Producer producer;
	protected Endpoint endpoint;
	protected CamelContext camelContext;
	protected ConfigurableApplicationContext ctx;

	public AbstractProducer(String destinationName) throws Exception {
		init(destinationName);
	}

	private void init(String destinationName) throws Exception {
		ctx = new ClassPathXmlApplicationContext("camel-client.xml");
		camelContext = ctx.getBean("camel-client", CamelContext.class);
		camelContext.start();
		endpoint = camelContext.getEndpoint(destinationName);
		producer = endpoint.createProducer();
		producer.start();
	}

	protected Exchange buildMessage(Message message) {
		Exchange exchange = endpoint.createExchange();
		exchange.getIn().setBody(message.toJson());
		exchange.setPattern(ExchangePattern.InOut);
		return exchange;
	}

	public Response sendAndReceive(Message message) throws Exception {
		Exchange exchange = buildMessage(message);
		producer.process(exchange);
		Response response = Response.fromJson(exchange.getOut().getBody(String.class));
		return response;
	}

	public void destroy() {
		ctx.close();
		try {
			producer.stop();
			camelContext.stop();
		} catch (Exception e) {
		}
		producer = null;
	}
}
