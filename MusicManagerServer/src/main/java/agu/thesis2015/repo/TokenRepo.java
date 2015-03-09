package agu.thesis2015.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import agu.thesis2015.domain.Token;

@Repository
public interface TokenRepo extends MongoRepository<Token, String> {
	@Query("{'username':?0}")
	public List<Token> findByUsername(String username);
}
