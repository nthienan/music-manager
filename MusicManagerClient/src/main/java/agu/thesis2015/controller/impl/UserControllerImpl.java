/**
 * @author: nthienan
 */

package agu.thesis2015.controller.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import agu.thesis2015.controller.UserController;
import agu.thesis2015.domain.User;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.TokenTranfer;
import agu.thesis2015.service.UserService;

@RestController
@RequestMapping(value = "/api/user")
public class UserControllerImpl implements UserController {
	@Autowired
	private UserService service;

	@Override
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public Response getAll() {
		return service.getAll();
	}

	// get by id
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Response getById(@PathVariable String id) {
		return service.getById(id);
	}

	// insert
	@Override
	@RequestMapping(value = "/regis", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response insert(@RequestBody User user) {
		return service.insert(user);
	}

	// paging
	@Override
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public Response paging(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field) {
		return service.paging(page, size, sort, field);
	}

	// update but not pass
	@Override
	@RequestMapping(method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response update(@RequestBody User user) {
		return service.update(user);
	}

	// change pass
	@Override
	@RequestMapping(value = "/{username}/pass", method = RequestMethod.PUT, produces = "application/json")
	public Response changePass(@PathVariable String username, @RequestParam(defaultValue = "") String oldPass, @RequestParam(defaultValue = "") String newPass) {
		return service.changePass(username, oldPass, newPass);
	}

	// delete all
	@Override
	@RequestMapping(value = "/all", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteAll() {
		return service.deleteAll();
	}

	// delete
	@Override
	@RequestMapping(method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
	public Response delete(@RequestBody List<String> ids, HttpServletRequest request) {
		return service.delete(ids, request);
	}

	// delete one
	@Override
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteOne(@PathVariable String username, HttpServletRequest request) {
		return service.deleteOne(username, request);
	}

	// statistics
	@Override
	@RequestMapping(value = "/{username}/statistics", method = RequestMethod.GET, produces = "application/json")
	public Response statistics(@PathVariable String username) {
		return service.statistics(username);
	}

	// security
	@Override
	@RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
	public Response getUser() {
		return service.getUser();
	}

	@Override
	@RequestMapping(value = "/authenticate/{username}/{password}", method = RequestMethod.POST, produces = "application/json")
	public TokenTranfer authenticate(@PathVariable String username, @PathVariable String password) {
		return service.authenticate(username, password);
	}
}