package com.cms.cmssetting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Created by Momf Generator on 2018/10/18
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    private String username;

    private Integer age;

    /** 
    * 密码
    */
    private String password;

    /** 
    * 加密密码的盐
    */
    private String salt;

    /** 
    * 用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.
    */
    private Integer state;

    /** 
    * 是否可用
    */
    private Boolean available;
}