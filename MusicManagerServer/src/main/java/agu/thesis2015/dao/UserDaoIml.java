package agu.thesis2015.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import agu.thesis2015.domain.Song;
import agu.thesis2015.domain.User;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.repo.SongRepo;
import agu.thesis2015.repo.UserRepo;

import com.google.gson.Gson;

@Service(value = "userDao")
public class UserDaoIml implements UserDao {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private SongRepo songRepo;

	private Md5PasswordEncoder md5 = new Md5PasswordEncoder();

	private Response getResponse(ResponseStatus status, int statuscode, String message, Object response) {
		return new Response(status, statuscode, message, response);
	}

	@Override
	public String userProcessor(String context) {
		String response = null;
		Message message = Message.fromJson(context);
		RequestData request = null;
		if (message.getData() != null)
			request = RequestData.fromJson(message.getData().toString());
		switch (message.getMethod()) {
		case GET: {
			if (message.getAction().equals(MessageAction.GET_ALL)) {
				response = getAll().toJson();
			} else if (message.getAction().equals(MessageAction.GET_BY_ID)) {
				response = getByUserName(request.getListId().get(0)).toJson();
			} else if (message.getAction().equals(MessageAction.SECURITY)) {
				response = getUserNameSecurity(request.getListId().get(0)).toJson();
			} else if (message.getAction().equals(MessageAction.PAGING)) {
				response = paging(request.getField(), request.getPage(), request.getSize(), request.getDirection()).toJson();
			} else if (message.getAction().equals(MessageAction.SEARCH)) {
				response = search(request.getKeyword(), request.getField(), request.getPage(), request.getSize(), request.getDirection()).toJson();
			} else if (message.getAction().equals(MessageAction.SECURITY)) {
				response = changePassword(request.getUsername(), request.getListId().get(0), request.getListId().get(1)).toJson();
			} else if (message.getAction().equals(MessageAction.ORTHER)) {
				response = getInfoListSongs(request.getUsername()).toJson();
			}
			break;
		}
		case POST: {
			if (message.getAction().equals(MessageAction.INSERT)) {

				User newUser = User.fromJson(String.valueOf(message.getData()));
				response = insert(newUser).toJson();
			}
			break;

		}
		case PUT: {
			if (message.getAction().equals(MessageAction.UPDATE)) {
				User newUser = User.fromJson(String.valueOf(message.getData()));
				response = update(newUser).toJson();
			} else if (message.getAction().equals(MessageAction.SECURITY)) {
				response = changePassword(request.getUsername(), request.getListId().get(0), request.getListId().get(1)).toJson();
			}
			break;
		}
		case DELETE: {
			if (message.getAction().equals(MessageAction.DELETE_ALL)) {
				response = deleteAll().toJson();
			} else if (message.getAction().equals(MessageAction.DELETE)) {

				response = delete(request.getListId()).toJson();
			} else if (message.getAction().equals(MessageAction.ORTHER)) {
				response = getListSongId(request.getUsername()).toJson();
			}
			break;
		}
		default:
			break;
		}
		return response;
	}

