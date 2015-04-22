package agu.thesis2015.service;

import java.util.List;

import agu.thesis2015.domain.User;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.RequestData.Direction;

/**
 * 
 * @author ltduoc
 *
 */
public interface UserDao {
	/**
	 * Insert a new user
	 * 
	 * @param newUser
	 * @return {@link Response}
	 */
	public Response insert(User newUser);

	/**
	 * Update a user
	 * 
	 * @param newUser
	 * @return {@link Response}
	 */
	public Response update(User newUser);

	/**
	 * Delete users
	 * 
	 * @param listUsername
	 * @return {@link Response}
	 */

	public Response delete(List<String> listUsername);

	/**
	 * Delete all songs
	 * 
	 * @return {@link Response}
	 */

	public Response deleteAll();

	/**
	 * Get all songs
	 * 
	 * @return {@link Response}
	 */

	public Response getAll();

	/**
	 * Get a user by id
	 * 
	 * @param username
	 * @return {@link Response}
	 */

	public Response getByUserName(String username);

	/**
	 * Get a user for client security
	 * 
	 * @param username
	 * @return
	 */

	public Response getUserNameSecurity(String username);

	/**
	 * Searching users
	 * 
	 * @param keyword
	 * @param field
	 * @param page
	 * @param size
	 * @param direction
	 * @return
	 */

	public Response search(String keyword, String field, int page, int size, Direction direction);

	/**
	 * Paging a list users
	 * 
	 * @param field
	 * @param page
	 * @param size
	 * @param direction
	 * @return
	 */
	public Response paging(String field, int page, int size, Direction direction);

	/**
	 * Get information of songs
	 * 
	 * @param username
	 * @return
	 */
	public Response getInfoListSongs(String username);

	/**
	 * Change password of user
	 * 
	 * @param username
	 * @param oldPass
	 * @param newPass
	 * @return
	 */
	public Response changePassword(String username, String oldPass, String newPass);
	
	/**
	 * Get list song with specific id 
	 * @param username
	 * @return
	 */

	public Response getListSongId(String username);

	/**
	 * The processor switch method for the message reply
	 * 
	 * @param context
	 * @return {@link Response}
	 */
	public String userProcessor(String context);

	public Response activeUser(String username);

	public Response getUserWithPass(String username);
}
