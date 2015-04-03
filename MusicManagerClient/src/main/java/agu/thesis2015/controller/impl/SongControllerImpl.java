/**
 * @author: nthienan
 */

package agu.thesis2015.controller.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import agu.thesis2015.controller.SongController;
import agu.thesis2015.domain.Song;
import agu.thesis2015.model.Response;
import agu.thesis2015.service.SongService;

@RestController
@RequestMapping(value = "/api/song")
public class SongControllerImpl implements SongController {

	@Autowired
	private SongService service;

	// get all
	@Override
	@RequestMapping(value = "/{username}/all", method = RequestMethod.GET, produces = "application/json")
	public Response getAll(@PathVariable String username) {
		return service.getAll(username);
	}

	// get by id
	@Override
	@RequestMapping(value = "/{username}/{id}", method = RequestMethod.GET, produces = "application/json")
	public Response getById(@PathVariable String username, @PathVariable String id) {
		return service.getById(username, id);
	}

	// insert
	@Override
	@RequestMapping(value = "/{username}", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response insert(@PathVariable String username, @RequestBody Song song) {
		return service.insert(username, song);
	}

	// update
	@Override
	@RequestMapping(value = "/{username}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response update(@PathVariable String username, @RequestBody Song song) {
		return service.update(username, song);
	}

	// delete all
	@Override
	@RequestMapping(value = "/{username}/all", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteAll(@PathVariable String username, HttpServletRequest request) {
		return service.deleteAll(username, request);
	}

	// delete one
	@Override
	@RequestMapping(value = "/{username}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public Response deleteOne(@PathVariable String username, @PathVariable String id, HttpServletRequest request) {
		return service.deleteOne(username, id, request);
	}

	// delete multi
	@Override
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
	public Response deleteMulti(@PathVariable String username, @RequestBody List<String> ids, HttpServletRequest request) {
		return service.deleteMulti(username, ids, request);
	}

	// paging
	@Override
	@RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
	public Response paging(@PathVariable String username, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field) {
		return service.paging(username, page, size, sort, field);
	}

	// search
	@Override
	@RequestMapping(value = "/{username}/search", method = RequestMethod.GET, produces = "application/json")
	public Response search(@PathVariable String username, @RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field) {
		return service.search(username, keyword, page, size, sort, field);
	}

	// upload
	@Override
	@RequestMapping(value = "/{username}/upload", method = RequestMethod.POST, produces = "application/json")
	public Response upload(@PathVariable String username, @RequestParam String name, @RequestParam String gener, @RequestParam String artist,
			@RequestParam String musician, @RequestParam MultipartFile file, HttpServletRequest request) {
		return service.upload(username, name, gener, artist, musician, file, request);
	}

	// update view
	@Override
	@RequestMapping(value = "/{songId}/view", method = RequestMethod.PUT, produces = "application/json")
	public Response updateView(@PathVariable String songId) {
		return service.updateView(songId);
	}

	// update download
	@Override
	@RequestMapping(value = "/{songId}/download", method = RequestMethod.PUT, produces = "application/json")
	public Response updateDownload(@PathVariable String songId) {
		return service.updateDownload(songId);
	}

	// download file
	@Override
	@RequestMapping(value = "/{songId}/download", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String songId, HttpServletRequest request, HttpServletResponse response) {
		service.downloadFile(songId, request, response);
	}

	// share
	@Override
	@RequestMapping(value = "/{username}/{songId}/share", method = RequestMethod.PUT, produces = "application/json")
	public Response shared(@PathVariable String username, @PathVariable String songId) {
		return service.shared(username, songId);
	}

	// unshare
	@Override
	@RequestMapping(value = "/{username}/{songId}/unshare", method = RequestMethod.PUT, produces = "application/json")
	public Response unShare(@PathVariable String username, @PathVariable String songId) {
		return service.unShared(username, songId);
	}

	// get share
	@Override
	@RequestMapping(value = "/share", method = RequestMethod.GET, produces = "application/json")
	public Response getShare(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "asc") String sort, @RequestParam(defaultValue = "_id") String field) {
		return service.getShare(page, size, sort, field);
	}
}