	@Override
	public Response insert(User newUser) {
		try {
			String name = newUser.getUsername();
			if (name != null && userRepo.exists(name)) {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "The user is exsits", null);
			} else {
				newUser.setPassword(md5.encodePassword(newUser.getPassword(), null));
				userRepo.save(newUser);
				return getResponse(ResponseStatus.OK, 200, "Insert success", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response update(User newUser) {
		try {
			String name = newUser.getUsername();
			if (name != null && userRepo.exists(name)) {
				User user = userRepo.findOne(name);
				newUser.setPassword(user.getPassword());
				userRepo.save(newUser);
				return getResponse(ResponseStatus.OK, 200, "Update success", null);

			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "The user isn't exsits", null);
			}
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response delete(List<String> listUsername) {
		try {
			if (listUsername.size() > 0) {
				for (String username : listUsername) {
					userRepo.delete(username);
					List<Song> findAll = songRepo.findByUsername(username);
					for (Song song : findAll) {
						songRepo.delete(song);
					}
				}
				return getResponse(ResponseStatus.OK, 200, "Delete success", null);
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Delete error", null);
			}

		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response deleteAll() {
		songRepo.deleteAll();
		userRepo.deleteAll();
		return getResponse(ResponseStatus.OK, 200, "Delete all success", null);
	}

	@Override
	public Response getAll() {
		try {
			List<User> listUser = userRepo.findAll();
			if (listUser.size() > 0) {
				for (User user : listUser) {
					user.setPassword(null);
				}
				return getResponse(ResponseStatus.OK, 200, "Get list success", listUser);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Get list error", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response getByUserName(String username) {
		try {
			User user = userRepo.findOne(username);
			if (user != null) {
				user.setPassword(null);
				return getResponse(ResponseStatus.OK, 200, "Get user success", user);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "User isn't exsist", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response getUserNameSecurity(String username) {
		try {
			User user = userRepo.findOne(username);
			if (user != null) {
				return getResponse(ResponseStatus.OK, 200, "Get user success", user.toJson());
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "User isn't exsist", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response search(String keyword, String field, int page, int size, agu.thesis2015.model.RequestData.Direction direction) {
		Direction sort = direction.equals(agu.thesis2015.model.RequestData.Direction.ASC) ? Direction.ASC : Direction.DESC;

		Pageable pageRequest = new PageRequest(page, size, sort, field);
		try {
			Page<User> list = userRepo.search(pageRequest, keyword);
			if (list.getContent().size() > 0) {
				for (User user : list) {
					user.setPassword(null);
				}
				return getResponse(ResponseStatus.OK, 200, "Search success", list);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Search error", list);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response paging(String field, int page, int size, agu.thesis2015.model.RequestData.Direction direction) {
		try {
			Direction sort = direction.equals(agu.thesis2015.model.RequestData.Direction.ASC) ? Direction.ASC : Direction.DESC;
			Page<User> pageList = userRepo.findAll(new PageRequest(page, size, sort, field));

			if (pageList.getContent().size() > 0) {
				for (User user : pageList) {
					user.setPassword(null);
				}
				return getResponse(ResponseStatus.OK, 200, "Paging list", pageList);
			} else {
				return getResponse(ResponseStatus.BAD_REQUEST, 400, " This list is null", null);
			}

		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response getInfoListSongs(String username) {
		try {
			List<Song> listDownLoadDesc = songRepo.findByUsernameOrderByDownloadDesc(username);
			List<Song> listViewDesc = songRepo.findByUsernameOrderByViewDesc(username);

			Map<String, String> map = new HashMap<String, String>();

			List<Song> list = songRepo.findByUsername(username);
			String totalSongs = String.valueOf(list.size());
			String totalViewOfSongs = String.valueOf(getTotalViewOfSongs(list));
			String totalDownLoadOfSongs = String.valueOf(getTotalDownLoadOfSongs(list));
			String songNameMaxView = listViewDesc.get(0).getName();
			String songNameMaxDownLoad = listDownLoadDesc.get(0).getName();
			String songIdMaxView = listViewDesc.get(0).getId();
			String songIdMaxDownLoad = listDownLoadDesc.get(0).getId();

			if (list.size() > 0) {
				map.put("totalSongs", totalSongs);
				map.put("totalViewOfSongs", totalViewOfSongs);
				map.put("totalDownLoadOfSongs", totalDownLoadOfSongs);
				map.put("songNameMaxView", songNameMaxView);
				map.put("songNameMaxDownLoad", songNameMaxDownLoad);
				map.put("songIdMaxView", songIdMaxView);
				map.put("songIdMaxDownLoad", songIdMaxDownLoad);
				return getResponse(ResponseStatus.OK, 200, " Get succes info", map);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "List song null", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}

	}

	private int getTotalViewOfSongs(List<Song> list) {
		int toTal = 0;
		for (Song song : list) {
			toTal += song.getView();
		}
		return toTal;
	}

	private int getTotalDownLoadOfSongs(List<Song> list) {
		int toTal = 0;
		for (Song song : list) {
			toTal += song.getDownload();
		}
		return toTal;
	}

	@Override
	public Response changePassword(String username, String oldPass, String newPass) {
		try {
			User user = userRepo.checkPassWord(username, oldPass);

			if (user != null) {
				user.setPassword(newPass);
				userRepo.save(user);
				return getResponse(ResponseStatus.OK, 200, " Change success", null);
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, "Old password is incorrect", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

	@Override
	public Response getListSongId(String username) {
		try {

			List<String> listId = new ArrayList<String>();
			List<Song> findAll = songRepo.findByUsername(username);
			if (findAll.size() > 0) {
				for (Song song : findAll) {
					listId.add(song.getId());
				}
				return getResponse(ResponseStatus.OK, 200, " Get list path song success", new Gson().toJson(listId));
			} else
				return getResponse(ResponseStatus.BAD_REQUEST, 400, " Get list path song error", null);
		} catch (Exception e) {
			return getResponse(ResponseStatus.INTERNAL_SERVER_ERROR, 500, e.getMessage(), null);
		}
	}

}
