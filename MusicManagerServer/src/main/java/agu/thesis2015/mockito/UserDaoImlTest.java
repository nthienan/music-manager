package agu.thesis2015.mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import agu.thesis2015.dao.UserDaoIml;
import agu.thesis2015.domain.Song;
import agu.thesis2015.domain.User;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.Response.ResponseStatus;
import agu.thesis2015.repo.SongRepo;
import agu.thesis2015.repo.UserRepo;

import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoImlTest {

	@Mock
	private UserRepo userRepo;

	@Mock
	private SongRepo songRepo;

	@Mock
	private User user;

	@Mock
	private Song song;

	@Mock
	private List<Song> listSong;

	@Mock
	private List<User> listUser;

	@InjectMocks
	private UserDaoIml userDaoIml;

	@Before
	public void setUp() throws Exception {
		Mockito.reset(user);
		Mockito.reset(song);
		MockitoAnnotations.initMocks(userDaoIml);
	}

	@After
	public void tearDown() {
		song = null;
		user = null;
		userRepo = null;
		songRepo = null;
		userDaoIml = null;
	}

	@Test
	public void testInsertError() throws JSONException {
		when(user.getUsername()).thenReturn("ltduoc");
		when(userRepo.exists("ltduoc")).thenReturn(true);
		Response respo = new Response(ResponseStatus.BAD_REQUEST, 400, "The user is exsits", null);

		Response insert = userDaoIml.insert(user);

		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(insert), false);
		verify(userRepo).exists(user.getUsername());
	}

	@Test
	public void testInsertSuccess() throws JSONException {

		Response respo = new Response(ResponseStatus.OK, 200, "Insert success", null);

		Response insert = userDaoIml.insert(user);

		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(insert), false);

		verify(userRepo).save(user);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInsertException() throws JSONException {

		when(user.getUsername()).thenThrow(Exception.class);
		Response respo = new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, new Exception().getMessage(), null);

		Response insert = userDaoIml.insert(user);

		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(insert), false);
	}

	// Test Update case
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateException() throws JSONException {
		when(user.getUsername()).thenThrow(Exception.class);
		Response respo = new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, new Exception().getMessage(), null);
		Response update = userDaoIml.update(user);
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(update), false);

	}

	@Test
	public void testUpdateError() throws JSONException {

		Response respo = new Response(ResponseStatus.BAD_REQUEST, 400, "The user isn't exsits", null);
		Response update = userDaoIml.update(user);
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(update), false);

	}

	@Test
	public void testUpdateSuccess() throws JSONException {
		when(user.getUsername()).thenReturn("ltduoc");
		when(userRepo.exists("ltduoc")).thenReturn(true);
		when(userRepo.findOne("ltduoc")).thenReturn(new User("ltudoc", "123", null, "Le Thanh Duoc"));
		Response respo = new Response(ResponseStatus.OK, 200, "Update success", null);
		Response update = userDaoIml.update(user);
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(update), false);
		verify(userRepo).save(user);
		verify(userRepo).exists(user.getUsername());
		verify(userRepo).findOne(user.getUsername());
	}

	// Test Delete
	@Test
	public void testDeleteSuccess() throws JSONException {
		List<String> ids = new ArrayList<String>();
		ids.add("ltduoc");
		List<Song> songs = new ArrayList<Song>();
		songs.add(song);
		when(songRepo.findByUsername("ltduoc")).thenReturn(songs);
		Response respo = new Response(ResponseStatus.OK, 200, "Delete success", null);
		Response delete = userDaoIml.delete(ids);
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(delete), false);
		verify(userRepo).delete("ltduoc");
		verify(songRepo).findByUsername("ltduoc");
		verify(songRepo).delete(song);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void TestDeleteException() throws JSONException {
		List<String> ids = new ArrayList<String>();
		ids.add("ltduoc");
		when(songRepo.findByUsername("ltduoc")).thenThrow(Exception.class);
		Response respo = new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, new Exception().getMessage(), null);
		Response delete = userDaoIml.delete(ids);
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(delete), false);
		verify(userRepo).delete("ltduoc");

	}

	@Test
	public void TestDeleteError() throws JSONException {
		List<String> ids = new ArrayList<String>();
		Response respo = new Response(ResponseStatus.BAD_REQUEST, 400, "Delete error", null);
		Response delete = userDaoIml.delete(ids);
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(delete), false);

	}

	@Test
	public void testDeleteAll() {
		userDaoIml.deleteAll();
		verify(songRepo).deleteAll();
		verify(userRepo).deleteAll();
	}

	// Test Get
	@SuppressWarnings("unchecked")
	@Test
	public void testgetAllRxception() throws JSONException {
		Response respo = new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, new Exception().getMessage(), null);
		when(userRepo.findAll()).thenThrow(Exception.class);
		Response all = userDaoIml.getAll();
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(all), false);
	}

	@Test
	public void testgetAllError() throws JSONException {
		when(userRepo.findAll()).thenReturn(listUser);
		Response respo = new Response(ResponseStatus.BAD_REQUEST, 400, "Get list error", null);
		Response all = userDaoIml.getAll();
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(all), false);
	}

	@Test
	public void testgetAllSuccess() throws JSONException {
		List<User> ids = new ArrayList<User>();
		ids.add(new User("ltudoc", "123", null, "ltduoc"));
		Response respo = new Response(ResponseStatus.OK, 200, "Get list success", ids);
		when(userRepo.findAll()).thenReturn(ids);
		Response all = userDaoIml.getAll();
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(all), false);
		verify(userRepo).findAll();

	}

	@Test
	public void testGetByUserNameSuccess() throws JSONException {
		User u = new User("ltudoc", "123", null, "ltduoc");
		when(userRepo.findOne("ltduoc")).thenReturn(u);
		Response respo = new Response(ResponseStatus.OK, 200, "Get user success", u);
		Response byUserName = userDaoIml.getByUserName("ltduoc");
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(byUserName), false);
		verify(userRepo).findOne("ltduoc");
	}

	@Test
	public void testGetByUserNameError() throws JSONException {
		when(user.getUsername()).thenReturn("ltduoc");
		Response respo = new Response(ResponseStatus.BAD_REQUEST, 400, "User isn't exsist", null);
		Response byUserName = userDaoIml.getByUserName("ltduoc");
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(byUserName), false);
		verify(userRepo).findOne("ltduoc");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetByUserNameException() throws JSONException {
		// when(user.getUsername()).thenReturn("ltduoc");
		when(userRepo.findOne("ltduoc")).thenThrow(Exception.class);
		Response respo = new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, new Exception().getMessage(), null);
		Response byUserName = userDaoIml.getByUserName("ltduoc");
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(byUserName), false);
		verify(userRepo).findOne("ltduoc");
	}

	@Test
	public void testSecuritySuccess() throws JSONException {
		User u = new User("ltudoc", "123", null, "ltduoc");
		when(userRepo.findOne("ltduoc")).thenReturn(u);
		Response respo = new Response(ResponseStatus.OK, 200, "Get user success", u.toJson());
		Response byUserName = userDaoIml.getUserNameSecurity("ltduoc");
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(byUserName), false);
		verify(userRepo).findOne("ltduoc");
	}

	@Test
	public void testSecurityError() throws JSONException {
		when(user.getUsername()).thenReturn("ltduoc");
		Response respo = new Response(ResponseStatus.BAD_REQUEST, 400, "User isn't exsist", null);
		Response byUserName = userDaoIml.getUserNameSecurity("ltduoc");
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(byUserName), false);
		verify(userRepo).findOne("ltduoc");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSecurityException() throws JSONException {
		when(userRepo.findOne("ltduoc")).thenThrow(Exception.class);
		Response respo = new Response(ResponseStatus.INTERNAL_SERVER_ERROR, 500, new Exception().getMessage(), null);
		Response byUserName = userDaoIml.getUserNameSecurity("ltduoc");
		JSONAssert.assertEquals(new Gson().toJson(respo), new Gson().toJson(byUserName), false);
		verify(userRepo).findOne("ltduoc");
	}

	@Test
	public void testpagingException() {
		final int page = 1;
		final int size = 10;

		userDaoIml.paging("username", page, size, agu.thesis2015.model.RequestData.Direction.ASC);
	}

	@Test
	public void testpagingSuccess() {
		int page = 1;
		int size = 20;
		List<User> listUser = new ArrayList<User>();
		listUser.add(user);
		Page<User> pageUser = new PageImpl<User>(listUser);

		when(userRepo.findAll(new PageRequest(page, size, Direction.ASC, "username"))).thenReturn(pageUser);

		userDaoIml.paging("username", page, size, agu.thesis2015.model.RequestData.Direction.ASC);

		verify(userRepo).findAll(new PageRequest(page, size, Direction.ASC, "username"));
	}

	@Test
	public void testpagingError() {
		int page = 1;
		int size = 20;
		List<User> listUser = new ArrayList<User>();
		Page<User> pageUser = new PageImpl<User>(listUser);
		when(userRepo.findAll(new PageRequest(page, size, Direction.ASC, "username"))).thenReturn(pageUser);
		userDaoIml.paging("username", page, size, agu.thesis2015.model.RequestData.Direction.ASC);
	}

	@Test
	public void testsearchException() {
		int page = 1;
		int size = 20;
		List<User> listUser = new ArrayList<User>();
		Page<User> pageUser = new PageImpl<User>(listUser);
		when(userRepo.findAll(new PageRequest(page, size, Direction.ASC, "username"))).thenReturn(pageUser);
		userDaoIml.search("key", "username", page, size, agu.thesis2015.model.RequestData.Direction.ASC);
	}

	@Test
	public void testsearchError() {
		int page = 1;
		int size = 20;
		List<User> listUser = new ArrayList<User>();
		Page<User> pageUser = new PageImpl<User>(listUser);
		when(userRepo.search(new PageRequest(page, size, Direction.ASC, "username"), "key")).thenReturn(pageUser);
		userDaoIml.search("key", "username", page, size, agu.thesis2015.model.RequestData.Direction.ASC);
	}

	@Test
	public void testsearchSuccess() {
		int page = 1;
		int size = 20;
		List<User> listUser = new ArrayList<User>();
		listUser.add(user);
		Page<User> pageUser = new PageImpl<User>(listUser);
		when(userRepo.search(new PageRequest(page, size, Direction.ASC, "username"), "key")).thenReturn(pageUser);
		userDaoIml.search("key", "username", page, size, agu.thesis2015.model.RequestData.Direction.ASC);
	}
}
