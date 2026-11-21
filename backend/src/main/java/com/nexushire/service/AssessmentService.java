package com.nexushire.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexushire.dto.assessment.AssessmentAttemptResponse;
import com.nexushire.dto.assessment.AssessmentResponse;
import com.nexushire.dto.assessment.SubmitAssessmentRequest;
import com.nexushire.entity.Assessment;
import com.nexushire.entity.AssessmentAttempt;
import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.SeekerSkill;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.AssessmentAttemptRepository;
import com.nexushire.repository.AssessmentRepository;
import com.nexushire.repository.SeekerProfileRepository;
import com.nexushire.repository.SeekerSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentAttemptRepository attemptRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final SeekerSkillRepository seekerSkillRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<AssessmentResponse> listAll() {
        return assessmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public AssessmentAttemptResponse submit(SubmitAssessmentRequest request, User seeker) {
        Assessment assessment = assessmentRepository.findById(request.assessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        List<Map<String, Object>> questions = parseQuestions(assessment.getQuestionsJson());
        int correct = 0;
        for (int i = 0; i < questions.size() && i < request.answers().size(); i++) {
            Object expected = questions.get(i).get("correct");
            if (expected != null && request.answers().get(i).equals(((Number) expected).intValue())) {
                correct++;
            }
        }
        int score = questions.isEmpty() ? 0 : (int) Math.round(100.0 * correct / questions.size());
        boolean passed = score >= (assessment.getPassingScore() != null ? assessment.getPassingScore() : 60);
        AssessmentAttempt attempt = AssessmentAttempt.builder()
                .assessment(assessment)
                .seeker(seeker)
                .score(score)
                .passed(passed)
                .build();
        attempt = attemptRepository.save(attempt);

        if (passed) {
            seekerProfileRepository.findByUserId(seeker.getId()).ifPresent(profile ->
                    seekerSkillRepository.findBySeekerProfileIdAndSkillId(profile.getId(), assessment.getSkill().getId())
                            .ifPresent(ss -> {
                                ss.setVerified(true);
                                seekerSkillRepository.save(ss);
                            }));
        }
        return new AssessmentAttemptResponse(attempt.getId(), assessment.getId(), assessment.getTitle(),
                assessment.getSkill().getName(), score, passed, attempt.getAttemptedAt());
    }

    @Transactional(readOnly = true)
    public List<AssessmentAttemptResponse> myAttempts(User seeker) {
        return attemptRepository.findBySeekerIdOrderByAttemptedAtDesc(seeker.getId())
                .stream()
                .map(a -> new AssessmentAttemptResponse(a.getId(), a.getAssessment().getId(),
                        a.getAssessment().getTitle(), a.getAssessment().getSkill().getName(),
                        a.getScore(), a.isPassed(), a.getAttemptedAt()))
                .toList();
    }

    public AssessmentResponse toResponse(Assessment a) {
        return new AssessmentResponse(a.getId(), a.getSkill().getId(), a.getSkill().getName(),
                a.getTitle(), a.getDurationMinutes(), a.getPassingScore());
    }

    private List<Map<String, Object>> parseQuestions(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid assessment data");
        }
    }
}
