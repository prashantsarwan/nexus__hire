package com.nexushire.service;

import com.nexushire.dto.recruiter.RecruiterProfileResponse;
import com.nexushire.entity.RecruiterProfile;
import com.nexushire.entity.User;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyService companyService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getMyProfile() {
        User user = userService.getCurrentUser();
        return toResponse(loadProfile(user.getId()));
    }

    @Transactional(readOnly = true)
    public RecruiterProfile loadProfile(Long userId) {
        return recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile missing"));
    }

    public RecruiterProfileResponse toResponse(RecruiterProfile profile) {
        User u = profile.getUser();
        return new RecruiterProfileResponse(
                profile.getId(),
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                profile.getDesignation(),
                profile.isCompanyAdmin(),
                companyService.toResponse(profile.getCompany()));
    }
}
