package com.yseventeen.blogsystem.controller;

import com.yseventeen.blogsystem.common.Response;
import com.yseventeen.blogsystem.domain.User;
import com.yseventeen.blogsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
@RequestMapping("/u")
public class UserspaceController {

    @Qualifier("userDetailsService")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Value(value = "${file.server.url}")
    private String fileServerUrl;


    // u/{username}:GET具体某个用户的主页
    // username


    /**
     * u/{username}/profile:GET获取个人设置页面
     * PreAuthorize 判断当前用户与登陆用户是否是同一人
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model){
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user",user);
        model.addAttribute("fileServerUrl",fileServerUrl);
        return new ModelAndView("userspace/profile");
    }


    /**
     * POST保存个人设置
     * @param username 用户账号
     * @param user 待保存的对象
     * @return
     */
    @PostMapping(value = "/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, User user){
        Optional<User> originalUser = userRepository.findById(Optional.ofNullable(user).get().getId());
        originalUser.get().setEmail(Optional.ofNullable(user).get().getEmail());
        originalUser.get().setName(Optional.ofNullable(user).get().getName());

        String originalPassword = originalUser.get().getPassword();
        String rawPassword = Optional.ofNullable(user).get().getPassword();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePassword = passwordEncoder.encode(rawPassword);

        if (!passwordEncoder.matches(originalPassword,encodePassword)){
            originalUser.get().setPassword(rawPassword);
        }

        try {
            userRepository.save(originalUser.get());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/u/"+username+"/profile");
    }


    /**
     * GET获取个人头像
     * @param username 用户账号
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/avatar")
    @PreAuthorize(value = "authentication.name.equals(#username)")
    public ModelAndView avatar(@PathVariable("username") String username,Model model){
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user",user);
        return new ModelAndView("userspace/avatar");
    }


    /**
     * POST保存个人头像
     * @param username 用户账号
     * @param user 待保存的对象
     * @return
     */

    @PostMapping(value = "/{username}/avatar")
    @PreAuthorize(value = "authentication.name.equals(#username)")
    public ResponseEntity<Response> avatar(@PathVariable("username") String username, @RequestBody User user){
        String avatarUrl = Optional.ofNullable(user).get().getAvatar();
        Long id = Optional.ofNullable(user).get().getId();

        System.out.println(avatarUrl+"==="+id);
        Optional<User> originalUser = userRepository.findById(id);
        originalUser.get().setAvatar(avatarUrl);

        try {
            userRepository.save(originalUser.get());
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(new Response(true,"处理成功",avatarUrl));
    }


    // u/{username}/blogs:GET查询用户博客，一下三个条件任选一个
    //  order:  排序类型，new/hot，默认是new
    //  catalog:博客分类ID，默认是空
    //  keyword:搜索关键字。博客的标签，即为关键字
    //  async:是否异步请求页面
    //  pageIndex:
    //  pageSize:


    // u/{username}/blogs/edit:GET获取新增或者编辑的博客界面


    // u/{username}/blogs/edit:POST获取新增或者编辑的博客

    // u/{username}/blogs/edit/{id}:GET获取编辑某一篇博客的界面



    // 删除博客


}
