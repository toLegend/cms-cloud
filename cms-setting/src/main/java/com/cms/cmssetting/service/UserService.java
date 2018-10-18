package com.cms.cmssetting.service;import com.cms.cmssetting.entity.User;import com.cms.cmssetting.entity.UserExample;import com.cms.cmssetting.mapper.UserMapper;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.security.core.authority.SimpleGrantedAuthority;import org.springframework.security.core.userdetails.UserDetails;import org.springframework.security.core.userdetails.UserDetailsService;import org.springframework.security.core.userdetails.UsernameNotFoundException;import org.springframework.stereotype.Service;import java.util.Arrays;import java.util.List;/** * 用户业务类 * @author momf * @date 2018-10-17 */@Slf4j@Servicepublic class UserService implements UserDetailsService {    @Autowired    UserMapper userMapper;    @Override    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {        User user = findByUsername(username);        if(user == null){            log.info("登录用户【"+username + "】不存在.");            throw new UsernameNotFoundException("登录用户【"+username + "】不存在.");        }        return new org.springframework.security.core.userdetails.User(                    user.getUsername(), user.getPassword(), getAuthority());    }    /**     * 根据登录查询用户信息     * @param username     */    public User findByUsername(String username){        UserExample userExample = new UserExample();        userExample.createCriteria().andUsernameEqualTo(username);        List<User> userlist = userMapper.selectByExample(userExample);        User user = userlist.size()>0?userlist.get(0):null;        return user;    }    /**     * 获取用户角色     * @return     */    private List getAuthority() {        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));    }}