/**
 * @author: nthienan
 * @created: Mar 23, 2015
 */

package agu.thesis2015.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import agu.thesis2015.controller.UserController;
import agu.thesis2015.domain.User;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.UserProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.TokenTranfer;
import agu.thesis2015.model.UserTransfer;
import agu.thesis2015.model.RequestData.Direction;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.security.TokenUtils;
import agu.thesis2015.security.UserDetailService;

@Service
public class UserService implements UserController {

	@Autowired
	private UserProducer producer;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailService userDetailService;

	@Override
	public Response getAll() {
		Message message = new Message(MessageMethod.GET, MessageAction.GET_ALL, null);
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response getById(String id) {
		RequestData requestData = new RequestData();
		requestData.addId(id);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_BY_ID, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response insert(User user) {
		user.addRole("ROLE_USER");
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, user.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response paging(int page, int size, String sort, String field) {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(null, page, size, direction, field);

		Message message = new Message(MessageMethod.GET, MessageAction.PAGING, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response update(User user) {
		Message message = new Message(MessageMethod.PUT, MessageAction.UPDATE, user.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response changePass(String username, String oldPass, String newPass) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		requestData.addId(md5.encodePassword(oldPass, null));
		requestData.addId(md5.encodePassword(newPass, null));
		Message message = new Message(MessageMethod.PUT, MessageAction.SECURITY, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response deleteAll() {
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE_ALL, null);
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response delete(List<String> ids, HttpServletRequest request) {
		RequestData requestData = new RequestData();
		requestData.setListId(ids);
		for (String username : ids) {
			try {
				deleteMusicFile(username, request);
			} catch (Exception e) {
			}
		}
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response deleteOne(String username, HttpServletRequest request) {
		RequestData requestData = new RequestData();
		requestData.addId(username);
		try {
			deleteMusicFile(username, request);
		} catch (Exception e) {
		}
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response statistics(String username) {
		RequestData requestData = new RequestData(username, 0, 0, null, null);
		Message message = new Message(MessageMethod.GET, MessageAction.ORTHER, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof String && ((String) principal).equals("anonymousUser")) {
			throw new WebApplicationException(401);
		}
		UserDetails userDetails = (UserDetails) principal;
		UserTransfer user = new UserTransfer(userDetails.getUsername(), createRoleMap(userDetails));
		Response response = new Response(ResponseStatus.OK, 200, "Success", user);
		return response;
	}

	@Override
	public TokenTranfer authenticate(String username, String password) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		password = md5.encodePassword(password, null);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		/*
		 * Reload user as password of authentication principal will be null
		 * after authorization and password is needed for token generation
		 */
		UserDetails userDetails = this.userDetailService.loadUserByUsername(username);
		TokenTranfer tokenTranfer = new TokenTranfer(TokenUtils.createToken(userDetails));
		return tokenTranfer;
	}

	private Map<String, Boolean> createRoleMap(UserDetails userDetails) {
		Map<String, Boolean> roles = new HashMap<String, Boolean>();
		for (GrantedAuthority authority : userDetails.getAuthorities()) {
			roles.put(authority.getAuthority(), Boolean.TRUE);
		}
		return roles;
	}

	@SuppressWarnings("unchecked")
	private List<String> getSongIds(String username) throws Exception {
		RequestData requestData = new RequestData(username, 0, 0, null, null);
		Message message = new Message(MessageMethod.DELETE, MessageAction.ORTHER, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		if (response.getResponse() != null) {
			List<String> songIds = new Gson().fromJson(response.getResponse().toString(), List.class);
			return songIds;
		}
		return null;
	}

	private void deleteMusicFile(String username, HttpServletRequest request) throws Exception {
		List<String> ids = getSongIds(username);
		if (ids != null) {
			String realPath = request.getSession().getServletContext().getRealPath("/music");
			for (String id : ids) {
				File file = new File(realPath + File.separator + id + ".mp3");
				if (file.exists())
					file.delete();
			}
		}
	}

}
