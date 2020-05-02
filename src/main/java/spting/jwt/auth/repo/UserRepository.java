package spting.jwt.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import spting.jwt.auth.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel,Integer> {
    UserModel findByUserName(String username);
}
