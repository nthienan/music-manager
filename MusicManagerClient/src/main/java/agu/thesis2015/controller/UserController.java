/**
 * @author: nthienan
 * @created: Mar 23, 2015
 */

package agu.thesis2015.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import agu.thesis2015.domain.User;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.TokenTranfer;

public interface UserController {
	Response getAll();
	Response getById(String id);
	Response insert(String username, String password, String role, String fullName, String email, MultipartFile avatar, HttpServletRequest request);
	Response paging(int page, int size, String sort, String field);
	Response update(User user);
	Response changePass(String username, String oldPass, String newPass);
	Response delete(List<String> ids, HttpServletRequest request);
	Response deleteOne(String username, HttpServletRequest request);
	Response statistics(String username);
	Response getUser();
	TokenTranfer authenticate(String username, String password);
	Response active(String username, String activeToken);
}
