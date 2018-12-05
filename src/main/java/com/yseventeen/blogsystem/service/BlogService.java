package com.yseventeen.blogsystem.service;

import com.yseventeen.blogsystem.domain.Blog;
import com.yseventeen.blogsystem.domain.Catalog;
import com.yseventeen.blogsystem.domain.User;
import com.yseventeen.blogsystem.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }

    public Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        //Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
        String tags = title;
        Page<Blog> blogs = blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title,user, tags,user, pageable);
        return blogs;
    }

    public Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable) {
        return blogRepository.findByCatalog(catalog, pageable);
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).get();
    }

    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
