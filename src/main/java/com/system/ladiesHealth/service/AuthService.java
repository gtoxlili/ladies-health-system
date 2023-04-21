package com.system.ladiesHealth.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.system.ladiesHealth.constants.LoginTypeEnum;
import com.system.ladiesHealth.constants.RoleEnum;
import com.system.ladiesHealth.dao.UserRepository;
import com.system.ladiesHealth.dao.specification.UserSpecification;
import com.system.ladiesHealth.domain.dto.UserSubmitDTO;
import com.system.ladiesHealth.domain.po.UserPO;
import com.system.ladiesHealth.domain.vo.OperateVO;
import com.system.ladiesHealth.domain.vo.UserDetailVO;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.exception.BusinessException;
import com.system.ladiesHealth.utils.JwtUtil;
import com.system.ladiesHealth.utils.RollbackUtil;
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
    private RollbackUtil rollbackUtil;

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
        String token;
        if (userDTO.getRememberMe()) {
            token = jwtUtil.generateToken(userPO.getId(), userPO.getRole(), 7 * 24 * 60 * 60);
        } else {
            token = jwtUtil.generateToken(userPO.getId(), userPO.getRole());
        }

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

        // 检测数据库是否已经存在
        UserSpecification specification = new UserSpecification(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPhone());
        if (userRepository.exists(specification)) {
            throw new BusinessException("User already exists");
        }

        userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        UserPO userPO = userRepository.save(authConvert.generateUserPOByUserSubmitDTO(userDTO, role));

        // 注册成功后，直接登录
        // 生成 token
        String token = jwtUtil.generateToken(userPO.getId(), role);

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
     * 更新用户信息逻辑
     */
    public Res<OperateVO> updateInfo(String userID, UserSubmitDTO userDTO) {
        UserPO userPO = userRepository.findById(userID).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userID: " + userID)
        );

        // 保存旧数据
        UserPO oldUserPO = ObjUtil.clone(userPO);

        if (userDTO.getPassword() != null) {
            userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        }
        authConvert.updateUserPOByUserSubmitDTO(userDTO, userPO);
        userRepository.save(userPO);

        return Res.ok(
                rollbackUtil.builder("更新用户信息", () -> userRepository.save(oldUserPO))
        );
    }

    /**
     * Admin 更新用户信息逻辑
     */
    public Res<OperateVO> updateInfo(UserSubmitDTO userDTO) {
        UserPO userPO = userRepository.findByUsernameAndDelTimeIsNull(userDTO.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userName: " + userDTO.getUsername())
        );
        if (userPO.getRole() == RoleEnum.ROLE_ADMIN) {
            String id = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!id.equals(userPO.getId())) {
                throw new BusinessException("Can not modify other admins information");
            }
        }
        return this.updateInfo(userPO.getId(), userDTO);
    }

    /**
     * 注销用户逻辑
     */
    public Res<OperateVO> delete(String id) {
        UserPO userPO = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with userID: " + id)
        );
        // 设置为当前时间戳
        userPO.setDelTime(DateUtil.date());
        userRepository.save(userPO);
        this.logout();

        return Res.ok(
                rollbackUtil.builder("注销用户", () -> {
                    userPO.setDelTime(null);
                    userRepository.save(userPO);
                })
        );
    }

    /**
     * 批量注销用户逻辑
     */
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN')")
    public Res<OperateVO> delete(List<String> usernames) {
        List<UserPO> userPOs = userRepository.findAllByUsernameInAndDelTimeIsNull(usernames);
        DateTime now = DateUtil.date();
        userPOs.forEach(userPO -> userPO.setDelTime(now));
        userRepository.saveAll(userPOs);

        return Res.ok(
                rollbackUtil.builder("批量注销用户", () -> {
                    userPOs.forEach(userPO -> userPO.setDelTime(null));
                    userRepository.saveAll(userPOs);
                })
        );
    }

}
