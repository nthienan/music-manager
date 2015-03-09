package agu.thesis2015.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import agu.thesis2015.domain.Song;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.SongProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.RequestData.Direction;
import agu.thesis2015.model.Response;

@RestController
@RequestMapping(value = "/api/song")
public class SongController {
	@Autowired
	private SongProducer producer;

	// get all
	@RequestMapping(value = "/{username}/all", method = RequestMethod.GET, produces = "application/json")
	public Response getAll(@PathVariable String username) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_ALL, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// get by id
	@RequestMapping(value = "/{username}/{id}", method = RequestMethod.GET, produces = "application/json")
	public Response getById(@PathVariable String username, @PathVariable String id) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(id);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_BY_ID, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// insert
	@RequestMapping(value = "/{username}", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response insert(@PathVariable String username, @RequestBody Song song) throws Exception {
		song.setUsername(username);
		song.setId(UUID.randomUUID().toString());
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, song.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// update
	@RequestMapping(value = "/{username}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response update(@PathVariable String username, @RequestBody Song song) throws Exception {
		song.setUsername(username);
		Message message = new Message(MessageMethod.PUT, MessageAction.UPDATE, song.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// delete all
	@RequestMapping(value = "/{username}/all", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteAll(@PathVariable String username, HttpServletRequest request) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE_ALL, requestData.toJson());
		deleteMusicFile(getSongIds(username), request);
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// delete one
	@RequestMapping(value = "/{username}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteOne(@PathVariable String username, @PathVariable String id, HttpServletRequest request) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(id);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		List<String> list = new ArrayList<String>();
		list.add(id);
		deleteMusicFile(list, request);
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// delete multi
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
	public Response deleteMulti(@PathVariable String username, @RequestBody List<String> ids, HttpServletRequest request) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.setListId(ids);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		deleteMusicFile(ids, request);
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// paging
	@RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
	public Response paging(@PathVariable String username, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field) throws Exception {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(username, page, size, direction, field);

		Message message = new Message(MessageMethod.GET, MessageAction.PAGING, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// search
	@RequestMapping(value = "/{username}/search", method = RequestMethod.GET, produces = "application/json")
	public Response search(@PathVariable String username, @RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field)
			throws Exception {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(username, page, size, direction, field, keyword);

		Message message = new Message(MessageMethod.GET, MessageAction.SEARCH, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// upload
	@RequestMapping(value = "/{username}/upload", method = RequestMethod.POST, produces = "application/json")
	public Response upload(@PathVariable String username, @RequestParam String name, @RequestParam String gener, @RequestParam String artist,
			@RequestParam String musician, @RequestParam MultipartFile file, HttpServletRequest request) throws Exception {
		Song song = new Song(name, gener, artist, musician);
		song.setUsername(username);
		song.setId(UUID.randomUUID().toString());
		String path = saveFile(file, song.getId(), request);
		song.setPath(path);
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, song.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// update view
	@RequestMapping(value = "/{username}/{songId}/view", method = RequestMethod.PUT, produces = "application/json")
	public Response updateView(@PathVariable String username, @PathVariable String songId) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(songId);

		Message message = new Message(MessageMethod.PUT, MessageAction.VIEW, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		return response;
	}

	// update download
	@RequestMapping(value = "/{username}/{songId}/download", method = RequestMethod.PUT, produces = "application/json")
	public Response updateDownload(@PathVariable String username, @PathVariable String songId) throws Exception {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(songId);

		Message message = new Message(MessageMethod.PUT, MessageAction.DOWNLOAD, requestData.toJson());
		Response responseData = producer.sendAndReceive(message);
		return responseData;
	}

	// download file
	@RequestMapping(value = "/{songId}/download", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String songId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestData requestData = new RequestData();
		requestData.addId(songId);
		Message message = new Message(MessageMethod.GET, MessageAction.DOWNLOAD, requestData.toJson());
		Response responseData = producer.sendAndReceive(message);
		Song song = Song.fromJson(responseData.getResponse().toString());
		String songName = song.getName().replace(" ", "_") + "-" + song.getArtist().replace(" ", "_") + ".mp3";

		String fileName = request.getSession().getServletContext().getRealPath("/music") + File.separator + songId + ".mp3";
		File file = new File(fileName);
		response.setContentType(" multipart/form-data");
		response.setContentLength(new Long(file.length()).intValue());
		response.setHeader("Content-Disposition", "attachment; filename=" + songName);
		FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
	}

	private String saveFile(MultipartFile file, String SongId, HttpServletRequest request) throws IOException {
		String rootPath = request.getSession().getServletContext().getRealPath("/music");
		File dir = new File(rootPath);
		if (!dir.exists())
			dir.mkdirs();

		String fileName = rootPath + File.separator + SongId + ".mp3";
		File saveFile = new File(fileName);
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveFile));
		stream.write(file.getBytes());
		stream.close();
		return "/music/" + SongId + ".mp3";
	}

	private void deleteMusicFile(List<String> ids, HttpServletRequest request) throws Exception {
		if (ids != null) {
			String realPath = request.getSession().getServletContext().getRealPath("/music");
			for (String id : ids) {
				File file = new File(realPath + File.separator + id + ".mp3");
				if (file.exists())
					file.delete();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getSongIds(String username) throws Exception {
		RequestData requestData = new RequestData(username, 0, 0, null, null);
		Message message = new Message(MessageMethod.DELETE, MessageAction.ORTHER, requestData.toJson());
		Response response = producer.sendAndReceive(message);
		if (response.getResponse() != null) {
			List<String> songIds = new Gson().fromJson(response.getResponse().toString(), List.class);
			return songIds;
		}
		return null;
	}
}