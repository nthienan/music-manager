package agu.thesis2015.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenTranfer {
	private String token;

	public TokenTranfer() {
	}

	public TokenTranfer(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static TokenTranfer fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, TokenTranfer.class);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}
