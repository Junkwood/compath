package com.example.user.persistence;

import com.example.user.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    // 1. 중복 검사용 (List로 전체를 가져와서 Limit 쿼리 방지)
    @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId AND u.isActive = 'O1'")
    List<UserEntity> findByUserId(@Param("userId") Integer userId);

    // 2. 로그인 검증용 (역시 List로 가져와서 Limit 쿼리 방지)
    List<UserEntity> findByUserIdAndPassword(Integer userId, String password);
}