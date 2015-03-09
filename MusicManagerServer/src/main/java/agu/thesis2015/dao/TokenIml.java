package agu.thesis2015.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agu.thesis2015.domain.Token;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.repo.TokenRepo;

@Service(value = "tokenDao")
public class TokenIml implements TokenDao {
	@Autowired
	TokenRepo tokenRepo;

	private Response getResponse(ResponseStatus status, int statuscode, String message, Object response) {
		return new Response(status, statuscode, message, response);
	}

	@Override
	public String tokenProcessor(String context) {
		String response = null;
		Message message = Message.fromJson(context);
		RequestData request = null;
		if (message.getData() != null)
			request = RequestData.fromJson(message.getData().toString());
		switch (message.getMethod()) {
		case GET: {
			if (message.getAction().equals(MessageAction.GET_ALL)) {
				response = get(request.getListId().get(0)).toJson();

			}
			break;
		}
		case POST: {
			if (message.getAction().equals(MessageAction.INSERT)) {

				Token newToken = Token.fromJson(String.valueOf(message.getData()));
				response = insert(newToken).toJson();

			}
			break;
		}
		case PUT: {
			if (message.getAction().equals(MessageAction.UPDATE)) {

				Token newToken = Token.fromJson(String.valueOf(message.getData()));
				response = update(newToken).toJson();

			}
			break;
		}
		case DELETE: {
			if (message.getAction().equals(MessageAction.DELETE_ALL)) {
				response = delete(request.getUsername()).toJson();

			}
			break;
		}
		default:
			break;
		}
		return response;
	}

	@Override
	public Response insert(Token newToken) {
		try {
			if (tokenRepo.exists(newToken.getSeries())) {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "The token is exsits", null);
			} else {
				tokenRepo.save(newToken);
				return getResponse(ResponseStatus.OK, 200, "Insert success", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response update(Token newToken) {
		try {
			if (tokenRepo.exists(newToken.getSeries())) {

				tokenRepo.save(newToken);
				return getResponse(ResponseStatus.OK, 200, "Update success", null);
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "The user isn't exsits", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response get(String seriesId) {
		try {
			Token token = tokenRepo.findOne(seriesId);
			if (token != null) {
				return getResponse(ResponseStatus.OK, 200, "Get token success", token.toJson());
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Token isn't exsist", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response delete(String username) {
		try {
			List<Token> tokens = tokenRepo.findByUsername(username);
			tokenRepo.delete(tokens);
			return getResponse(ResponseStatus.OK, 200, "Delete all success", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

}
