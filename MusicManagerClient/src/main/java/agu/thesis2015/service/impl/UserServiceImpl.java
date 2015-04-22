/**
 * @author: nthienan
 * @created: Mar 23, 2015
 */

package agu.thesis2015.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import agu.thesis2015.domain.User;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.UserProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.model.TokenTranfer;
import agu.thesis2015.model.RequestData.Direction;
import agu.thesis2015.security.UserDetailService;
import agu.thesis2015.security.util.TokenUtils;
import agu.thesis2015.service.MailService;
import agu.thesis2015.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	public final static String AVARTAR_PATH = "/image";
	public final static String DEFAULT_AVARTAR = "/image/avartar.png";

	@Autowired
	private UserProducer producer;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailService userDetailService;

	@Autowired
	private MailService mailService;

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
	public Response insert(User user, MultipartFile avartar, HttpServletRequest request) {
		user.addRole("ROLE_USER");
		user.setActive(false);
		String imgPath = DEFAULT_AVARTAR;
		if (null != avartar) {
			try {
				imgPath = saveFile(avartar, user.getUsername(), request);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		user.setImage(imgPath);

		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, user.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
			// insert success
			if (response.getStatuscode() == 200) {
				RequestData requestData = new RequestData();
				requestData.setUsername(user.getUsername());
				Message msg = new Message(MessageMethod.GET, MessageAction.ACTIVE, requestData.toJson());
				Response receive = producer.sendAndReceive(msg);
				User u = User.fromJson(receive.getResponse().toString());
				mailService.sendActivateMail(user.getUsername(), user.getFullName(), user.getEmail(), TokenUtils.createActiveToken(u));
			}
		} catch (Exception e) {
			this.deleteOne(user.getUsername(), request);
			return new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getLocalizedMessage(), null);
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

	// @Override
	// public Response deleteAll() {
	// Message message = new Message(MessageMethod.DELETE,
	// MessageAction.DELETE_ALL, null);
	// Response response;
	// try {
	// response = producer.sendAndReceive(message);
	// } catch (Exception e) {
	// return null;
	// }
	// return response;
	// }

	@Override
	public Response delete(List<String> ids, HttpServletRequest request) {
		RequestData requestData = new RequestData();
		requestData.setListId(ids);
		for (String username : ids) {
			try {
				deleteMusicFile(username, request);
				deleteAvartarFile(Arrays.asList(username), request);
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
			deleteAvartarFile(Arrays.asList(username), request);
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
		Response response = this.getById(userDetails.getUsername());
		return response;
	}

	@Override
	public User getFullUser(String username) {
		RequestData requestData = new RequestData();
		requestData.addId(username);
		Message message = new Message(MessageMethod.GET, MessageAction.SECURITY, requestData.toJson());

		User user = null;
		try {
			Response response = producer.sendAndReceive(message);
			user = User.fromJson(response.getResponse().toString());
		} catch (Exception e) {
			return null;
		}

		return user;
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

	@Override
	public Response active(String username, String activeToken) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Message msg = new Message(MessageMethod.GET, MessageAction.ACTIVE, requestData.toJson());
		Response receive = null;
		try {
			receive = producer.sendAndReceive(msg);

			User user = User.fromJson(receive.getResponse().toString());
			boolean result = TokenUtils.validateActiveToken(activeToken, user);

			if (result) {
				RequestData data = new RequestData();
				data.addId(username);
				Message message = new Message(MessageMethod.PUT, MessageAction.ACTIVE, data.toJson());
				Response response = null;
				response = producer.sendAndReceive(message);
				return response;
			} else
				return new Response(ResponseStatus.BAD_REQUEST, 400, "Active code invalid!", null);
		} catch (Exception e) {
			return new Response(ResponseStatus.BAD_REQUEST, 400, "Some error while active: " + e.getLocalizedMessage() + ". Please try active again!", null);
		}
	}

	// private Map<String, Boolean> createRoleMap(UserDetails userDetails) {
	// Map<String, Boolean> roles = new HashMap<String, Boolean>();
	// for (GrantedAuthority authority : userDetails.getAuthorities()) {
	// roles.put(authority.getAuthority(), Boolean.TRUE);
	// }
	// return roles;
	// }

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

	private String saveFile(MultipartFile file, String username, HttpServletRequest request) throws IOException {
		String rootPath = request.getSession().getServletContext().getRealPath(AVARTAR_PATH);
		File dir = new File(rootPath);
		if (!dir.exists())
			dir.mkdirs();

		String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));

		String fileName = rootPath + File.separator + username + ext;
		File saveFile = new File(fileName);
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveFile));
		stream.write(file.getBytes());
		stream.close();
		return AVARTAR_PATH + "/" + username + ext;
	}

	private void deleteAvartarFile(List<String> username, HttpServletRequest request) throws Exception {
		if (username != null) {
			String realPath = request.getSession().getServletContext().getRealPath(AVARTAR_PATH);
			for (String id : username) {
				User user = this.getFullUser(id);
				File file = new File(realPath + File.separator + user.getImage().replace(AVARTAR_PATH, "").replace("/", ""));
				if (file.exists())
					file.delete();
			}
		}
	}
}
