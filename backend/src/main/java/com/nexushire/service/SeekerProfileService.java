package com.nexushire.service;

import com.nexushire.dto.common.PageResponse;
import com.nexushire.dto.recruiter.CandidateSearchCriteria;
import com.nexushire.dto.seeker.SeekerProfileRequest;
import com.nexushire.dto.seeker.SeekerProfileResponse;
import com.nexushire.dto.seeker.SeekerSkillItem;
import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.SeekerSkill;
import com.nexushire.entity.Skill;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.SeekerProfileRepository;
import com.nexushire.repository.SeekerSkillRepository;
import com.nexushire.repository.SkillRepository;
import com.nexushire.repository.spec.SeekerProfileSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeekerProfileService {

    private final SeekerProfileRepository seekerProfileRepository;
    private final SeekerSkillRepository seekerSkillRepository;
    private final SkillRepository skillRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public SeekerProfileResponse getMyProfile() {
        User user = userService.getCurrentUser();
        return toResponse(loadProfile(user.getId()));
    }

    @Transactional
    public SeekerProfileResponse updateMyProfile(SeekerProfileRequest request) {
        User user = userService.getCurrentUser();
        SeekerProfile profile = loadProfile(user.getId());
        profile.setHeadline(request.headline());
        profile.setSummary(request.summary());
        profile.setTotalExperienceYears(request.totalExperienceYears());
        profile.setCurrentLocation(request.currentLocation());
        profile.setPreferredLocations(request.preferredLocations());
        profile.setExpectedSalaryMin(request.expectedSalaryMin());
        profile.setExpectedSalaryMax(request.expectedSalaryMax());
        profile.setNoticePeriodDays(request.noticePeriodDays());
        seekerProfileRepository.save(profile);
        if (request.skills() != null) {
            seekerSkillRepository.deleteAll(seekerSkillRepository.findBySeekerProfileId(profile.getId()));
            for (SeekerProfileRequest.SkillItem item : request.skills()) {
                Skill skill = skillRepository.findById(item.skillId())
                        .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
                seekerSkillRepository.save(SeekerSkill.builder()
                        .seekerProfile(profile)
                        .skill(skill)
                        .proficiency(item.proficiency() != null ? item.proficiency()
                                : SeekerSkill.Proficiency.INTERMEDIATE)
                        .verified(false)
                        .build());
            }
        }
        return toResponse(profile);
    }

    @Transactional
    public SeekerProfileResponse uploadResume(MultipartFile file) {
        User user = userService.getCurrentUser();
        SeekerProfile profile = loadProfile(user.getId());
        String url = fileStorageService.storeResume(file, user.getId());
        profile.setResumeUrl(url);
        seekerProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Transactional
    public SeekerProfileResponse addSkill(Long skillId, SeekerSkill.Proficiency proficiency) {
        User user = userService.getCurrentUser();
        SeekerProfile profile = loadProfile(user.getId());
        if (seekerSkillRepository.existsBySeekerProfileIdAndSkillId(profile.getId(), skillId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Skill already added");
        }
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        seekerSkillRepository.save(SeekerSkill.builder()
                .seekerProfile(profile)
                .skill(skill)
                .proficiency(proficiency != null ? proficiency : SeekerSkill.Proficiency.INTERMEDIATE)
                .verified(false)
                .build());
        return toResponse(profile);
    }

    @Transactional
    public SeekerProfileResponse removeSkill(Long skillId) {
        User user = userService.getCurrentUser();
        SeekerProfile profile = loadProfile(user.getId());
        seekerSkillRepository.deleteBySeekerProfileIdAndSkillId(profile.getId(), skillId);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public PageResponse<SeekerProfileResponse> searchCandidates(CandidateSearchCriteria criteria, Pageable pageable) {
        Page<SeekerProfile> page = seekerProfileRepository.findAll(
                SeekerProfileSpecification.search(criteria.skillIds(), criteria.minExperience(),
                        criteria.location(), criteria.minSalary(), criteria.keyword()),
                pageable);
        return PageResponse.of(page, this::toResponse);
    }

    @Transactional(readOnly = true)
    public SeekerProfile loadByUserId(Long userId) {
        return seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile missing"));
    }

    private SeekerProfile loadProfile(Long userId) {
        return seekerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seeker profile missing"));
    }

    public SeekerProfileResponse toResponse(SeekerProfile profile) {
        List<SeekerSkillItem> skills = seekerSkillRepository.findBySeekerProfileId(profile.getId())
                .stream()
                .map(ss -> new SeekerSkillItem(
                        ss.getSkill().getId(),
                        ss.getSkill().getName(),
                        ss.getProficiency(),
                        ss.isVerified()))
                .toList();
        User u = profile.getUser();
        return new SeekerProfileResponse(
                profile.getId(),
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                profile.getHeadline(),
                profile.getSummary(),
                profile.getTotalExperienceYears(),
                profile.getCurrentLocation(),
                profile.getPreferredLocations(),
                profile.getExpectedSalaryMin(),
                profile.getExpectedSalaryMax(),
                profile.getNoticePeriodDays(),
                profile.getResumeUrl(),
                profile.getVideoProfileUrl(),
                skills);
    }
}
