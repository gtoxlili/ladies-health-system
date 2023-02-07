package com.system.ladiesHealth.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.system.ladiesHealth.constants.LoginTypeEnum;
import com.system.ladiesHealth.constants.RoleEnum;
import com.system.ladiesHealth.dao.UserRepository;
import com.system.ladiesHealth.domain.dto.UserSubmitDTO;
import com.system.ladiesHealth.domain.po.UserPO;
import com.system.ladiesHealth.domain.pojo.RollbackPOJO;
import com.system.ladiesHealth.domain.vo.OperateVO;
import com.system.ladiesHealth.domain.vo.UserDetailVO;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.utils.JwtUtil;
import com.system.ladiesHealth.utils.convert.AuthConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthConvert authConvert;

    @Autowired
    private Cache<String, RollbackPOJO> rollbackCache;

    /**
     * 处理登录逻辑
     */
    public ResponseEntity<Res<UserDetailVO>> login(UserSubmitDTO userDTO, LoginTypeEnum loginType) {

        // 根据登录类型获取用户信息
        String tag = loginType.apply(userDTO);
        if (StrUtil.isBlank(tag)) {
            throw new UsernameNotFoundException(loginType.name() + " can not be empty");
        }

        UserPO userPO = loginType.findMethod.apply(userRepository).apply(tag)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with " + loginType.name().toLowerCase() + ": " + tag
                ));

        if (!bCryptPasswordEncoder.matches(userDTO.getPassword(), userPO.getPassword())) {
            throw new BadCredentialsException("Password is not correct with " + loginType.name().toLowerCase() + ": " + tag);
        }
        // 生成 token
        String token = jwtUtil.generateToken(userPO.getUsername(), userPO.getRole());

        // 认证成功后，将认证信息放入 Spring Security 上下文
        Authentication authentication = jwtUtil.parseAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 将 Token 放入 Header 中
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtUtil.getTokenHeader(), token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(Res.ok(authConvert.generateUserDetailVOByUserPO(userPO)));
    }

    /**
     * 处理登出逻辑
     */
    public Res<Void> logout() {
        SecurityContextHolder.clearContext();
        return Res.ok();
    }

    /**
     * 处理注册逻辑
     */
    public ResponseEntity<Res<UserDetailVO>> register(UserSubmitDTO userDTO, RoleEnum role) {
        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        userRepository.save(authConvert.generateUserPOByUserSubmitDTO(userDTO, role));

        // 注册成功后，直接登录
        // 生成 token
        String token = jwtUtil.generateToken(userDTO.getUsername(), role);

        // 认证成功后，将认证信息放入 Spring Security 上下文
        Authentication authentication = jwtUtil.parseAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 将 Token 放入 Header 中
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtUtil.getTokenHeader(), token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(Res.ok(authConvert.generateUserDetailVOByUserSubmitDTO(userDTO, role)));
    }

    /**
     * 更新用户信息逻辑
     */
    public Res<OperateVO> updateInfo(UserSubmitDTO userDTO) {
        if (userDTO.getPassword() != null) {
            userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        }
        UserPO userPO = userRepository.findByUsername(userDTO.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userName: " + userDTO.getUsername())
        );
        // 保存旧数据
        UserPO oldUserPO = ObjUtil.clone(userPO);

        authConvert.updateUserPOByUserSubmitDTO(userDTO, userPO);
        userRepository.save(userPO);

        String nanoid = NanoIdUtils.randomNanoId();
        String actionName = "更新用户信息";
        rollbackCache.put(nanoid,
                RollbackPOJO
                        .builder()
                        .action(actionName)
                        .rollback(() -> userRepository.save(oldUserPO))
                        .build()
        );
        return Res.ok(
                OperateVO.builder()
                        .action(actionName)
                        .rollbackUrl("/rollback/" + nanoid)
                        .build()
        );
    }

    /**
     * 注销用户逻辑
     */
    public Res<OperateVO> delete(String username) {
        UserPO userPO = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userName: " + username)
        );
        // 设置为当前时间戳
        userPO.setDelFlag(DateUtil.currentSeconds());
        userRepository.save(userPO);
        this.logout();

        String nanoid = NanoIdUtils.randomNanoId();
        String actionName = "注销用户";
        rollbackCache.put(nanoid,
                RollbackPOJO
                        .builder()
                        .action(actionName)
                        .rollback(() -> {
                            userPO.setDelFlag(0L);
                            userRepository.save(userPO);
                        })
                        .build()
        );

        return Res.ok(
                OperateVO.builder()
                        .action("注销用户")
                        .rollbackUrl("/rollback/" + nanoid)
                        .build()
        );
    }

    /**
     * 批量注销用户逻辑
     */
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN')")
    public Res<OperateVO> delete(List<String> usernames) {
        List<UserPO> userPOs = userRepository.findAllByUsernameIn(usernames);
        Long currentSeconds = DateUtil.currentSeconds();
        userPOs.forEach(userPO -> userPO.setDelFlag(currentSeconds));
        userRepository.saveAll(userPOs);

        String nanoid = NanoIdUtils.randomNanoId();
        String actionName = "批量注销用户";
        rollbackCache.put(nanoid,
                RollbackPOJO
                        .builder()
                        .action(actionName)
                        .rollback(() -> {
                            userPOs.forEach(userPO -> userPO.setDelFlag(0L));
                            userRepository.saveAll(userPOs);
                        })
                        .build()
        );

        return Res.ok(
                OperateVO.builder()
                        .action("批量注销用户")
                        .rollbackUrl("/rollback/" + nanoid)
                        .build()
        );
    }

}