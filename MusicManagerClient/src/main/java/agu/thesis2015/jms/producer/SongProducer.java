package agu.thesis2015.jms.producer;

import org.springframework.stereotype.Component;

@Component
public class SongProducer extends AbstractProducer {

	public static final String JSM_ENDPOINT_NAME = "activemq:SongQueue";

	public SongProducer() throws Exception {
		super(JSM_ENDPOINT_NAME);
	}
}