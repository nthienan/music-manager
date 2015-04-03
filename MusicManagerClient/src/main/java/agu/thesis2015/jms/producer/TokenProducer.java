/**
 * @author: nthienan
 */

package agu.thesis2015.jms.producer;

import org.springframework.stereotype.Component;

@Component
public class TokenProducer extends AbstractProducer {
	
	public static final String JSM_ENDPOINT_NAME = "activemq:TokenQueue";
	
	public TokenProducer() throws Exception {
		super(JSM_ENDPOINT_NAME);
	}

}
