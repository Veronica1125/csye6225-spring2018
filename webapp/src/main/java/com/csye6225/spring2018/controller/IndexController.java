package com.csye6225.spring2018.controller;
import com.csye6225.spring2018.pojo.User;
import com.csye6225.spring2018.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String index(HttpServletRequest httpServletRequest) {
        logger.info("Loading home page.");
        //httpServletRequest.setAttribute("result", "******");
        return "index";
    }

    @RequestMapping("/find")
    public String find(HttpServletRequest httpServletRequest){
        String email = httpServletRequest.getParameter("email");
        User user = userRepository.findUserByEmail(email);
        httpServletRequest.setAttribute("result", user.getPassword());
        return "index";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/signup")
    public String signUp(){
        return "signup";
    }

//    @RequestMapping("/logout")
//    public String logout(){
//        return "redirect:/login";
//    }


}