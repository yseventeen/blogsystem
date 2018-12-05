package com.yseventeen.blogsystem.controller;

import com.google.common.base.Strings;
import com.yseventeen.blogsystem.common.Response;
import com.yseventeen.blogsystem.domain.Authority;
import com.yseventeen.blogsystem.domain.User;
import com.yseventeen.blogsystem.repository.AuthorityRepository;
import com.yseventeen.blogsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    /**
     * 查询所有用户 列表
     * @param model
     * @param async
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    // GET/users :返回用于展现用户列表的list.html页面
    @GetMapping
    public ModelAndView list(Model model,@RequestParam(value="async",required=false) boolean async,
                             @RequestParam(name = "name", required = false,defaultValue = "") String name ,
                             @RequestParam(name = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        // 模糊查询
        name = "%" + name + "%";
        Page<User> page = userRepository.findByUsernameLike(name,pageable);

        logger.info("name = "+name,"users = "+page.getContent());
//        Page<User> page = userRepository.findAll(pageable);
        model.addAttribute("userList", page.getContent());
        model.addAttribute("page", page);
        return new ModelAndView(async==true?"users/list :: #mainContainerRepleace":"users/list", "userModel", model);
    }

    /**
     * 新增用户的表单页面
     * @param model
     * @return
     */
    // GET/users/form :返回用户新增或者修改用户的form.html页面
    @GetMapping(value = "add")
    public ModelAndView createForm(Model model) {
        User u = new User(null, null, null, null, null, 1, null);
        Optional<User> user = Optional.ofNullable(u);
        model.addAttribute("title", "用户管理");
        model.addAttribute("user", user);
        return new ModelAndView("users/add");
    }

    /**
     * 新增用户  表单提交的方法
     * @param user
     * @return
     */
    // POST/users :新增或者修改用户，成功过后重定向到list.html页面
    @PostMapping
    public ResponseEntity<Response> create(User user,Long authorityId) {
        logger.info("users/"+user+"   方法被调用");

        List<Authority> authorities = new ArrayList<>();
        Optional<Authority> one = authorityRepository.findById(authorityId);
        authorities.add(one.get());
        user.setAuthorities(authorities);

        String uuid = "";
        try {
            uuid = user.getUuid();
        } catch (NullPointerException e) {
            logger.info("from uuid is null ---- 执行保存操作");
        }
        if (Strings.isNullOrEmpty(uuid)){
            user.setUuid(UUID.randomUUID().toString());
        }

        userRepository.save(user);

        return ResponseEntity.ok().body(new Response(true,"处理成功",user));
    }

    /**
     * 删除用户  提交处理的方法
     * @param uuid
     * @return
     */
    // GET/users/delete/{id} :根据id删除相应的用户数据，成功后重定向到list.html页面
    @DeleteMapping(value = "/{uuid}")
    @Transactional
    public ResponseEntity<Response> delete(@PathVariable String uuid) {
        logger.info("delete  --  users/"+uuid+"   方法被调用");
        try {
            userRepository.deleteByUuid(uuid);
        }catch (Exception e){
            return ResponseEntity.ok().body(Response.createByErrorMessage(e.getMessage()));
        }
        return ResponseEntity.ok().body(Response.createBySuccess());
    }

    /**
     * 修改用户显示的表单页面
     * @param model
     * @param uuid
     * @return
     */
    @GetMapping(value = "edit/{uuid}")
    public ModelAndView edit(Model model, @PathVariable String uuid) {
        logger.info("edit/"+uuid+"   方法被调用");
        Optional<User> user = userRepository.findByUuid(uuid);
        model.addAttribute("user", user);

        return new ModelAndView("users/edit");
    }





    // GET/users/{id} :返回用户展现用户的view.html页面
    @GetMapping(value = "/{uuid}")
    public ModelAndView view(Model model, @PathVariable String uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        model.addAttribute("user", user);
        model.addAttribute("title", "用户管理");
        return new ModelAndView("users/view");
    }


}
