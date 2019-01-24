package com.andj.HelpLinguo.repository;

import com.andj.HelpLinguo.model.Question;
import com.andj.HelpLinguo.model.Role;

import com.andj.HelpLinguo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("questionRepository")
public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
