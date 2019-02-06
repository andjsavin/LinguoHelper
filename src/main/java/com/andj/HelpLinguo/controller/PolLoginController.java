package com.andj.HelpLinguo.controller;

import javax.validation.Valid;

import com.andj.HelpLinguo.model.*;
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
import java.util.Optional;
import java.util.Set;

@Controller
public class PolLoginController {

    public class QuestTag {
        public Question question;
        public String tags;
    }

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @RequestMapping(value = {"/pl", "/landing_pl"}, method = RequestMethod.GET)
    public ModelAndView loginPL() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("landing_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/registration_pl", method = RequestMethod.GET)
    public ModelAndView registrationPL() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/info_pl", method = RequestMethod.GET)
    public ModelAndView infoPL() {
        ModelAndView modelAndView = new ModelAndView();
        UserInfo userInfo = new UserInfo();
        modelAndView.addObject("userInfo", userInfo);
        modelAndView.setViewName("admin/info_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/info_pl", method = RequestMethod.POST)
    public ModelAndView updateInfoPL(@Valid UserInfo userInfo, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/info_pl");
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
            modelAndView.addObject("successMessage", "Aktualizacja zakończona sukcesem");
            modelAndView.addObject("user_info", new UserInfo());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/registration_pl", method = RequestMethod.POST)
    public ModelAndView createNewUserPL(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "Użytkownik z podanym adresem e-mail już jest zarejestrowany");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration_pl");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "Użytkownik został zarejestrowany");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration_pl");

        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/home_pl", method = RequestMethod.GET)
    public ModelAndView homePL() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        UserInfo userInfo = user.getUserInfo();
        List<Question> questions = questionService.findQuestionsByUser(user.getId());
        List<Answer> answers = questionService.findAnswersByUser(user.getId());
        modelAndView.addObject("userName", "Witamy " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        if (userInfo != null) {
            modelAndView.addObject("langInfo", "Twoje obrane języki: " + userInfo.getLang1() + " i " + userInfo.getLang2() + ".");
        } else {
            modelAndView.addObject("langInfo", "Informacja nie została wprowadzona");
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
        modelAndView.addObject("ask", "Ilość zadanych pytań: " + questions.size());
        modelAndView.addObject("answer", "Ilość danych odpoweidzi: " + answers.size());
        modelAndView.setViewName("admin/home_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/user_pl", method = RequestMethod.GET)
    public ModelAndView userInfoPL(@RequestParam("id") int id) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findById(id).get();
        UserInfo userInfo = user.getUserInfo();
        List<Question> questions = questionService.findQuestionsByUser(user.getId());
        List<Answer> answers = questionService.findAnswersByUser(user.getId());
        modelAndView.addObject("userName", user.getName() + " " + user.getLastName());
        if (userInfo != null) {
            modelAndView.addObject("langInfo", "Twoje obrane języki: " + userInfo.getLang1() + " i " + userInfo.getLang2() + ".");
        } else {
            modelAndView.addObject("langInfo", "Informacja nie została wprowadzona");
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
        modelAndView.addObject("ask", "Ilość zadanych pytań: " + questions.size());
        modelAndView.addObject("answer", "Ilość danych odpoweidzi: " + answers.size());
        modelAndView.setViewName("admin/user_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/browse_pl", method = RequestMethod.GET)
    public ModelAndView browsePL() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<Question> questions = questionService.findAll();
        List<String> tags = new ArrayList<String>();
        List<QuestTag> questTags = new ArrayList<QuestTag>();
        if (user != null) {
            modelAndView.addObject("info", "<a href=\"/admin/info_pl\">Aktualizuj informacje</a>");
            modelAndView.addObject("messages", " <a href=\"/admin/messages_pl\">Wiadomości</a>");
            modelAndView.addObject("logout", " <a href=\"/logout\">Wyloguj</a>");
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
        modelAndView.setViewName("browse_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/ask_pl", method = RequestMethod.GET)
    public ModelAndView showAskPL() {
        ModelAndView modelAndView = new ModelAndView();
        Question question = new Question();
        modelAndView.addObject("question", question);
        modelAndView.addObject("tag1");
        modelAndView.addObject("tag2");
        modelAndView.addObject("tag3");
        modelAndView.addObject("tag4");
        modelAndView.addObject("tag5");
        modelAndView.setViewName("admin/ask_pl");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/ask_pl", method = RequestMethod.POST)
    public ModelAndView askPL(@Valid Question question, @RequestParam("tag1") String tag1, @RequestParam("tag2") String tag2, @RequestParam("tag3") String tag3, @RequestParam("tag4") String tag4, @RequestParam("tag5") String tag5) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        question.setUser(user);
        question.setViews(0);
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
        modelAndView.addObject("successMessage", "Pytanie zostało zadane");
        modelAndView.setViewName("admin/ask_pl");
        return modelAndView;
    }

    @RequestMapping(value="/admin/answer_pl", method=RequestMethod.GET)
    public ModelAndView showAnswerPL(@RequestParam("id") int id) {
        List<Answer> answers = questionService.findAllAnswersByQuestionId(id);
        Optional<Question> question = questionService.findById(id);
        question.get().setViews(question.get().getViews() + 1);
        questionService.saveQuestion(question.get());
        Answer answer = new Answer();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", question.get().getTitle());
        modelAndView.addObject("question_text", question.get().getText());
        modelAndView.addObject("answers", answers);
        modelAndView.addObject("answer", answer);
        modelAndView.addObject("id", id);
        modelAndView.setViewName("/admin/answer_pl");
        return modelAndView;
    }

    @RequestMapping(value="/admin/messages_pl", method=RequestMethod.GET)
    public ModelAndView showMessagesPL() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/messages_pl");
        return modelAndView;
    }

    @RequestMapping(value="/admin/test_pl", method=RequestMethod.GET)
    public ModelAndView testPL() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/test_pl");
        return modelAndView;
    }

    @RequestMapping(value="/admin/answer_pl", method=RequestMethod.POST)
    public ModelAndView giveAnswerPL(@Valid Answer answer, @RequestParam("id") int id) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        answer.setUser(user);
        answer.setQuestion(questionService.findById(id).get());
        questionService.saveAnswer(answer);
        List<Answer> answers = questionService.findAllAnswersByQuestionId(id);
        Optional<Question> question = questionService.findById(id);
        question.get().setViews(question.get().getViews() + 1);
        questionService.saveQuestion(question.get());
        Answer answerNew = new Answer();
        modelAndView.addObject("title", question.get().getTitle());
        modelAndView.addObject("question_text", question.get().getText());
        modelAndView.addObject("answers", answers);
        modelAndView.addObject("answer", answerNew);
        modelAndView.addObject("id", id);
        modelAndView.setViewName("admin/answer_pl");
        return modelAndView;
    }
}
