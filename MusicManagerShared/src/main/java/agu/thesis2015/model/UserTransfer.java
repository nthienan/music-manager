/**
 * @author: nthienan
 */

package agu.thesis2015.model;

import java.util.Map;

import agu.thesis2015.domain.User;

public class UserTransfer {
	private String username;

	private Map<String, Boolean> roles;

	public UserTransfer(String userName, Map<String, Boolean> roles) {
		this.username = userName;
		this.roles = roles;
	}

	public UserTransfer(User user) {
		this.username = user.getUsername();
		for (String r : user.getRoles()) {
			roles.put(r, true);
		}
	}

	public String getUsername() {
		return this.username;
	}

	public Map<String, Boolean> getRoles() {
		return this.roles;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setRoles(Map<String, Boolean> roles) {
		this.roles = roles;
	}
}