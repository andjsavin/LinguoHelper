package com.andj.HelpLinguo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_info_id")
    private int id;
    @Column(name = "lang1")
    private String lang1;
    @Column(name = "lang2")
    private String lang2;
    @Column(name = "avatar")
    private String avatar;
    @OneToOne(mappedBy = "userInfo")
    private User user;

}