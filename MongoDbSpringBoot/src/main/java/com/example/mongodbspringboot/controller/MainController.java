package com.example.mongodbspringboot.controller;

import com.example.mongodbspringboot.document.User;
import com.example.mongodbspringboot.repository.UserRepository;
import com.example.mongodbspringboot.repository.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
public class MainController {
    private static final String[] NAMES = {"Spy", "Family", "Jujutsu", "Kaisen"};

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    private UserRepository userRepository;

    @ResponseBody
    @RequestMapping("/")
    public String home() {
        String html = "";
        html += "<ul>";
        html += " <li><a href='/addUser'>Add User</a></li>";
        html += " <li><a href='/showAllUser'>Show All User</a></li>";
        html += " <li><a href='/showFullNameLikeSpy'>Show All 'Spy'</a></li>";
        html += " <li><a href='/deleteAllUser'>Delete All User</a></li>";
        html += "</ul>";
        return html;
    }

    @ResponseBody
    @RequestMapping("/addUser")
    public String testInsert() {
        User User = new User();

        long id = this.userRepositoryCustom.getMaxEmpId() + 1;
        int idx = (int) (id % NAMES.length);
        String fullName = NAMES[idx] + " " + id;

        User.setId(id);
        User.setEmpNo("E" + id);
        User.setFullName(fullName);
        User.setHireDate(new Date());
        this.userRepository.insert(User);

        return "Inserted: " + User;
    }

    @ResponseBody
    @RequestMapping("/showAllUser")
    public String showAllUser() {

        List<User> users = this.userRepository.findAll();

        String html = "";
        for (User emp : users) {
            html += emp + "<br>";
        }

        return html;
    }

    @ResponseBody
    @RequestMapping("/showFullNameLikeSpy")
    public String showFullNameLikeSpy() {

        List<User> users = this.userRepository.findByFullNameLike("Spy");

        String html = "";
        for (User emp : users) {
            html += emp + "<br>";
        }

        return html;
    }

    @ResponseBody
    @RequestMapping("/deleteAllUser")
    public String deleteAllUser() {

        this.userRepository.deleteAll();
        return "Đã xóa :D";
    }


}
