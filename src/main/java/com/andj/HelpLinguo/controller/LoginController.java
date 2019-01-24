package com.andj.HelpLinguo.controller;

import javax.validation.Valid;

import com.andj.HelpLinguo.model.Question;
import com.andj.HelpLinguo.model.Tag;
import com.andj.HelpLinguo.model.User;
import com.andj.HelpLinguo.model.UserInfo;
import com.andj.HelpLinguo.service.QuestionService;
import com.andj.HelpLinguo.service.UserService;
import com.andj.HelpLinguo.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class LoginController {

    public class QuestTag {
        public Question question;
        public String tags;
    }

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @RequestMapping(value = {"/", "/landing"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("landing");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/info", method = RequestMethod.GET)
    public ModelAndView info() {
        ModelAndView modelAndView = new ModelAndView();
        UserInfo userInfo = new UserInfo();
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.setViewName("admin/info");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/info", method = RequestMethod.POST)
    public ModelAndView updateInfo(@Valid UserInfo userInfo, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/info");
        } else {
            userService.saveUserInfo(userInfo);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            UserInfo u = null;
            if (user.getUserInfo() != null) {
                u = user.getUserInfo();
            }
            user.setUserInfo(userInfo);
            userService.updateUser(user);
            if (u != null) {
                userService.deleteUserInfo(u);
            }
            modelAndView.addObject("successMessage", "Updated succesfully");
            modelAndView.addObject("user_info", new UserInfo());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/home", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        UserInfo userInfo = user.getUserInfo();
        List<Question> questions = questionService.findQuestionsByUser(user.getId());
        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        if (userInfo != null) {
            modelAndView.addObject("langInfo", "Your chosen languages are: " + userInfo.getLang1() + " and " + userInfo.getLang2() + ".");
        } else {
            modelAndView.addObject("langInfo", "No information provided yet");
        }
        if (userInfo != null) {
            modelAndView.addObject("avatar", userInfo.getAvatar());
        }
        String flag = "";
        if (user.getCountry().equals("Ukraine")) {
            flag = "ukr.png";
        } else if (user.getCountry().equals("Poland")) {
            flag = "pol.png";
        } else if (user.getCountry().equals("England")) {
            flag = "en.png";
        }
        modelAndView.addObject("flag", flag);
        modelAndView.addObject("ask", "Number of asked questions: " + questions.size());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/browse", method = RequestMethod.GET)
    public ModelAndView browse() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<Question> questions = questionService.findAll();
        List<String> tags = new ArrayList<String>();
        List<QuestTag> questTags = new ArrayList<QuestTag>();
        if (user != null) {
            modelAndView.addObject("info", "<a href=\"/admin/info\">Update Info</a>");
            modelAndView.addObject("messages", " <a href=\"/admin/messages\">Messages</a>");
            modelAndView.addObject("logout", " <a href=\"/logout\">Logout</a>");
        } else {
            modelAndView.addObject("info");
            modelAndView.addObject("messages");
            modelAndView.addObject("logout");
        }
        for (int i = 0; i < questions.size(); i++) {
            List<Tag> tagq = questionService.findAllByQuestionId(questions.get(i).getId());
            String tagstr = "";
            for (int j = 0; j < tagq.size(); j++) {
                tagstr += tagq.get(j).getTagText() + " ";
            }
            tags.add(tagstr);
            QuestTag questTag = new QuestTag();
            questTag.question = questions.get(i);
            questTag.tags = tagstr;
            questTags.add(questTag);
        }
        modelAndView.addObject("questions", questTags);
        modelAndView.setViewName("browse");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/ask", method = RequestMethod.GET)
    public ModelAndView showAsk() {
        ModelAndView modelAndView = new ModelAndView();
        Question question = new Question();
        modelAndView.addObject("question", question);
        modelAndView.addObject("tag1");
        modelAndView.addObject("tag2");
        modelAndView.addObject("tag3");
        modelAndView.addObject("tag4");
        modelAndView.addObject("tag5");
        modelAndView.setViewName("admin/ask");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/ask", method = RequestMethod.POST)
    public ModelAndView ask(@Valid Question question, @RequestParam("tag1") String tag1, @RequestParam("tag2") String tag2, @RequestParam("tag3") String tag3, @RequestParam("tag4") String tag4, @RequestParam("tag5") String tag5) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        question.setUser(user);
        questionService.saveQuestion(question);
        if (!tag1.equals("")) {
            Tag tag = new Tag();
            tag.setTagText(tag1);
            tag.setQuestion(question);
            questionService.saveTag(tag);
        }
        if (!tag2.equals("")) {
            Tag tag = new Tag();
            tag.setTagText(tag2);
            tag.setQuestion(question);
            questionService.saveTag(tag);
        }
        if (!tag3.equals("")) {
            Tag tag = new Tag();
            tag.setTagText(tag3);
            tag.setQuestion(question);
            questionService.saveTag(tag);
        }
        if (!tag4.equals("")) {
            Tag tag = new Tag();
            tag.setTagText(tag4);
            tag.setQuestion(question);
            questionService.saveTag(tag);
        }
        if (!tag5.equals("")) {
            Tag tag = new Tag();
            tag.setTagText(tag5);
            tag.setQuestion(question);
            questionService.saveTag(tag);
        }
        modelAndView.addObject("successMessage", "Question created succesfully");
        modelAndView.setViewName("admin/ask");
        return modelAndView;
    }

    @RequestMapping(value="/admin/answer", method=RequestMethod.GET)
    public ModelAndView showAnswer(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/answer");
        return modelAndView;
    }
}
