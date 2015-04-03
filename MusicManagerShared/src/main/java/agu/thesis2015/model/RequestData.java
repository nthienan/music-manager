/**
 * @author: nthienan
 */

package agu.thesis2015.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestData {
	public enum Direction {
		ASC, DESC
	}

	private String username;
	private int page;
	private int size;
	private Direction direction;
	private String field;
	private String keyword;
	private List<String> listId;

	public RequestData() {
		super();
	}

	public RequestData(String username, int page, int size,
			Direction direction, String field) {
		super();
		this.username = username;
		this.page = page;
		this.size = size;
		this.direction = direction;
		this.field = field;
	}

	public RequestData(String username, int page, int size,
			Direction direction, String field, String keyword) {
		super();
		this.username = username;
		this.page = page;
		this.size = size;
		this.direction = direction;
		this.field = field;
		this.keyword = keyword;
	}

	public RequestData(String username, int page, int size,
			Direction direction, String field, String keyword,
			List<String> songIds) {
		super();
		this.username = username;
		this.page = page;
		this.size = size;
		this.direction = direction;
		this.field = field;
		this.keyword = keyword;
		this.listId = songIds;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<String> getListId() {
		return listId;
	}

	public void setListId(List<String> listId) {
		this.listId = listId;
	}

	public void addId(String id) {
		if (listId == null)
			listId = new ArrayList<String>();
		listId.add(id);
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static RequestData fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, RequestData.class);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}
