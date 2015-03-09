package agu.thesis2015.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import agu.thesis2015.domain.User;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.UserProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.RequestData.Direction;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.model.TokenTranfer;
import agu.thesis2015.model.UserTransfer;
import agu.thesis2015.security.TokenUtils;
import agu.thesis2015.security.UserDetailService;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
	@Autowired
	private UserProducer producer;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailService userService;

	// get all
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public Response getAll() throws Exception {
		Message message = new Message(MessageMethod.GET, MessageAction.GET_ALL, null);
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// get by id
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Response getById(@PathVariable String id) throws Exception {
		RequestData requestData = new RequestData();
		requestData.addId(id);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_BY_ID, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// insert
	@RequestMapping(value = "/regis", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response insert(@RequestBody User user) throws Exception {
		user.addRole("ROLE_USER");
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, user.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// paging
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public Response paging(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field) throws Exception {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(null, page, size, direction, field);

		Message message = new Message(MessageMethod.GET, MessageAction.PAGING, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// update but not pass
	@RequestMapping(method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response update(@RequestBody User user) throws Exception {
		Message message = new Message(MessageMethod.PUT, MessageAction.UPDATE, user.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// change pass
	@RequestMapping(value = "/{username}/pass", method = RequestMethod.PUT, produces = "application/json")
	public Response changePass(@PathVariable String username, @RequestParam(defaultValue = "") String oldPass, @RequestParam(defaultValue = "") String newPass)
			throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		requestData.addId(md5.encodePassword(oldPass, null));
		requestData.addId(md5.encodePassword(newPass, null));
		Message message = new Message(MessageMethod.PUT, MessageAction.SECURITY, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// delete all
	@RequestMapping(value = "/all", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteAll() throws Exception {
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE_ALL, null);
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// delete
	@RequestMapping(method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
	public Response delete(@RequestBody List<String> ids, HttpServletRequest request) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setListId(ids);
		for (String username : ids) {
			deleteMusicFile(username, request);
		}
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// delete one
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteOne(@PathVariable String username, HttpServletRequest request) throws Exception {
		RequestData requestData = new RequestData();
		requestData.addId(username);
		deleteMusicFile(username, request);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// statistics
	@RequestMapping(value = "/{username}/statistics", method = RequestMethod.GET, produces = "application/json")
	public Response statistics(@PathVariable String username) throws Exception {
		RequestData requestData = new RequestData(username, 0, 0, null, null);
		Message message = new Message(MessageMethod.GET, MessageAction.ORTHER, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// security
	@RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
	public Response getUser() throws Exception {
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

	@RequestMapping(value = "/authenticate/{username}/{password}", method = RequestMethod.POST, produces = "application/json")
	public TokenTranfer authenticate(@PathVariable String username, @PathVariable String password) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		password = md5.encodePassword(password, null);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		/*
		 * Reload user as password of authentication principal will be null
		 * after authorization and password is needed for token generation
		 */
		UserDetails userDetails = this.userService.loadUserByUsername(username);
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
