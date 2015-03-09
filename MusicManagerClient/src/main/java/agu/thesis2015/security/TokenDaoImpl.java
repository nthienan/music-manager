package agu.thesis2015.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Component;

import agu.thesis2015.domain.Token;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.TokenProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;

@Component
public class TokenDaoImpl implements TokenDao {

	@Autowired
	private TokenProducer producer;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		Token myToken = new Token(token);
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, myToken.toJson());
		try {
			producer.sendAndReceive(message);
		} catch (Exception e) {
		}
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		Token token = new Token(getTokenForSeries(series));
		token.setTokenValue(tokenValue);
		token.setDate(lastUsed);
		Message message = new Message(MessageMethod.PUT, MessageAction.UPDATE, token.toJson());
		try {
			producer.sendAndReceive(message);
		} catch (Exception e) {
		}
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		RequestData requestData = new RequestData();
		requestData.addId(seriesId);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_BY_ID, requestData.toJson());
		Response response = null;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
		}
		Token myToken = Token.fromJson(response.getResponse().toString());
		PersistentRememberMeToken token = new PersistentRememberMeToken(myToken.getUsername(), myToken.getSeries(), myToken.getTokenValue(), myToken.getDate());
		return token;
	}

	@Override
	public void removeUserTokens(String username) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE_ALL, requestData.toJson());
		try {
			producer.sendAndReceive(message);
		} catch (Exception e) {
		}
	}
}
