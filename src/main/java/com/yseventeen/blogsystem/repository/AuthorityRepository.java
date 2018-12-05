package com.yseventeen.blogsystem.repository;

import com.yseventeen.blogsystem.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority,Long> {
}
