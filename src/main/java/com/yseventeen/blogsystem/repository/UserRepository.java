package com.yseventeen.blogsystem.repository;

import com.yseventeen.blogsystem.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUuid(String uuid);

    void deleteByUuid(String uuid);

    Page<User> findByUsernameLike(String name, Pageable pageable);

    User findByUsername(String username);
}
