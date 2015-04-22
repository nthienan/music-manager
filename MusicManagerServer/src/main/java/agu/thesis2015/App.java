package agu.thesis2015;

import org.apache.camel.CamelContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import agu.thesis2015.util.Util;

/**
 * @author ltduoc
 *
 */
public class App {
	private static AbstractApplicationContext context;

	public static void main(String[] args) throws Exception {

		System.out.println("Run app...");
		context = new ClassPathXmlApplicationContext("camel-server.xml");
		final CamelContext camel = context.getBean("camel-server", CamelContext.class);
		camel.start();
		Util util = context.getBean("util", Util.class);
		util.init();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					camel.stop();
					context.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
