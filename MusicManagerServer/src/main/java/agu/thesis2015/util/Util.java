/**
 * @author ltduoc
 */
package agu.thesis2015.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import agu.thesis2015.domain.User;
import agu.thesis2015.repo.UserRepo;

public class Util {

	@Autowired
	private UserRepo userRepo;

	public void init() {

		Set<String> Roles = new HashSet<String>();
		Roles.add("ROLE_USER");

		User ltduoc = userRepo.findOne("ltduoc");
		if (ltduoc == null) {
			User user = new User("ltduoc", "e10adc3949ba59abbe56e057f20f883e", "Fellipe Le");
			user.setRoles(Roles);
			user.setEmail("dth114092@gmail.com");
			user.setImage("/image/avartar.png");
			user.setActive(true);
			userRepo.save(user);
		}

		User nthienan = userRepo.findOne("nthienan");

		if (nthienan == null) {
			Roles.add("ROLE_ADMIN");
			User user = new User("nthienan", "e10adc3949ba59abbe56e057f20f883e", "An Nguyen");
			user.setRoles(Roles);
			user.setEmail("nguyenthienan93@gmail.com");
			user.setImage("/image/nthienan.jpg");
			user.setActive(true);
			userRepo.save(user);
		}
	}
}
