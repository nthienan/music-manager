package agu.thesis2015.jms.producer;

import org.springframework.stereotype.Component;

@Component
public class UserProducer extends AbstractProducer {

	public static final String JSM_ENDPOINT_NAME = "activemq:UserQueue";
	
	public UserProducer() throws Exception {
		super(JSM_ENDPOINT_NAME);
	}
}
