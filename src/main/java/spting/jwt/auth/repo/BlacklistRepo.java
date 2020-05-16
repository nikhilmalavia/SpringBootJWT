package spting.jwt.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import spting.jwt.auth.model.Blacklisted;

@Repository
public interface BlacklistRepo extends JpaRepository<Blacklisted, Integer> {

	@Query("SELECT COUNT(id) FROM Blacklisted b WHERE b.token = :token")
	int findByToken(@Param("token") String token);
	
	
}
