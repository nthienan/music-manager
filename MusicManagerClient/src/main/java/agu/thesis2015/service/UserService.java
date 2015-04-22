/**
 * @author: nthienan
 * @created: Mar 23, 2015
 */

package agu.thesis2015.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import agu.thesis2015.domain.User;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.TokenTranfer;

public interface UserService {

	Response getAll();

	Response getById(String id);

	Response insert(User user, MultipartFile avartar, HttpServletRequest request);

	Response paging(int page, int size, String sort, String field);

	Response update(User user);

	Response changePass(String username, String oldPass, String newPass);

//	Response deleteAll();

	Response delete(List<String> ids, HttpServletRequest request);

	Response deleteOne(String username, HttpServletRequest request);

	Response statistics(String username);

	Response getUser();
	
	User getFullUser(String username);

	TokenTranfer authenticate(String username, String password);
	
	Response active(String username, String activeToken);

}
