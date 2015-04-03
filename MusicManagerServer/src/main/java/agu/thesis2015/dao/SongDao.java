package agu.thesis2015.dao;

import java.util.List;

import agu.thesis2015.domain.Song;
import agu.thesis2015.model.RequestData.Direction;
import agu.thesis2015.model.Response;

/**
 * 
 * @author ltduoc
 *
 */
public interface SongDao {
	/**
	 * Insert a new song
	 * 
	 * @param newSong
	 * @return {@link Response}
	 */
	public Response insert(Song newSong);

	/**
	 * Update a song
	 * 
	 * @param newSong
	 * @return {@link Response}
	 */
	public Response update(Song newSong);

	/**
	 * Delete songs
	 * 
	 * @param username
	 * @param listId
	 * @return {@link Response}
	 */

	public Response delete(String username, List<String> listId);

	/**
	 * Delete all songs
	 * 
	 * @param username
	 * @return {@link Response}
	 */

	public Response deleteAll(String username);

	/**
	 * Get all songs
	 * 
	 * @param username
	 * @return {@link Response}
	 */

	public Response getAll(String username);

	/**
	 * Get a song by id
	 * 
	 * @param username
	 * @param id
	 * @return {@link Response}
	 */

	public Response getById(String username, String id);

	/**
	 * Down load a song
	 * 
	 * @param id
	 * @return
	 */
	public Response getSongDownLoad(String id);

	/**
	 * Searching songs
	 * 
	 * @param username
	 * @param keyword
	 * @param field
	 * @param page
	 * @param size
	 * @param direction
	 * @return {@link Response}
	 */
	public Response search(String username, String keyword, String field,
			int page, int size, Direction direction);

	/**
	 * Paging a list songs
	 * 
	 * @param username
	 * @param field
	 * @param page
	 * @param size
	 * @param direction
	 * @return {@link Response}
	 */

	public Response paging(String username, String field, int page, int size,
			Direction direction);

	/**
	 * The processor switch method for the message reply
	 * 
	 * @param context
	 * @return {@link Response}
	 */

	public String songProcessor(String context);

	/**
	 * Count and increase the filed view
	 * 
	 * @param username
	 * @param id
	 * @return {@link Response}
	 */

	public Response countView(String id);

	/**
	 * Count and increase the filed download
	 * 
	 * @param username
	 * @param id
	 * @return {@link Response}
	 */
	public Response countDownLoad(String id);

	public Response getListSongShard(String field, int page, int size,
			Direction direction);

	public Response changeStateSong(String username, String id, boolean state);

	public Response fullTextSearch(String username, String keyword, int page,
			int size);

}
