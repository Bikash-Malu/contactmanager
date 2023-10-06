package com.smart.smartcontact.controller;
import com.smart.smartcontact.dao.UserRepository;
import com.smart.smartcontact.entities.User;
import com.smart.smartcontact.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String home1(Model model){
        model.addAttribute("title","Home-Start Contact Manager");
        return "home";
    }
    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About-Start Contact Manager");
        return "about";
    }
    @GetMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("title","Register-Start Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }
    @GetMapping("/signin")
    public String cumtomLogin(Model model){
        model.addAttribute("title","login page");
        return "login1";
    }
    //handler for register
    @PostMapping("/do_register")
    public String registerUser(@ModelAttribute("user")User user, @RequestParam(value = "term",defaultValue = "false")boolean term, Model model, HttpSession session)
    {
        try{
            if(!term){
                System.out.println("you have not agree the term and condition");
                throw new Exception("you have not agree the term and condition");
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageurl("photo/user.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println(term);
            System.out.println(user);
            User result= this.userRepository.save(user);
            model.addAttribute("user",new User());
            session.setAttribute("message",new Message("successfully register!!","alert-success"));
            return "login";
        }
        catch(Exception e){
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message",new Message("something went wrong!!"+e.getMessage(),"alert-danger"));
            return "signup";
        }
    }

}
