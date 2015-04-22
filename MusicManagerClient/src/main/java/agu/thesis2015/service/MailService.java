package agu.thesis2015.service;

public interface MailService {
	void sendActivateMail(String username, String name, String emailAdderss, String token);
}
