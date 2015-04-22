/**
 * @author ltduoc
 */
package agu.thesis2015.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import agu.thesis2015.domain.Song;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.repo.SongRepo;
import agu.thesis2015.service.SongDao;

/**
 * 
 * @author ltduoc
 *
 */
@Service(value = "songDao")
public class SongDaoImpl implements SongDao {

	@Autowired
	private SongRepo songRepo;

	private Response getResponse(ResponseStatus status, int statuscode, String message, Object response) {
		return new Response(status, statuscode, message, response);
	}

	@Override
	public String songProcessor(String context) {
		Message message = Message.fromJson(context);
		MessageAction messageAction = message.getAction();
		RequestData request = RequestData.fromJson(message.getData().toString());

		String username = "";
		String id = "";
		List<String> listId = null;
		String field = "";
		int page = 0;
		int size = 0;
		String keyword = "";
		agu.thesis2015.model.RequestData.Direction direction = null;
		Response response = null;

		switch (message.getMethod()) {
		case GET: {
			if (messageAction.equals(MessageAction.GET_ALL)) {
				username = request.getUsername();

				response = getAll(username);
			} else if (messageAction.equals(MessageAction.GET_BY_ID)) {
				username = request.getUsername();
				id = request.getListId().get(0);

				response = getById(username, id);
			} else if (messageAction.equals(MessageAction.PAGING)) {
				username = request.getUsername();
				field = request.getField();
				page = request.getPage();
				size = request.getSize();
				direction = request.getDirection();

				response = paging(username, field, page, size, direction);
			} else if (messageAction.equals(MessageAction.SEARCH)) {
				username = request.getUsername();
				keyword = request.getKeyword();
				field = request.getField();
				page = request.getPage();
				size = request.getSize();
				direction = request.getDirection();

				response = search(username, keyword, field, page, size, direction);
			} else if (messageAction.equals(MessageAction.DOWNLOAD)) {
				id = request.getListId().get(0);

				response = getSongDownLoad(id);
			} else if (messageAction.equals(MessageAction.SHARE)) {
				field = request.getField();
				page = request.getPage();
				size = request.getSize();
				direction = request.getDirection();
				response = getListSongShard(field, page, size, direction);
			}
			break;
		}
		case POST: {
			if (messageAction.equals(MessageAction.INSERT)) {
				Song newSong = Song.fromJson(String.valueOf(message.getData()));
				response = insert(newSong);
			}
			break;
		}
		case PUT: {
			if (messageAction.equals(MessageAction.UPDATE)) {
				Song newSong = Song.fromJson(String.valueOf(message.getData()));
				response = update(newSong);
			} else if (messageAction.equals(MessageAction.VIEW)) {
				id = request.getListId().get(0);

				response = countView(id);
			} else if (messageAction.equals(MessageAction.DOWNLOAD)) {
				id = request.getListId().get(0);

				response = countDownLoad(id);
			} else if (messageAction.equals(MessageAction.SHARE)) {
				username = request.getUsername();
				id = request.getListId().get(0);

				response = changeStateSong(username, id, true);
			} else if (messageAction.equals(MessageAction.UNSHARE)) {
				username = request.getUsername();
				id = request.getListId().get(0);

				response = changeStateSong(username, id, false);
			}
			break;
		}
		case DELETE: {
			if (messageAction.equals(MessageAction.DELETE_ALL)) {
				username = request.getUsername();

				response = deleteAll(username);
			} else if (messageAction.equals(MessageAction.DELETE)) {
				username = request.getUsername();
				listId = request.getListId();

				response = delete(username, listId);
			}
			break;
		}
		default:
			break;
		}
		return response.toJson();
	}

