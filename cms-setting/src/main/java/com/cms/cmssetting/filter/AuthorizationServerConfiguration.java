package com.cms.cmssetting.filter;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.http.HttpMethod;import org.springframework.security.authentication.AuthenticationManager;import org.springframework.security.core.userdetails.UserDetailsService;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;import org.springframework.security.oauth2.common.OAuth2AccessToken;import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;import org.springframework.security.oauth2.provider.OAuth2Authentication;import org.springframework.security.oauth2.provider.token.DefaultTokenServices;import org.springframework.security.oauth2.provider.token.TokenStore;import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;import java.util.HashMap;import java.util.Map;import java.util.concurrent.TimeUnit;/*** *  身份授权认证服务配置 *  配置客户端、token存储方式等 *  注解开启验证服务器 提供/oauth/authorize,/oauth/token,/oauth/check_token,/oauth/confirm_access,/oauth/error * @author momf * @date 2018-10-17 */@Configuration@EnableAuthorizationServerpublic class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {    /**     * 重定向地址     */    private static final String REDIRECT_URL = "https://www.baidu.com/";    /**     * 客户端     */    private static final String CLIEN_ID_THREE = "client";    /**     * secret客户端安全码     */    private static final String CLIENT_SECRET = "secret";    /**     * 密码模式授权模式     */    private static final String GRANT_TYPE_PASSWORD = "password";    /**     * 授权码模式  授权码模式使用到了回调地址，是最为复杂的方式，通常网站中经常出现的微博，qq第三方登录，都会采用这个形式。     */    private static final String AUTHORIZATION_CODE = "authorization_code";    private static final String REFRESH_TOKEN = "refresh_token";    /**     * 简化授权模式     */    private static final String IMPLICIT = "implicit";    /**     * 客户端模式     */    private static final String GRANT_TYPE = "client_credentials";    private static final String SCOPE_READ = "read";    private static final String SCOPE_WRITE = "write";    private static final String TRUST = "trust";    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;    private static final int FREFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;    /**     * 指定哪些资源是需要授权验证的     */    private static final String RESOURCE_ID = "resource_id";    /**     * 认证方式     */    @Autowired    private AuthenticationManager authenticationManager;    @Autowired    private UserDetailsService userDetailsService;    @Override    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {        // 用 BCrypt 对密码编码        String secret = new BCryptPasswordEncoder().encode(CLIENT_SECRET);        //配置3个认证 password认证、client认证、authorization_code认证；使用in-memory存储        configurer.inMemory()                .withClient(CLIEN_ID_THREE)                .resourceIds(RESOURCE_ID)                //允许授权类型                .authorizedGrantTypes(AUTHORIZATION_CODE,GRANT_TYPE, REFRESH_TOKEN,GRANT_TYPE_PASSWORD,IMPLICIT)                //允许授权范围                .scopes(SCOPE_READ,SCOPE_WRITE,TRUST)                //客户端可以使用的权限                .authorities("ROLE_CLIENT")                //secret客户端安全码                .secret(secret)                //指定可以接受令牌和授权码的重定向URIs                .redirectUris(REDIRECT_URL)                // 为true 则不会被重定向到授权的页面，也不需要手动给请求授权,直接自动授权成功返回code                .autoApprove(true)                //token 时间秒                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)                //刷新token 时间 秒                .refreshTokenValiditySeconds(FREFRESH_TOKEN_VALIDITY_SECONDS);    }    @Override    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {        endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)                .accessTokenConverter(accessTokenConverter())                //必须注入userDetailsService否则根据refresh_token无法加载用户信息                .userDetailsService(userDetailsService)                //支持GET  POST  请求获取token                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST,HttpMethod.OPTIONS)                //开启刷新token                .reuseRefreshTokens(true)                .tokenServices(tokenServices());    }    /**     * 认证服务器的安全配置     *     * @param security     * @throws Exception     */    @Override    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {        security                .realm(RESOURCE_ID)                .tokenKeyAccess("permitAll()")                //isAuthenticated():排除anonymous   isFullyAuthenticated():排除anonymous以及remember-me                .checkTokenAccess("isAuthenticated()")                //允许表单认证  这段代码在授权码模式下会导致无法根据code　获取token　                .allowFormAuthenticationForClients();    }    @Bean    public JwtAccessTokenConverter accessTokenConverter() {        JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {            /**             * 自定义一些token返回的信息             * @param accessToken             * @param authentication             * @return             */            @Override            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {                String grantType = authentication.getOAuth2Request().getGrantType();                //只有如下两种模式才能获取到当前用户信息                if(AUTHORIZATION_CODE.equals(grantType) || GRANT_TYPE_PASSWORD.equals(grantType)) {                    String userName = authentication.getUserAuthentication().getName();                    // 自定义一些token 信息 会在获取token返回结果中展示出来                    final Map<String, Object> additionalInformation = new HashMap<>();                    additionalInformation.put("user_name", userName);                    ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);                }                OAuth2AccessToken token = super.enhance(accessToken, authentication);                return token;            }        };        converter.setSigningKey("bcrypt");        return converter;    }    @Bean    public TokenStore tokenStore() {        //基于jwt实现令牌（Access Token）        return new JwtTokenStore(accessTokenConverter());    }    /**     * 重写默认的资源服务token     * @return     */    @Bean    public DefaultTokenServices tokenServices() {        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();        defaultTokenServices.setTokenEnhancer(accessTokenConverter());        defaultTokenServices.setTokenStore(tokenStore());        defaultTokenServices.setSupportRefreshToken(true);        // 30天        defaultTokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));        return defaultTokenServices;    }}