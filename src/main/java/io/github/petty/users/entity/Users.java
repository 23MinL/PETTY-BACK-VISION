package io.github.petty.users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username; // 현재는 id , 추후에는는 이름으로 사용
    private String email; // 추후에는 id 겸용
    private String password;
    private String role;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
