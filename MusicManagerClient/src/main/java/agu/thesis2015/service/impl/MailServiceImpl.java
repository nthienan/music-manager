/**
 * @author: nthienan
 */

package agu.thesis2015.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import agu.thesis2015.service.MailService;

@Service
public class MailServiceImpl implements MailService {

	private final String site = "http://localhost:8080/#active/";

	@Autowired
	private MailSender mailSender;

	@Override
	public void sendActivateMail(String username, String name, String emailAdderss, String token) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(emailAdderss);
		email.setSubject("Welcome to Music Manager Site");
		StringBuilder builder = new StringBuilder("Hi ");
		builder.append(name);
		builder.append(", \n\nTo complete registration at Music Manager site you need click the link below. If the link not work please copy and paste it into address bar browser.\n\nLink: ");
		builder.append(site + username + "/" + token);
		builder.append("\n\nThanks and Best regards\nMusic Manager Team");
		email.setText(builder.toString());
		mailSender.send(email);
	}
}
