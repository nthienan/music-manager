/**
 * @author: nthienan
 */

package agu.thesis2015.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import agu.thesis2015.domain.User;

public class UserDetail implements UserDetails {

	private static final long serialVersionUID = -7837718173030440299L;
	private User user;

	public UserDetail() {
		super();
	}

	public UserDetail(User user) {
		super();
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : user.getRoles()) {
			SimpleGrantedAuthority r = new SimpleGrantedAuthority(role);
			authorities.add(r);
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static UserDetail fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, UserDetail.class);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}