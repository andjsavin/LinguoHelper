package com.andj.HelpLinguo.repository;

import com.andj.HelpLinguo.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userInfoRepository")
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}

