package com.example.hot6novelcraft.domain.admin.service;

import com.example.hot6novelcraft.common.exception.ServiceErrorException;
import com.example.hot6novelcraft.common.exception.domain.UserExceptionEnum;
import com.example.hot6novelcraft.domain.admin.dto.response.AdminResponse;
import com.example.hot6novelcraft.domain.user.entity.User;
import com.example.hot6novelcraft.domain.user.entity.enums.UserRole;
import com.example.hot6novelcraft.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "ADMIN")
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * 일반 관리자 승인 대기 목록 조회
     * 관리자 권한 변경
     * 관리자 승인/거절 처리
     * */

    // 관리자 승인 대기 목록 조회 (SUPER_ADMIN 전용)
    @Transactional(readOnly = true)
    public List<AdminResponse> getPendingAdmins() {
        return userRepository.findAllByRole(UserRole.PENDING_ADMIN).stream()
                .map(AdminResponse::from)
                .toList();
    }

    // 일반 관리자 승인 (SUPER_ADMIN 전용)
    public void approvePendingAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceErrorException(UserExceptionEnum.ERR_NOT_FOUND_USER));

        if(user.getRole() != UserRole.PENDING_ADMIN) {
            throw new ServiceErrorException(UserExceptionEnum.ERR_NOT_PENDING_ADMIN);
        }
        user.changeRole(UserRole.ADMIN);
    }

    // 일반 관리자 승인 거절 (SUPER_ADMIN 전용)
    public void rejectPendingAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceErrorException(UserExceptionEnum.ERR_NOT_FOUND_USER));

        if(user.getRole() != UserRole.PENDING_ADMIN) {
            throw new ServiceErrorException(UserExceptionEnum.ERR_NOT_POSSIBLE_TO_REFUSE);
        }
        // 거절 상태로 변경
        user.changeRole(UserRole.REJECTED_ADMIN);
    }
}
