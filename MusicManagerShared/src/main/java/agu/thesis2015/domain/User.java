/**
 * @author: nthienan
 */

package agu.thesis2015.domain;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(collection = "Users")
public class User implements Serializable {

	private static final long serialVersionUID = 6590476795584596333L;
	
	@Id
	private String username;
	private String password;
	private Set<String> roles;
	private String fullName;
	private String image;
	private String email;
	private boolean active;

	public User() {
	}

	public User(String username, String password, Set<String> roles, String fullName, String email) {
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.fullName = fullName;
		this.email = email;
	}

	public User(String username, String password, String fullName) {
		super();
		this.username = username;
		this.password = password;
		this.fullName = fullName;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void addRole(String roleName){
		if(this.roles == null)
			roles = new HashSet<String>();
		this.roles.add(roleName);
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static User fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, User.class);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}