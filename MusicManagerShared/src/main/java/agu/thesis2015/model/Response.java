package agu.thesis2015.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response {

	public enum ResponseStatus {
		OK, BAD_REQUEST, INTERNAL_SERVER_ERROR
	}

	private ResponseStatus status;
	private int statuscode;
	private String message;
	private String type;
	private Object response;

	public Response() {
	}

	public Response(ResponseStatus status, int statuscode, String message,
			Object response) {
		super();
		this.status = status;
		this.statuscode = statuscode;
		this.message = message;
		this.response = response;
		if (response != null)
			this.type = response.getClass().getSimpleName();
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
		if (response != null)
			this.type = response.getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return toJson();
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static Response fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, Response.class);
		} catch (IOException e) {
			return null;
		}
	}
}