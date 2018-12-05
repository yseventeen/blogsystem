package com.yseventeen.blogsystem.repository;

import com.yseventeen.blogsystem.domain.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<Catalog,Long> {
}
