/**
 * @author: nthienan
 */

package agu.thesis2015.domain;

import java.io.IOException;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(collection = "Songs")
public class Song {
	@Id
	private String id;
	private String name;
	private String gener;
	private String artist;
	private String musician;
	private String path;
	private Date lastUpdate;
	private int download;
	private int view;
	private boolean shared;
	private String username;

	public Song() {
	}

	public Song(String name, String gener, String artist, String musician) {
		super();
		this.name = name;
		this.gener = gener;
		this.artist = artist;
		this.musician = musician;
	}

	public Song(String id, String name, String gener, String artist, String musician, String path, Date lastUpdate, int download, int view, String username) {
		this.id = id;
		this.name = name;
		this.gener = gener;
		this.artist = artist;
		this.musician = musician;
		this.path = path;
		this.lastUpdate = lastUpdate;
		this.download = download;
		this.view = view;
		this.username = username;
	}

	public Song(String id, String name, String gener, String artist, String musician, String path, Date lastUpdate, int download, int view, boolean shared,
			String username) {
		super();
		this.id = id;
		this.name = name;
		this.gener = gener;
		this.artist = artist;
		this.musician = musician;
		this.path = path;
		this.lastUpdate = lastUpdate;
		this.download = download;
		this.view = view;
		this.shared = shared;
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGener() {
		return gener;
	}

	public void setGener(String gener) {
		this.gener = gener;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getMusician() {
		return musician;
	}

	public void setMusician(String musician) {
		this.musician = musician;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String toJson() {
		try {
			return (new ObjectMapper()).writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static Song fromJson(String json) {
		try {
			return (new ObjectMapper()).readValue(json, Song.class);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return toJson();
	}
}