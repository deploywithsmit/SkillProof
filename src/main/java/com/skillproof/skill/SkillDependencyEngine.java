package com.skillproof.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SkillDependencyEngine {

    private static final Logger logger = LoggerFactory.getLogger(SkillDependencyEngine.class);

    private final SkillPrerequisiteRepository prerequisiteRepository;
    private final UserSkillRepository userSkillRepository;

    public SkillDependencyEngine(SkillPrerequisiteRepository prerequisiteRepository,
                                 UserSkillRepository userSkillRepository) {
        this.prerequisiteRepository = prerequisiteRepository;
        this.userSkillRepository = userSkillRepository;
    }

    /**
     * Determines whether all prerequisite skills for a target skill have been completed
     * or verified by the specified user.
     */
    public boolean arePrerequisitesMet(Long userId, Long skillId) {
        List<Skill> prerequisites = prerequisiteRepository.findPrerequisitesForSkill(skillId);
        if (prerequisites.isEmpty()) {
            return true;
        }

        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        Set<Long> completedSkillIds = new HashSet<>();
        for (UserSkill us : userSkills) {
            if (us.getStatus() == UserSkillStatus.COMPLETED || us.getStatus() == UserSkillStatus.VERIFIED) {
                completedSkillIds.add(us.getSkill().getId());
            }
        }

        for (Skill req : prerequisites) {
            if (!completedSkillIds.contains(req.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Detects if adding a directed prerequisite edge (skillId -> prerequisiteId)
     * would introduce a cycle in the skill dependency graph.
     * Uses Depth-First Search (DFS) with visited state tracking.
     */
    public boolean wouldCreateCycle(Long skillId, Long proposedPrerequisiteId) {
        if (Objects.equals(skillId, proposedPrerequisiteId)) {
            return true;
        }

        // Build adjacency graph: prerequisite -> skills that depend on it
        List<SkillPrerequisite> allEdges = prerequisiteRepository.findAll();
        Map<Long, List<Long>> adj = new HashMap<>();

        for (SkillPrerequisite edge : allEdges) {
            adj.computeIfAbsent(edge.getPrerequisite().getId(), k -> new ArrayList<>())
               .add(edge.getSkill().getId());
        }

        // Add the proposed new edge: proposedPrerequisiteId -> skillId
        adj.computeIfAbsent(proposedPrerequisiteId, k -> new ArrayList<>()).add(skillId);

        // Check if proposedPrerequisiteId is reachable from skillId
        Set<Long> visited = new HashSet<>();
        return dfsCycleCheck(skillId, proposedPrerequisiteId, adj, visited);
    }

    private boolean dfsCycleCheck(Long current, Long target, Map<Long, List<Long>> adj, Set<Long> visited) {
        if (current.equals(target)) {
            return true;
        }
        visited.add(current);

        List<Long> neighbors = adj.getOrDefault(current, Collections.emptyList());
        for (Long neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (dfsCycleCheck(neighbor, target, adj, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Explainable rule-based recommendation for a student based on:
     * 1. Prerequisites completed
     * 2. Skills not yet pursued or in progress
     * 3. Explains clearly why the skill is recommended
     */
    public List<SkillRecommendation> generateRecommendations(Long userId, List<Skill> allSkills) {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        Map<Long, UserSkill> userSkillMap = new HashMap<>();
        Set<Long> completedOrVerifiedIds = new HashSet<>();

        for (UserSkill us : userSkills) {
            userSkillMap.put(us.getSkill().getId(), us);
            if (us.getStatus() == UserSkillStatus.COMPLETED || us.getStatus() == UserSkillStatus.VERIFIED) {
                completedOrVerifiedIds.add(us.getSkill().getId());
            }
        }

        List<SkillRecommendation> recommendations = new ArrayList<>();

        for (Skill skill : allSkills) {
            // If already verified or completed, do not recommend
            if (completedOrVerifiedIds.contains(skill.getId())) {
                continue;
            }

            List<Skill> prerequisites = prerequisiteRepository.findPrerequisitesForSkill(skill.getId());
            boolean allMet = true;
            List<String> satisfiedNames = new ArrayList<>();
            List<String> missingNames = new ArrayList<>();

            for (Skill req : prerequisites) {
                if (completedOrVerifiedIds.contains(req.getId())) {
                    satisfiedNames.add(req.getName());
                } else {
                    allMet = false;
                    missingNames.add(req.getName());
                }
            }

            if (allMet) {
                String reason;
                int priority;
                UserSkill currentStatus = userSkillMap.get(skill.getId());

                if (currentStatus != null && currentStatus.getStatus() == UserSkillStatus.IN_PROGRESS) {
                    reason = "Currently in progress (" + currentStatus.getProgressPercent() + "% completed). Continue your practical verification.";
                    priority = 100 - currentStatus.getProgressPercent();
                } else if (!satisfiedNames.isEmpty()) {
                    reason = "Recommended because you have already completed prerequisites: " + String.join(", ", satisfiedNames) + ".";
                    priority = 80;
                } else {
                    reason = "Foundational " + skill.getDifficulty().name().toLowerCase() + " competency with no remaining prerequisites.";
                    priority = skill.getDifficulty() == Difficulty.BEGINNER ? 90 : 70;
                }

                recommendations.add(new SkillRecommendation(skill, reason, true, Collections.emptyList(), priority));
            } else {
                String reason = "Locked until you complete prerequisites: " + String.join(", ", missingNames) + ".";
                recommendations.add(new SkillRecommendation(skill, reason, false, missingNames, 10));
            }
        }

        recommendations.sort((a, b) -> Integer.compare(b.priority(), a.priority()));
        return recommendations;
    }

    public record SkillRecommendation(
            Skill skill,
            String explanation,
            boolean isEligible,
            List<String> missingPrerequisites,
            int priority
    ) {}
}
