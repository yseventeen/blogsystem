package com.yseventeen.blogsystem.controller;

import com.yseventeen.blogsystem.common.Response;
import com.yseventeen.blogsystem.domain.Blog;
import com.yseventeen.blogsystem.domain.Catalog;
import com.yseventeen.blogsystem.domain.User;
import com.yseventeen.blogsystem.repository.UserRepository;
import com.yseventeen.blogsystem.service.BlogService;
import com.yseventeen.blogsystem.service.CatalogService;
import com.yseventeen.blogsystem.util.ConstraintViolationExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/u")
public class UserspaceController {

    @Qualifier("userDetailsService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CatalogService catalogService;

    @Value(value = "${file.server.url}")
    private String fileServerUrl;

    /**
     * GET具体某个用户的主页
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "{username}")
    public ModelAndView userSpace(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("redirect:/u/" + username + "/blogs");
    }


    /**
     * u/{username}/profile:GET获取个人设置页面
     * PreAuthorize 判断当前用户与登陆用户是否是同一人
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("fileServerUrl", fileServerUrl);
        return new ModelAndView("userspace/profile");
    }


    /**
     * POST保存个人设置
     *
     * @param username 用户账号
     * @param user     待保存的对象
     * @return
     */
    @PostMapping(value = "/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, User user) {
        Optional<User> originalUser = userRepository.findById(Optional.ofNullable(user).get().getId());
        originalUser.get().setEmail(Optional.ofNullable(user).get().getEmail());
        originalUser.get().setName(Optional.ofNullable(user).get().getName());

        String originalPassword = originalUser.get().getPassword();
        String rawPassword = Optional.ofNullable(user).get().getPassword();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(rawPassword);

        if (!passwordEncoder.matches(originalPassword, encodePassword)) {
            originalUser.get().setPassword(rawPassword);
        }

        try {
            userRepository.save(originalUser.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/u/" + username + "/profile");
    }


    /**
     * GET获取个人头像
     *
     * @param username 用户账号
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/avatar")
    @PreAuthorize(value = "authentication.name.equals(#username)")
    public ModelAndView avatar(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/avatar");
    }


    /**
     * POST保存个人头像
     *
     * @param username 用户账号
     * @param user     待保存的对象
     * @return
     */

    @PostMapping(value = "/{username}/avatar")
    @PreAuthorize(value = "authentication.name.equals(#username)")
    public ResponseEntity<Response> avatar(@PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = Optional.ofNullable(user).get().getAvatar();
        Long id = Optional.ofNullable(user).get().getId();

        System.out.println(avatarUrl + "===" + id);
        Optional<User> originalUser = userRepository.findById(id);
        originalUser.get().setAvatar(avatarUrl);

        try {
            userRepository.save(originalUser.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
    }


    /**
     * GET查询用户博客，一下三个条件任选一个
     *
     * @param username
     * @param order     order:  排序类型，new/hot，默认是new
     * @param catalogId catalog:博客分类ID，默认是空
     * @param keyword   keyword:搜索关键字。博客的标签，即为关键字
     * @param async     async:是否异步请求页面
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("{username}/blogs")
    public ModelAndView listUserBlogUspace(@PathVariable("username") String username,
                                           @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                           @RequestParam(value = "catalog", required = false) Long catalogId,
                                           @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                           @RequestParam(value = "async", required = false) boolean async,
                                           @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                           Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);

        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) { // 分类查询
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByCatalog(catalog, pageable);
            order = "";
        } else if (order.equals("hot")) { // 最热查询
            Sort sort = new Sort(Sort.Direction.DESC, "readSize", "commentSize", "voteSize");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
        } else if (order.equals("new")) { // 最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByTitleVote(user, keyword, pageable);
        }

        List<Blog> list = page.getContent();    // 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("catalogId", catalogId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        return new ModelAndView(async == true ? "/userspace/u :: #mainContainerRepleace" : "/userspace/u");
    }

    /**
     * GET获取新增博客界面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "{username}/blogs/edit")
    public ModelAndView editBolg(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    /**
     * GET获取编辑博客界面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "{username}/blogs/edit/{id}")
    public ModelAndView editBolg(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", blogService.getBlogById(id));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("/userspace/blogedit");
    }


    /**
     * 保存博客
     *
     * @param username
     * @return
     */
    @PostMapping(value = "{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBolg(@PathVariable("username") String username, Blog blog) {
        User user = (User) userDetailsService.loadUserByUsername(username);

        if (Optional.ofNullable(blog).get().getCatalog().getId() == null) {
            return ResponseEntity.ok(new Response(false, "未选择分类"));
        }

        try {
            //判断是新增还是修改
            if (Optional.ofNullable(blog).get().getId() != null) {
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                orignalBlog.setCatalog(blog.getCatalog());
                orignalBlog.setTags(blog.getTags());
                blogService.saveBlog(orignalBlog);
            } else {
                blog.setUser(user);
                blogService.saveBlog(blog);
            }


        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));

    }

    // 删除博客
    /**
     * 删除博客
     * @param id
     * @return
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username,@PathVariable("id") Long id) {

        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }


}
