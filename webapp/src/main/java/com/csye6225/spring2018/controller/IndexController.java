package com.csye6225.spring2018.controller;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.Topic;
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
import java.util.List;

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

    private S3BucketController s3BucketController = new S3BucketController();

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
    public String login(Model model, String error, String logout, String reset, String signup){
        if(error != null){
            model.addAttribute("error", "Your email or password is not valid");
        }
        if(logout != null){
            model.addAttribute("logout", "You have been logged out successfully");
        }
        if(reset != null){
            model.addAttribute("reset", "Password reset success, Please login again!");
        }
        if(signup != null){
            model.addAttribute("signup", "Sign up success, Please login!");
        }
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUp(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") User user, BindingResult bindingResult, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        userValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            return "signup";
        }
        String password = user.getPassword();
        user.setPassword(bCryptPasswordEncoder().encode(password));
        user.setConfirmPassword(null);
        userRepository.save(user);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
        }
        return "redirect:/login?signup";
    }

    @RequestMapping(value = "/snsrset", method = RequestMethod.GET)
    public String snsRset(Principal principal){
        User user = userRepository.findUserByEmail(principal.getName());
        if(user != null){
            AmazonSNS snsClient = AmazonSNSClientBuilder.standard()
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .build();
            List<Topic> topics = snsClient.listTopics().getTopics();
            for(Topic topic : topics){

                if(topic.getTopicArn().endsWith("password_reset")){
                    PublishRequest req = new PublishRequest(topic.getTopicArn(), user.getEmail());
                    snsClient.publish(req);
                    break;
                }
            }
        }
        return "/";
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
        return "redirect:/login?reset";
    }

    @RequestMapping("/{email}/profile/pic.jpeg")
    public void getImage(@PathVariable String email, HttpServletResponse httpServletResponse) throws IOException {
        User user  = userRepository.findUserByEmail(email);
        //byte[] pic = user.getImage();
        byte[] pic = s3BucketController.getFile(user.getEmail() + "ProfilePic");
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
        byte[] pic = s3BucketController.getFile(user.getEmail() + "ProfilePic");
        if(pic == null || pic.length == 0){
            model.addAttribute("defaultPic", true);
            System.out.println("using default profile picture");
        }else{
            model.addAttribute("defaultPic", false);
            System.out.println("using user's profile picture");
        }
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
        if((int) multipartFile.getSize() == 0){
            model.addAttribute("picError", "No File Chosen!");
            return "redirect:/{email}/profile";
        }
        try {
            s3BucketController.uploadFile(user.getEmail(), multipartFile);
        } catch (Exception e) {
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
        //user.setImage(null);
        //userRepository.save(user);
        s3BucketController.deleteFile(user.getEmail() + "ProfilePic");
        return "redirect:/{email}/profile";
    }
}