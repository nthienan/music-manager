package agu.thesis2015.mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import agu.thesis2015.dao.UserDaoIml;
import agu.thesis2015.domain.User;
import agu.thesis2015.repo.SongRepo;
import agu.thesis2015.repo.UserRepo;

@RunWith(MockitoJUnitRunner.class)
public class test {

	// @Mock
	private UserRepo userRepo;

	// @Mock
	private SongRepo songRepo;
	private User user;
	private UserDaoIml userDaoIml;

	@Before
	public void setUp() throws Exception {
		userRepo = mock(UserRepo.class);
		user = mock(User.class);
		
		userDaoIml = new UserDaoIml(userRepo, songRepo);
	}

	@After
	public void tearDown() {
		userRepo = null;
		songRepo = null;
		userDaoIml = null;
	}

	// @SuppressWarnings("unchecked")
	@Test
	public void testInsert() {

		when(user.getUsername()).thenReturn("thao");
		when(userRepo.exists(user.getUsername())).thenReturn(true);
		userDaoIml.update(user);
		verify(userRepo).save(user);

	}

}
