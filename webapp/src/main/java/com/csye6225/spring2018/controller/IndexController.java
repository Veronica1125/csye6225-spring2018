package com.csye6225.spring2018.controller;
import com.csye6225.spring2018.pojo.User;
import com.csye6225.spring2018.repository.UserRepository;
import com.csye6225.spring2018.service.SecurityServiceImpl;
import com.csye6225.spring2018.validator.UserValidator;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;

@Controller
public class IndexController {

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private UserValidator userValidator;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @RequestMapping("/")
    public String index(Principal principal, Model model) {
        logger.info("Loading home page.");
        User user = userRepository.findUserByEmail(principal.getName());
        if(user == null){
            return "login";
        }
        model.addAttribute("email", user.getEmail());
        return "index";
    }

    @RequestMapping("/find")
    public String find(HttpServletRequest httpServletRequest){
        String email = httpServletRequest.getParameter("email");
        User user = userRepository.findUserByEmail(email);
        httpServletRequest.setAttribute("result", user.getPassword());
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout, String reset){
        if(error != null){
            model.addAttribute("error", "Your email or password is not valid");
        }
        if(logout != null){
            model.addAttribute("logout", "You have been logged out successfully");
        }
        if(reset != null){
            model.addAttribute("reset", "Password reset success, Please login again!");
        }
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUp(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") User user, BindingResult bindingResult, Model model){
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            return "signup";
        }
        String password = user.getPassword();
        user.setPassword(bCryptPasswordEncoder().encode(password));
        user.setConfirmPassword(null);
        userRepository.save(user);
        return "login";
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String reset(){
        return "reset";
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public String changePassword(Principal principal, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model){
        User user = userRepository.findUserByEmail(principal.getName());
        String password = httpServletRequest.getParameter("password");
        String confirmPassword = httpServletRequest.getParameter("confirm");
        if(password == null || password.trim().equals("")){
            model.addAttribute("error", "Password can not be null!");
            return "reset";
        }
        if(confirmPassword == null || confirmPassword.trim().equals("")){
            model.addAttribute("error", "Confirmed password can not be null!");
            return "reset";
        }
        if(!password.equals(confirmPassword)){
            model.addAttribute("error","Password and confirmed password does not match!");
            return "reset";
        }
        user.setPassword(password);
        user.setPassword(bCryptPasswordEncoder().encode(password));
        user.setConfirmPassword(null);
        userRepository.save(user);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
        }
        return "/login?reset";
    }

    @RequestMapping("/{email}/profile/pic.jpeg")
    public void getImage(@PathVariable String email, HttpServletResponse httpServletResponse) throws IOException {
        User user  = userRepository.findUserByEmail(email);
        byte[] pic = user.getImage();
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        servletOutputStream.write(pic);
        servletOutputStream.close();
        servletOutputStream.flush();
        servletOutputStream = null;
    }

    @RequestMapping(value="/{email}/profile", method = RequestMethod.GET)
    public String userProfile(@PathVariable String email, Principal principal, Model model){
        if(principal == null || principal.getName() == null || !principal.getName().equals(email)){
            System.out.println("Visitor view");
            model.addAttribute("edit", false);
        }else{
            System.out.println("Logged in");
            model.addAttribute("edit", true);
        }
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("user", user);
        return "profile";
    }

    @RequestMapping(value = "/{email}/profile", method = RequestMethod.POST)
    public String userProfile(@PathVariable String email, HttpServletRequest httpServletRequest, Model model){
        User user = userRepository.findUserByEmail(email);
        String aboutMe = httpServletRequest.getParameter("aboutme");
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        if(aboutMe.trim().length() > 140){
            model.addAttribute("error","Length of About Me should be less than 140 chars!");
            return "profile";
        }
        user.setAboutMe(aboutMe);
        userRepository.save(user);
        return "redirect:/{email}/profile";
    }

    @RequestMapping(value = "/{email}/profile/updatepic", method = RequestMethod.POST)
    public String updateUserPic(@PathVariable String email, Model model, @RequestParam("updatepic") MultipartFile multipartFile){
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        byte[] bfile = new byte[(int) multipartFile.getSize()];
        if(bfile.length == 0){
            model.addAttribute("picError", "No File Chosen!");
            return "profile";
        }
        try {
            InputStream inputStream = multipartFile.getInputStream();
            inputStream.read(bfile);
            user.setImage(bfile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("picError","update profile picture failed!");
            return "profile";
        }
        return "redirect:/{email}/profile";
    }

    @RequestMapping(value = "/{email}/profile/deletepic", method = RequestMethod.POST)
    public String deleteUserPic(@PathVariable String email, Model model){
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        user.setImage(null);
        userRepository.save(user);
        return "redirect:/{email}/profile";
    }
}