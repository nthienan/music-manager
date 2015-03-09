package agu.thesis2015.dao;

import agu.thesis2015.domain.Token;
import agu.thesis2015.model.Response;

public interface TokenDao {

	public Response insert(Token newToken);

	public Response update(Token newToken);

	public Response get(String seriesId);

	public Response delete(String username);

	public String tokenProcessor(String context);
}
