/**
 * @author: nthienan
 * @created: Mar 23, 2015
 */

package agu.thesis2015.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import agu.thesis2015.domain.Song;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.SongProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.RequestData.Direction;
import agu.thesis2015.service.SongService;

@Service
public class SongServiceImpl implements SongService{
	@Autowired
	private SongProducer producer;

	@Override
	public Response getAll(String username) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_ALL, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response getById(String username, String id) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(id);
		Message message = new Message(MessageMethod.GET, MessageAction.GET_BY_ID, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response insert(String username, Song song) {
		song.setUsername(username);
		song.setId(UUID.randomUUID().toString());
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, song.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response update(String username, Song song) {
		song.setUsername(username);
		Message message = new Message(MessageMethod.PUT, MessageAction.UPDATE, song.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response deleteAll(String username, HttpServletRequest request) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE_ALL, requestData.toJson());
		Response response;
		try {
			deleteMusicFile(getSongIds(username), request);
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response deleteOne(String username, String id, HttpServletRequest request) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(id);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		List<String> list = new ArrayList<String>();
		list.add(id);
		Response response;
		try {
			deleteMusicFile(list, request);
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response deleteMulti(String username, List<String> ids, HttpServletRequest request) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.setListId(ids);
		Message message = new Message(MessageMethod.DELETE, MessageAction.DELETE, requestData.toJson());
		Response response;
		try {
			deleteMusicFile(ids, request);
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response search(String username, String keyword, int page, int size, String sort, String field) {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(username, page, size, direction, field, keyword);

		Message message = new Message(MessageMethod.GET, MessageAction.SEARCH, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response upload(String username, String name, String gener, String artist, String musician, boolean shared, MultipartFile file, HttpServletRequest request) {
		Song song = new Song(name, gener, artist, musician);
		song.setUsername(username);
		song.setShared(shared);
		song.setId(UUID.randomUUID().toString());
		String path = "";
		try {
			path = saveFile(file, song.getId(), request);
		} catch (IOException e) {
		}
		song.setPath(path);
		Message message = new Message(MessageMethod.POST, MessageAction.INSERT, song.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public void downloadFile(String songId, HttpServletRequest request, HttpServletResponse response) {
		RequestData requestData = new RequestData();
		requestData.addId(songId);
		Message message = new Message(MessageMethod.GET, MessageAction.DOWNLOAD, requestData.toJson());
		Response responseData = null;
		try {
			responseData = producer.sendAndReceive(message);
		} catch (Exception e1) {
		}
		Song song = Song.fromJson(responseData.getResponse().toString());
		String songName = song.getName().replace(" ", "_") + "-" + song.getArtist().replace(" ", "_") + ".mp3";

		String fileName = request.getSession().getServletContext().getRealPath("/music") + File.separator + songId + ".mp3";
		File file = new File(fileName);
		response.setContentType(" multipart/form-data");
		response.setContentLength(new Long(file.length()).intValue());
		response.setHeader("Content-Disposition", "attachment; filename=" + songName);
		try {
			FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
		} catch (IOException e) {
		}
	}

	@Override
	public Response paging(String username, int page, int size, String sort, String field) {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(username, page, size, direction, field);

		Message message = new Message(MessageMethod.GET, MessageAction.PAGING, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response updateView(String songId) {
		RequestData requestData = new RequestData();
		requestData.addId(songId);

		Message message = new Message(MessageMethod.PUT, MessageAction.VIEW, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response updateDownload(String songId) {
		RequestData requestData = new RequestData();
		requestData.addId(songId);

		Message message = new Message(MessageMethod.PUT, MessageAction.DOWNLOAD, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response shared(String username, String songId) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(songId);

		Message message = new Message(MessageMethod.PUT, MessageAction.SHARE, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response unShared(String username, String songId) {
		RequestData requestData = new RequestData();
		requestData.setUsername(username);
		requestData.addId(songId);

		Message message = new Message(MessageMethod.PUT, MessageAction.UNSHARE, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
	}

	@Override
	public Response getShare(int page, int size, String sort, String field) {
		Direction direction = sort.equalsIgnoreCase("DESC") ? Direction.DESC : Direction.ASC;
		RequestData requestData = new RequestData(null, page, size, direction, field);

		Message message = new Message(MessageMethod.GET, MessageAction.SHARE, requestData.toJson());
		Response response;
		try {
			response = producer.sendAndReceive(message);
		} catch (Exception e) {
			return null;
		}
		return response;
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
}
