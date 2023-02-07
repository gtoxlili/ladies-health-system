package com.system.ladiesHealth.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.system.ladiesHealth.constants.RoleEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Slf4j
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {
    private final String tokenHeader;
    private final String tokenPrefix;
    private final String alg;
    private final String secret;
    private final Integer refreshExpire;

    @Getter(AccessLevel.NONE)
    private final JWTSigner signer;

    @Getter(AccessLevel.NONE)
    private final Cache<String, Authentication> caffeine;

    private JwtUtil(
            String tokenHeader,
            String tokenPrefix,
            String alg,
            String secret,
            Integer refreshExpire
    ) {
        this.tokenHeader = tokenHeader;
        this.tokenPrefix = tokenPrefix;
        this.alg = alg;
        this.secret = secret;
        this.refreshExpire = refreshExpire;
        String upperAlg = alg.toUpperCase();
        signer = JWTSignerUtil.createSigner(upperAlg, KeyUtil.generateKey(upperAlg, StrUtil.bytes(secret)));
        caffeine = Caffeine.newBuilder()
                .initialCapacity(16)
                .maximumSize(128)
                .expireAfterWrite(Duration.ofDays(1))
                .build();

    }

    /**
     * 生成token
     *
     * @param aud 身份, sub 用户名
     * @return token
     */
    public String generateToken(String sub, RoleEnum aud) {
        Date date = DateUtil.date();
        return tokenPrefix + " " +
                JWT.create()
                        .setSubject(sub)
                        .setAudience(aud.name())
                        .setIssuedAt(date)
                        .setExpiresAt(DateUtil.offsetSecond(date, refreshExpire))
                        .setSigner(signer)
                        .sign();
    }

    /**
     * 验证token
     *
     * @param token token
     * @throws ValidateException 验证异常
     */
    public void verifyToken(String token) throws ValidateException {
        // 如果缓存中存在则不验证
        if (caffeine.getIfPresent(token) != null) return;

        String tokenVal = token.substring(tokenPrefix.length() + 1);
        JWTValidator validator = JWTValidator.of(tokenVal);
        validator.validateAlgorithm(signer);
        try {
            validator.validateDate();
        } catch (ValidateException e) {
            throw new AccountExpiredException(e.getMessage());
        }
    }

    /**
     * 根据 token 获取用户认证信息
     *
     * @param token token
     * @return Authentication
     */
    public Authentication parseAuthentication(String token) {
        // 如果缓存中存在则直接返回
        Authentication authentication = caffeine.getIfPresent(token);
        if (authentication != null) return authentication;

        JWT jwt = JWTUtil.parseToken(token.substring(tokenPrefix.length() + 1));
        String sub = (String) jwt.getPayload(JWT.SUBJECT);
        List<String> aud = (List<String>) jwt.getPayload(JWT.AUDIENCE);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(aud.get(0)));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(sub, token, authorities);

        caffeine.put(token, authenticationToken);

        return authenticationToken;
    }
}
