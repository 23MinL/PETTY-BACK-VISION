package io.github.petty.users.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JoinDTO {
    private String username;
    private String password;
    private String displayName;
    private String phone;

}
