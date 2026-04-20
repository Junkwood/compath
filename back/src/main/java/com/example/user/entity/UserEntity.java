package com.example.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_seq")
    @SequenceGenerator(
            name = "emp_seq",         // JPA 안에서 부를 생성기 별명
            sequenceName = "emp_seq", // ⭐ 실제 오라클 DB에 만들어둔 시퀀스 이름 (예: USER_SEQ)
            allocationSize = 1)              // ⭐ 오라클 시퀀스가 1씩 증가한다면 반드시 1로 설정!
    private Integer userId;
    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;
    @Column(nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(name = "user_type", nullable = false, length = 2)
    @ColumnTransformer(read = "role(user_type)")
    private String userType;

    @Column(name = "is_active", nullable = false, length = 2)
    private String isActive = "01";
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // 5. Null 허용 컬럼
    @Column(name = "editor_user_id")
    private Integer editorUserId;

    @Formula("(SELECT g.group_name " +
            "FROM group_members gm " +
            "JOIN groups g ON gm.group_id = g.group_id " +
            "WHERE gm.user_id = user_id " + // 여기서 뒤의 user_id는 현재 users 테이블의 PK입니다.
            "AND gm.is_primary = 'Y')")
    private String primaryGroupName;
    @Formula("(SELECT gm.group_id " +
            "FROM group_members gm " +
            "WHERE gm.user_id = user_id " + // 여기서 뒤의 user_id는 현재 users 테이블의 PK입니다.
            "AND gm.is_primary = 'Y')")
    private Integer primaryGroupId;
}
