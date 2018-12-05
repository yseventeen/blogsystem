package com.yseventeen.blogsystem.repository;

import com.yseventeen.blogsystem.domain.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog,Long> {
}
