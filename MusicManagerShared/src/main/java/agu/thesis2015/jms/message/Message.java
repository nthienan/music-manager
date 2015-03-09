package agu.thesis2015.jms.message;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {

	public enum MessageMethod {
		GET, POST, PUT, DELETE
	}

	public enum MessageAction {
		INSERT, UPDATE, DELETE, DELETE_ALL, GET_ALL, GET_BY_ID, SEARCH, PAGING, VIEW, DOWNLOAD, SECURITY, ORTHER
	}

	private MessageMethod method;
	private MessageAction action;
	private Object data;

	public Message() {
		super();
	}

	public Message(MessageMethod method, MessageAction action, Object data) {
		super();
		this.method = method;
		this.action = action;
		this.data = data;
	}

	public MessageMethod getMethod() {
		return method;
	}

	public void setMethod(MessageMethod method) {
		this.method = method;
	}

	public MessageAction getAction() {
		return action;
	}

	public void setAction(MessageAction action) {
		this.action = action;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
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

	public static Message fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, Message.class);
		} catch (IOException e) {
			return null;
		}
	}
}
