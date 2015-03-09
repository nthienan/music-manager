package agu.thesis2015.jms;

import org.apache.camel.builder.RouteBuilder;

/**
 * 
 * @author ltduoc
 *
 */
public class ServerRoutes extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("activemq:SongQueue").beanRef("songDao", "songProcessor");
		from("activemq:UserQueue").beanRef("userDao", "userProcessor");
		from("activemq:TokenQueue").beanRef("tokenDao", "tokenProcessor");
	}

}
