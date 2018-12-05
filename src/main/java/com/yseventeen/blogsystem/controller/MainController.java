package com.yseventeen.blogsystem.controller;

import com.yseventeen.blogsystem.domain.Authority;
import com.yseventeen.blogsystem.domain.User;
import com.yseventeen.blogsystem.repository.AuthorityRepository;
import com.yseventeen.blogsystem.repository.UserRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 主页控制器
 */
@RestController
@RequestMapping("/")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    private static final Long ROLE_USER_AUTHORITY_ID = 2L;

    @GetMapping("/")
    public String root(){
        return "redirect:/index";
    }

    @GetMapping("index")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @GetMapping("login")
    public ModelAndView login(){
        return new ModelAndView("login");
    }

    @PostMapping("login")
    public ModelAndView loginFrom(){
        return new ModelAndView("index");
    }

    @GetMapping("login-error")
    public ModelAndView loginError(Model model){
        model.addAttribute("loginError",true);
        model.addAttribute("errorMsg","登陆失败，用户名或者密码错误！");
        return new ModelAndView("login");
    }

    @GetMapping("register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @PostMapping("register")
    public ModelAndView registerUser(User user) {
        List<Authority> authorities = new ArrayList<>();
        Optional<Authority> authority = authorityRepository.findById(ROLE_USER_AUTHORITY_ID);
        authorities.add(authority.get());
        user.setUuid(UUID.randomUUID().toString());
        String password = user.getPassword();
        password = new BCryptPasswordEncoder().encode(password);
        user.setPassword(password);

        user.setAuthorities(authorities);
        userRepository.save(user);
        return new ModelAndView("redirect:/login");
    }

    @PostMapping("tp")
    @ResponseBody
    public String tp() {
       return "test post";
    }

    @GetMapping("search")
    public ModelAndView search() {
        return new ModelAndView("search");
    }
}
