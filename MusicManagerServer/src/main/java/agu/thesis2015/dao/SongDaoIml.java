package agu.thesis2015.dao;

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

/**
 * 
 * @author ltduoc
 *
 */
@Service(value = "songDao")
public class SongDaoIml implements SongDao {

	@Autowired
	private SongRepo songRepo;

	private Response getResponse(ResponseStatus status, int statuscode, String message, Object response) {
		return new Response(status, statuscode, message, response);
	}

	@Override
	public String songProcessor(String context) {
		String response = null;
		Message message = Message.fromJson(context);
		RequestData request = RequestData.fromJson(message.getData().toString());
		switch (message.getMethod()) {
		case GET: {
			if (message.getAction().equals(MessageAction.GET_ALL)) {
				response = getAll(request.getUsername()).toJson();
			} else if (message.getAction().equals(MessageAction.GET_BY_ID)) {
				response = getById(request.getUsername(), request.getListId().get(0)).toJson();
			} else if (message.getAction().equals(MessageAction.PAGING)) {
				response = paging(request.getUsername(), request.getField(), request.getPage(), request.getSize(), request.getDirection()).toJson();
			} else if (message.getAction().equals(MessageAction.SEARCH)) {
				response = search(request.getUsername(), request.getKeyword(), request.getField(), request.getPage(), request.getSize(), request.getDirection())
						.toJson();
			} else if (message.getAction().equals(MessageAction.DOWNLOAD)) {
				response = getSongDownLoad(request.getListId().get(0)).toJson();
			}
			break;
		}
		case POST: {
			if (message.getAction().equals(MessageAction.INSERT)) {

				Song newSong = Song.fromJson(String.valueOf(message.getData()));
				response = insert(newSong).toJson();
			}
			break;
		}
		case PUT: {
			if (message.getAction().equals(MessageAction.UPDATE)) {
				Song newSong = Song.fromJson(String.valueOf(message.getData()));
				response = update(newSong).toJson();
			} else if (message.getAction().equals(MessageAction.VIEW)) {
				response = countView(request.getUsername(), request.getListId().get(0)).toJson();
			} else if (message.getAction().equals(MessageAction.DOWNLOAD)) {
				response = countDownLoad(request.getUsername(), request.getListId().get(0)).toJson();
			}
			break;
		}
		case DELETE: {
			if (message.getAction().equals(MessageAction.DELETE_ALL)) {
				response = deleteAll(request.getUsername()).toJson();

			} else if (message.getAction().equals(MessageAction.DELETE)) {
				response = delete(request.getUsername(), request.getListId()).toJson();
			}
			break;
		}
		default:
			break;
		}
		return response;
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
			Song song = songRepo.findByUsernameAndId(username, id);
			if (song != null) {
				return getResponse(ResponseStatus.OK, 200, "Get song success", song);
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Id isn't exsits", song);
			}
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
	public Response countView(String username, String id) {
		Song song = songRepo.findByUsernameAndId(username, id);
		if (song != null) {
			song.setPath(song.getPath());
			song.setView(song.getView() + 1);
			songRepo.save(song);
			return getResponse(ResponseStatus.OK, 200, "View success", song);
		} else
			return getResponse(ResponseStatus.BAD_REQUEST, 400, "View error", null);
	}

	@Override
	public Response countDownLoad(String username, String id) {
		Song song = songRepo.findByUsernameAndId(username, id);
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

}
