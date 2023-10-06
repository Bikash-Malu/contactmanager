package com.smart.smartcontact.controller;

import com.smart.smartcontact.dao.ContactRepository;
import com.smart.smartcontact.dao.UserRepository;
import com.smart.smartcontact.entities.Contact;
import com.smart.smartcontact.entities.User;
import com.smart.smartcontact.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @ModelAttribute
    public void addCommonData(Model model,Principal principal){
        String userName=principal.getName();
        System.out.println(userName);
        User user= this.userRepository.getUserByUserName(userName);
        System.out.println(user);
        model.addAttribute("user",user);


    }
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        return "normal/user_dashboard";
    }
    //add form handler
    @GetMapping("/add_contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","contact page");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }
//    processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileimage") MultipartFile file,
                                 Principal principal){
        try{
            String name=principal.getName();
            User user=this.userRepository.getUserByUserName(name);
            if(!file.isEmpty()){
                contact.setImage(file.getOriginalFilename());
                File file1=new ClassPathResource("static/photo").getFile();
                Path path=  Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("file upload");
            }
            else{
                contact.setImage("user.png");
            }
            user.getContacts().add(contact);
            contact.setUser(user);
            this.userRepository.save(user);
            System.out.println(contact);
            System.out.println("added to database");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "normal/add_contact_form";
    }
    //per page 5
    @GetMapping("/show-contact/{page}")
    public String showContact(@PathVariable("page") Integer page, Model m,Principal principal){
        m.addAttribute("title","show user contact");
          String userName=principal.getName();
      User user=this.userRepository.getUserByUserName(userName);
        Pageable pageable=PageRequest.of(page,5);
        Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
        m.addAttribute("contacts",contacts);
        m.addAttribute("currentPage",page);
        m.addAttribute("totalpage",contacts.getTotalPages());
        return "normal/show_contact";
    }
    //particular contact details
    @RequestMapping("/{cid}/contact")
    public String show(@PathVariable("cid")Integer cid,Model model){
        Optional<Contact> optional=this.contactRepository.findById(cid);
       Contact contact=optional.get();
       model.addAttribute("contact",contact);
        return "normal/contact_detail";
    }
    //delete contact
    @GetMapping("/delete/{cid}")
    public String deletecontact(@PathVariable("cid")Integer cid, HttpSession session){
        Optional<Contact>optional=this.contactRepository.findById(cid);
     Contact contact=optional.get();
     contact.setUser(null);
     this.contactRepository.delete(contact);
     //session.setAttribute("message",new Message("contact delete succesfully..","success"));
        return "normal/delete";
    }
//update form
    @PostMapping("/update_contact/{cid}")
    public String upadte(Model m,@PathVariable("cid") Integer cid){
        m.addAttribute("title","update contact");
      Contact contact = this.contactRepository.findById(cid).get();
      m.addAttribute("contact",contact);
        return "normal/update_form";
    }
   // update contact handelr
    @PostMapping("/process-update")
    public String handler(@ModelAttribute Contact contact,Principal principal,HttpSession session){
        User user=this.userRepository.getUserByUserName(principal.getName());
        contact.setUser(user);
        this.contactRepository.save(contact);
        return "normal/update";
    }
//YOUR profile
    @GetMapping("/profile")
    public String profile(Model m){
        m.addAttribute("title","profile page");
        return "normal/profile";
    }

}
