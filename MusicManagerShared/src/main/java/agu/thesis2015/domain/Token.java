package agu.thesis2015.domain;

import java.io.IOException;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(collection = "Tokens")
public class Token {
	private String username;
	@Id
	private String series;
	private String tokenValue;
	private Date date;

	public Token() {
	}

	public Token(String username, String series, String tokenValue, Date date) {
		this.username = username;
		this.series = series;
		this.tokenValue = tokenValue;
		this.date = date;
	}

	public Token(PersistentRememberMeToken p) {
		this.username = p.getUsername();
		this.series = p.getSeries();
		this.tokenValue = p.getTokenValue();
		this.date = p.getDate();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static Token fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, Token.class);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}
