package com.andj.HelpLinguo.service;

import com.andj.HelpLinguo.model.*;
import com.andj.HelpLinguo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("questionService")
public class QuestionService {

    private final TagRepository tagRepository;
    private final AnswerRepository answerRepository;
    private QuestionRepository questionRepository;
    private UserInfoRepository userInfoRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public QuestionService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserInfoRepository userInfoRepository,
                       QuestionRepository questionRepository,
                       TagRepository tagRepository,
                       AnswerRepository answerRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
        this.answerRepository = answerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<Question> findQuestionsByUser(int id) {
        List<Question> all = questionRepository.findAll();
        List<Question> questions = new ArrayList<Question>();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getUser().getId() == id) {
                questions.add(all.get(i));
            }
        }
        return questions;
    }

    public List<Answer> findAnswersByUser(int id) {
        List<Answer> all = answerRepository.findAll();
        List<Answer> answers = new ArrayList<Answer>();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getUser().getId() == id) {
                answers.add(all.get(i));
            }
        }
        return answers;
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(int question_id) { return questionRepository.findById(question_id); }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    public void deleteUserInfo(UserInfo userInfo) {
        userInfoRepository.delete(userInfo);
    }

    public Tag saveTag(Tag tag){ return tagRepository.save(tag);}

    public List<Tag> findAllByQuestionId(int question_id) { return tagRepository.findAllByQuestionId(question_id);}

    public List<Answer> findAllAnswersByQuestionId(int question_id) { return answerRepository.findAllByQuestionId(question_id); }

    public Answer saveAnswer(Answer answer) {return answerRepository.save(answer);}
}
