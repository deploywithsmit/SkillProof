package com.skillproof.skill;

import com.skillproof.common.BadRequestException;
import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.security.SecurityUtils;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillPrerequisiteRepository prerequisiteRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillDependencyEngine dependencyEngine;

    public SkillService(SkillRepository skillRepository,
                        SkillPrerequisiteRepository prerequisiteRepository,
                        UserSkillRepository userSkillRepository,
                        UserRepository userRepository,
                        SkillDependencyEngine dependencyEngine) {
        this.skillRepository = skillRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.dependencyEngine = dependencyEngine;
    }

    @Transactional(readOnly = true)
    public Page<SkillSummaryDto> searchSkills(String category, Difficulty difficulty, String search, Pageable pageable) {
        Page<Skill> page = skillRepository.searchSkills(
                category != null && !category.isBlank() ? category : null,
                difficulty,
                search != null && !search.isBlank() ? search : null,
                pageable
        );

        Long currentUserId = SecurityUtils.getCurrentUserPrincipal().map(p -> p.getId()).orElse(null);

        Map<Long, UserSkill> userSkillMap = new HashMap<>();
        if (currentUserId != null) {
            userSkillRepository.findByUserId(currentUserId)
                    .forEach(us -> userSkillMap.put(us.getSkill().getId(), us));
        }

        return page.map(skill -> {
            UserSkill us = userSkillMap.get(skill.getId());
            boolean eligible = currentUserId == null || dependencyEngine.arePrerequisitesMet(currentUserId, skill.getId());
            int prereqCount = prerequisiteRepository.findBySkillId(skill.getId()).size();
            return new SkillSummaryDto(skill, us, eligible, prereqCount);
        });
    }

    @Transactional(readOnly = true)
    public SkillDetailDto getSkillDetails(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        return buildSkillDetailDto(skill);
    }

    @Transactional(readOnly = true)
    public SkillDetailDto getSkillBySlug(String slug) {
        Skill skill = skillRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with slug: " + slug));

        return buildSkillDetailDto(skill);
    }

    private SkillDetailDto buildSkillDetailDto(Skill skill) {
        Long currentUserId = SecurityUtils.getCurrentUserPrincipal().map(p -> p.getId()).orElse(null);

        SkillDetailDto dto = new SkillDetailDto();
        dto.setId(skill.getId());
        dto.setSlug(skill.getSlug());
        dto.setName(skill.getName());
        dto.setCategory(skill.getCategory());
        dto.setDifficulty(skill.getDifficulty());
        dto.setDescription(skill.getDescription());
        dto.setLearningObjectives(skill.getLearningObjectives());
        dto.setEstimatedHours(skill.getEstimatedHours());
        dto.setIconName(skill.getIconName());

        Set<Long> userCompletedSkills = new HashSet<>();
        if (currentUserId != null) {
            userSkillRepository.findByUserIdAndSkillId(currentUserId, skill.getId()).ifPresent(us -> {
                dto.setUserStatus(us.getStatus());
                dto.setProgressPercent(us.getProgressPercent());
            });

            userSkillRepository.findByUserId(currentUserId).forEach(us -> {
                if (us.getStatus() == UserSkillStatus.COMPLETED || us.getStatus() == UserSkillStatus.VERIFIED) {
                    userCompletedSkills.add(us.getSkill().getId());
                }
            });
            dto.setEligible(dependencyEngine.arePrerequisitesMet(currentUserId, skill.getId()));
        } else {
            dto.setEligible(true);
        }

        // Prerequisites
        List<Skill> prereqs = prerequisiteRepository.findPrerequisitesForSkill(skill.getId());
        List<SkillPrerequisiteDto> prereqDtos = new ArrayList<>();
        for (Skill p : prereqs) {
            boolean completed = userCompletedSkills.contains(p.getId());
            prereqDtos.add(new SkillPrerequisiteDto(p.getId(), p.getSlug(), p.getName(), p.getDifficulty(), completed));
        }
        dto.setPrerequisites(prereqDtos);

        // Dependent skills unlocked by this skill
        List<Skill> dependents = prerequisiteRepository.findDependentSkillsForSkill(skill.getId());
        List<SkillPrerequisiteDto> depDtos = new ArrayList<>();
        for (Skill d : dependents) {
            boolean completed = userCompletedSkills.contains(d.getId());
            depDtos.add(new SkillPrerequisiteDto(d.getId(), d.getSlug(), d.getName(), d.getDifficulty(), completed));
        }
        dto.setUnlockedSkills(depDtos);

        return dto;
    }

    @Transactional
    public UserSkill startPursuingSkill(Long skillId) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));

        if (!dependencyEngine.arePrerequisitesMet(userId, skillId)) {
            throw new BadRequestException("You have not completed the required prerequisite skills for " + skill.getName() + ".");
        }

        return userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseGet(() -> userSkillRepository.save(new UserSkill(user, skill, UserSkillStatus.IN_PROGRESS, 10)));
    }

    @Transactional(readOnly = true)
    public List<SkillRecommendationDto> getRecommendations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Skill> allSkills = skillRepository.findAll();
        List<SkillDependencyEngine.SkillRecommendation> recs = dependencyEngine.generateRecommendations(userId, allSkills);

        return recs.stream()
                .map(SkillRecommendationDto::new)
                .limit(6)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return skillRepository.findAllCategories();
    }

    @Transactional
    public void addPrerequisite(Long skillId, Long prerequisiteId) {
        if (skillId.equals(prerequisiteId)) {
            throw new BadRequestException("A skill cannot be a prerequisite of itself.");
        }

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        Skill prereq = skillRepository.findById(prerequisiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Prerequisite skill not found with ID: " + prerequisiteId));

        if (prerequisiteRepository.existsBySkillIdAndPrerequisiteId(skillId, prerequisiteId)) {
            throw new BadRequestException("This prerequisite relationship already exists.");
        }

        // Cycle check
        if (dependencyEngine.wouldCreateCycle(skillId, prerequisiteId)) {
            throw new BadRequestException("Cycle detected! Adding this prerequisite would create a circular dependency loop.");
        }

        prerequisiteRepository.save(new SkillPrerequisite(skill, prereq));
    }
}
