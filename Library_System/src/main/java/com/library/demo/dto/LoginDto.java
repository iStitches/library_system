package com.library.demo.dto;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginDto{
    @Size(min = 5,message = "用户名必须大于5位")
    private String username;
    @Pattern(regexp = "^[a-z A-Z 0-9]{6,}",message = "密码必须为大于6位的字母数字组合")
    private String password;

}
