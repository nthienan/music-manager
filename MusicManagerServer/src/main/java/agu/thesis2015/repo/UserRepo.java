package agu.thesis2015.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import agu.thesis2015.domain.User;

/**
 * 
 * @author ltduoc
 *
 */
@Repository
public interface UserRepo extends MongoRepository<User, String> {

	@Query("{$or : [{'username' : { $regex: ?0, $options: 'i' }}, {'fullname' : { $regex: ?0, $options: 'i' } }, {'roles' : { $regex: ?0, $options: 'i' } }  ] }")
	public Page<User> search(Pageable page, String keywork);

	@Query("{ $and:[ {'username' : ?0},{ 'password' : ?1} ]}")
	public User checkPassWord(String username, String password);

}