	@Override
	public Response insert(Song newSong) {
		try {
			String name = newSong.getId();
			if (name != null && songRepo.exists(name)) {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "This song is exsits", null);
			} else {
				Song s = songRepo.save(newSong);
				s.setLastUpdate(new Date());
				songRepo.save(s);
				return getResponse(ResponseStatus.OK, 200, "Insert success", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response update(Song newSong) {
		try {
			Song song = songRepo.findByUsernameAndId(newSong.getUsername(), newSong.getId());
			if (song != null) {
				Song s = songRepo.save(newSong);
				s.setLastUpdate(new Date());
				songRepo.save(s);
				return getResponse(ResponseStatus.OK, 200, "Update success", null);
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "This song isn't exsits", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response delete(String username, List<String> listId) {
		try {
			if (listId.size() > 0) {
				for (String id : listId) {
					Song song = songRepo.findByUsernameAndId(username, id);
					if (song != null)
						songRepo.delete(id);
				}
				return getResponse(ResponseStatus.OK, 200, "Delete success", null);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Delete error", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response deleteAll(String username) {
		try {
			List<Song> findByUsername = songRepo.findByUsername(username);
			if (findByUsername.size() > 0) {
				for (Song song : findByUsername) {
					songRepo.delete(song);
				}
				return getResponse(ResponseStatus.OK, 200, "Delete all success", null);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "This user has't any songs", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response getAll(String username) {
		try {
			List<Song> listSong = songRepo.findByUsername(username);
			if (listSong.size() > 0) {
				return getResponse(ResponseStatus.OK, 200, "Get list success", listSong);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Get list error", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response getById(String username, String id) {
		try {
			Song s = songRepo.findOne(id);
			if (null != s) {
				if (!s.isShared()) {
					Song song = songRepo.findByUsernameAndId(username, id);
					if (song != null) {
						return getResponse(ResponseStatus.OK, 200, "Get song success", song);
					} else {
						return getResponse(ResponseStatus.BAD_REQUEST, 400, "Id isn't exsits", song);
					}
				} else {// shared
					return getResponse(ResponseStatus.OK, 200, "Get song success", s);
				}
			}
			return getResponse(ResponseStatus.BAD_REQUEST, 400, "Id isn't exsits", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response search(String username, String keyword, String field, int page, int size, agu.thesis2015.model.RequestData.Direction direction) {
		Direction sort = direction.equals(agu.thesis2015.model.RequestData.Direction.ASC) ? Direction.ASC : Direction.DESC;
		Pageable pageRequest = new PageRequest(page, size, sort, field);
		try {
			Page<Song> list = songRepo.findAllCriteria(pageRequest, username, keyword);
			if (list.getContent().size() > 0)
				return getResponse(ResponseStatus.OK, 200, "Search success", list);
			else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Search error", list);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response paging(String username, String field, int page, int size, agu.thesis2015.model.RequestData.Direction direction) {
		try {
			Direction sort = direction.equals(agu.thesis2015.model.RequestData.Direction.ASC) ? Direction.ASC : Direction.DESC;
			Page<Song> pageList = songRepo.findByUsername(username, new PageRequest(page, size, sort, field));
			if (pageList.getContent().size() > 0) {
				return getResponse(ResponseStatus.OK, 200, "Paging list", pageList);
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, " This list is null", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response countView(String id) {
		Song song = songRepo.findOne(id);
		if (song != null) {
			song.setPath(song.getPath());
			song.setView(song.getView() + 1);
			songRepo.save(song);
			return getResponse(ResponseStatus.OK, 200, "View success", song);
		} else
			return getResponse(ResponseStatus.BAD_REQUEST, 400, "View error", null);
	}

	@Override
	public Response countDownLoad(String id) {
		Song song = songRepo.findOne(id);
		if (song != null) {
			song.setPath(song.getPath());
			song.setDownload(song.getDownload() + 1);
			songRepo.save(song);
			return getResponse(ResponseStatus.OK, 200, "Download success", song);
		} else
			return getResponse(ResponseStatus.BAD_REQUEST, 400, "Download error", null);
	}

	@Override
	public Response getSongDownLoad(String id) {
		Song song = songRepo.findOne(id);
		if (song != null) {
			return getResponse(ResponseStatus.OK, 200, "Get a song for download success", song.toJson());
		} else
			return getResponse(ResponseStatus.BAD_REQUEST, 400, "et a song for download  error", null);
	}

	@Override
	public Response getListSongShard(String field, int page, int size, agu.thesis2015.model.RequestData.Direction direction) {

		try {
			Direction sort = direction.equals(agu.thesis2015.model.RequestData.Direction.ASC) ? Direction.ASC : Direction.DESC;
			Pageable pageRequest = new PageRequest(page, size, sort, field);
			Page<Song> pageList = songRepo.findByShared(true, pageRequest);
			if (pageList.getContent().size() > 0) {
				return getResponse(ResponseStatus.OK, 200, "Get list success", pageList);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Get list error", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response changeStateSong(String username, String id, boolean state) {
		try {
			Song song = songRepo.findByUsernameAndId(username, id);
			if (song != null) {
				song.setShared(state);
				songRepo.save(song);
				if (state) {
					return getResponse(ResponseStatus.OK, 200, "Shared song success", null);
				} else {
					return getResponse(ResponseStatus.OK, 200, "Unshared song success", null);
				}
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Id isn't exsits", song);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

}
