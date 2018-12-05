package com.yseventeen.blogsystem.repository.es;

import com.yseventeen.blogsystem.domain.es.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,String> {
    Page<EsBlog> findDistinctByTitleContainingOrSummeryContainingOrContentContaining(String title, String summery, String contain, Pageable pageable);
}
