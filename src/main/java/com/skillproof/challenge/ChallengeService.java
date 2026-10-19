package com.skillproof.challenge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.security.SecurityUtils;
import com.skillproof.skill.*;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeService.class);

    private final ChallengeRepository challengeRepository;
    private final ChallengePrerequisiteRepository prerequisiteRepository;
    private final UserSkillRepository userSkillRepository;
    private final SubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;

    public ChallengeService(ChallengeRepository challengeRepository,
                            ChallengePrerequisiteRepository prerequisiteRepository,
                            UserSkillRepository userSkillRepository,
                            SubmissionRepository submissionRepository,
                            ObjectMapper objectMapper) {
        this.challengeRepository = challengeRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.userSkillRepository = userSkillRepository;
        this.submissionRepository = submissionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Page<ChallengeSummaryDto> searchChallenges(Long skillId, Difficulty difficulty, String search, Pageable pageable) {
        Page<Challenge> page = challengeRepository.searchChallenges(skillId, difficulty, search, pageable);

        Long currentUserId = SecurityUtils.getCurrentUserPrincipal().map(p -> p.getId()).orElse(null);

        Set<Long> completedSkillIds = new HashSet<>();
        if (currentUserId != null) {
            userSkillRepository.findByUserId(currentUserId).forEach(us -> {
                if (us.getStatus() == UserSkillStatus.COMPLETED || us.getStatus() == UserSkillStatus.VERIFIED) {
                    completedSkillIds.add(us.getSkill().getId());
                }
            });
        }

        return page.map(ch -> {
            ChallengeSummaryDto dto = new ChallengeSummaryDto();
            dto.setId(ch.getId());
            dto.setSlug(ch.getSlug());
            dto.setSkillId(ch.getSkill().getId());
            dto.setSkillName(ch.getSkill().getName());
            dto.setTitle(ch.getTitle());
            dto.setDifficulty(ch.getDifficulty());
            dto.setPoints(ch.getPoints());
            dto.setEstimatedMinutes(ch.getEstimatedMinutes());
            dto.setSummary(ch.getSummary());

            if (currentUserId != null) {
                // Check challenge prerequisites
                List<Skill> prereqs = prerequisiteRepository.findPrerequisiteSkillsForChallenge(ch.getId());
                boolean eligible = prereqs.isEmpty() || prereqs.stream().allMatch(p -> completedSkillIds.contains(p.getId()));
                dto.setEligible(eligible);

                // Latest submission status
                submissionRepository.findTopByUserIdAndChallengeIdOrderByCreatedAtDesc(currentUserId, ch.getId())
                        .ifPresent(sub -> dto.setLatestSubmissionStatus(sub.getStatus()));
            } else {
                dto.setEligible(true);
            }
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public ChallengeDetailDto getChallengeDetails(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with ID: " + challengeId));

        return buildChallengeDetailDto(challenge);
    }

    @Transactional(readOnly = true)
    public ChallengeDetailDto getChallengeBySlug(String slug) {
        Challenge challenge = challengeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with slug: " + slug));

        return buildChallengeDetailDto(challenge);
    }

    private ChallengeDetailDto buildChallengeDetailDto(Challenge challenge) {
        Long currentUserId = SecurityUtils.getCurrentUserPrincipal().map(p -> p.getId()).orElse(null);

        ChallengeDetailDto dto = new ChallengeDetailDto();
        dto.setId(challenge.getId());
        dto.setSlug(challenge.getSlug());
        dto.setSkillId(challenge.getSkill().getId());
        dto.setSkillName(challenge.getSkill().getName());
        dto.setSkillCategory(challenge.getSkill().getCategory());
        dto.setTitle(challenge.getTitle());
        dto.setDifficulty(challenge.getDifficulty());
        dto.setPoints(challenge.getPoints());
        dto.setEstimatedMinutes(challenge.getEstimatedMinutes());
        dto.setSummary(challenge.getSummary());
        dto.setDescription(challenge.getDescription());
        dto.setInstructions(challenge.getInstructions());
        dto.setSubmissionRequirements(challenge.getSubmissionRequirements());
        dto.setSampleInputOutput(challenge.getSampleInputOutput());

        // Parse Rubric JSON
        try {
            List<RubricCriterionDto> rubric = objectMapper.readValue(challenge.getRubricJson(), new TypeReference<List<RubricCriterionDto>>() {});
            dto.setRubric(rubric);
        } catch (Exception ex) {
            logger.warn("Could not deserialize rubric JSON for challenge {}: {}", challenge.getId(), ex.getMessage());
        }

        // Check prerequisites
        List<Skill> prereqs = prerequisiteRepository.findPrerequisiteSkillsForChallenge(challenge.getId());
        Set<Long> userCompletedSkills = new HashSet<>();

        if (currentUserId != null) {
            userSkillRepository.findByUserId(currentUserId).forEach(us -> {
                if (us.getStatus() == UserSkillStatus.COMPLETED || us.getStatus() == UserSkillStatus.VERIFIED) {
                    userCompletedSkills.add(us.getSkill().getId());
                }
            });

            Optional<Submission> latestSub = submissionRepository.findTopByUserIdAndChallengeIdOrderByCreatedAtDesc(currentUserId, challenge.getId());
            latestSub.ifPresent(sub -> {
                dto.setLatestSubmissionStatus(sub.getStatus());
                dto.setLatestSubmissionId(sub.getId());
            });

            boolean eligible = prereqs.isEmpty() || prereqs.stream().allMatch(p -> userCompletedSkills.contains(p.getId()));
            dto.setEligible(eligible);
        } else {
            dto.setEligible(true);
        }

        List<SkillPrerequisiteDto> prereqDtos = prereqs.stream()
                .map(p -> new SkillPrerequisiteDto(p.getId(), p.getSlug(), p.getName(), p.getDifficulty(), userCompletedSkills.contains(p.getId())))
                .toList();
        dto.setPrerequisiteSkills(prereqDtos);

        return dto;
    }
}
