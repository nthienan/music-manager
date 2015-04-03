/**
 * @author: nthienan
 * @created: Mar 23, 2015
 */

package agu.thesis2015.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import agu.thesis2015.domain.User;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.TokenTranfer;

public interface UserController {
	public Response getAll();

	public Response getById(String id);

	public Response insert(User user);

	public Response paging(int page, int size, String sort, String field);

	public Response update(User user);

	public Response changePass(String username, String oldPass, String newPass);

	public Response deleteAll();

	public Response delete(List<String> ids, HttpServletRequest request);

	public Response deleteOne(String username, HttpServletRequest request);

	public Response statistics(String username);

	public Response getUser();

	public TokenTranfer authenticate(String username, String password);
}
