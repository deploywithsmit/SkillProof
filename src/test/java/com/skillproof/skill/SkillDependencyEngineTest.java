package com.skillproof.skill;

import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillDependencyEngineTest {

    @Mock
    private SkillPrerequisiteRepository prerequisiteRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    private SkillDependencyEngine engine;

    private Skill javaSkill;
    private Skill sqlSkill;
    private Skill restSkill;
    private Skill springEnterpriseSkill;
    private User testStudent;

    @BeforeEach
    void setUp() {
        engine = new SkillDependencyEngine(prerequisiteRepository, userSkillRepository);

        javaSkill = new Skill("java", "Java Fundamentals", "Programming", Difficulty.BEGINNER, "Core Java", "OOP", 15, "code");
        javaSkill.setId(1L);

        sqlSkill = new Skill("sql", "SQL Design", "Databases", Difficulty.BEGINNER, "SQL", "3NF", 12, "database");
        sqlSkill.setId(2L);

        restSkill = new Skill("rest", "REST API Development", "Programming", Difficulty.INTERMEDIATE, "REST APIs", "HTTP", 20, "server");
        restSkill.setId(3L);

        springEnterpriseSkill = new Skill("enterprise", "Enterprise Spring", "Programming", Difficulty.ADVANCED, "DDD", "Arch", 25, "layers");
        springEnterpriseSkill.setId(4L);

        testStudent = new User("student@example.com", "student", "Student Test", "pwd", Role.STUDENT, UserStatus.ACTIVE);
        testStudent.setId(50L);
    }

    @Test
    @DisplayName("Should detect direct self-cycle (A -> A)")
    void shouldDetectSelfCycle() {
        boolean cycle = engine.wouldCreateCycle(1L, 1L);
        assertThat(cycle).isTrue();
    }

    @Test
    @DisplayName("Should detect indirect cyclic dependency (A -> B -> C -> A)")
    void shouldDetectIndirectCycle() {
        // Existing edges:
        // java (1) -> rest (3) (rest depends on java: prerequisite=1, skill=3)
        // rest (3) -> enterprise (4) (enterprise depends on rest: prerequisite=3, skill=4)
        SkillPrerequisite edge1 = new SkillPrerequisite(restSkill, javaSkill);
        SkillPrerequisite edge2 = new SkillPrerequisite(springEnterpriseSkill, restSkill);

        when(prerequisiteRepository.findAll()).thenReturn(List.of(edge1, edge2));

        // Proposed: java (1) depends on enterprise (4) -> creates cycle 1 -> 3 -> 4 -> 1
        boolean cycle = engine.wouldCreateCycle(1L, 4L);
        assertThat(cycle).isTrue();
    }

    @Test
    @DisplayName("Should allow valid non-cyclic prerequisite addition")
    void shouldAllowValidPrerequisite() {
        // Existing: rest (3) depends on java (1)
        SkillPrerequisite edge1 = new SkillPrerequisite(restSkill, javaSkill);
        when(prerequisiteRepository.findAll()).thenReturn(List.of(edge1));

        // Proposed: rest (3) also depends on sql (2)
        boolean cycle = engine.wouldCreateCycle(3L, 2L);
        assertThat(cycle).isFalse();
    }

    @Test
    @DisplayName("Should confirm prerequisites met when student completed all required skills")
    void shouldConfirmPrerequisitesMet() {
        when(prerequisiteRepository.findPrerequisitesForSkill(3L)).thenReturn(List.of(javaSkill, sqlSkill));

        UserSkill us1 = new UserSkill(testStudent, javaSkill, UserSkillStatus.VERIFIED, 100);
        UserSkill us2 = new UserSkill(testStudent, sqlSkill, UserSkillStatus.COMPLETED, 100);

        when(userSkillRepository.findByUserId(50L)).thenReturn(List.of(us1, us2));

        boolean met = engine.arePrerequisitesMet(50L, 3L);
        assertThat(met).isTrue();
    }

    @Test
    @DisplayName("Should reject prerequisites when one or more required skills are missing")
    void shouldRejectWhenPrerequisiteMissing() {
        when(prerequisiteRepository.findPrerequisitesForSkill(3L)).thenReturn(List.of(javaSkill, sqlSkill));

        // Only java is completed, sql is missing
        UserSkill us1 = new UserSkill(testStudent, javaSkill, UserSkillStatus.VERIFIED, 100);
        when(userSkillRepository.findByUserId(50L)).thenReturn(List.of(us1));

        boolean met = engine.arePrerequisitesMet(50L, 3L);
        assertThat(met).isFalse();
    }

    @Test
    @DisplayName("Should generate explainable recommendations based on completed competencies")
    void shouldGenerateExplainableRecommendations() {
        // REST requires Java
        when(prerequisiteRepository.findPrerequisitesForSkill(3L)).thenReturn(List.of(javaSkill));

        // User completed Java
        UserSkill us1 = new UserSkill(testStudent, javaSkill, UserSkillStatus.VERIFIED, 100);
        when(userSkillRepository.findByUserId(50L)).thenReturn(List.of(us1));

        List<SkillDependencyEngine.SkillRecommendation> recommendations =
                engine.generateRecommendations(50L, List.of(javaSkill, restSkill));

        // Java is already verified, so it shouldn't be recommended
        assertThat(recommendations).hasSize(1);
        SkillDependencyEngine.SkillRecommendation rec = recommendations.get(0);
        assertThat(rec.skill()).isEqualTo(restSkill);
        assertThat(rec.isEligible()).isTrue();
        assertThat(rec.explanation()).contains("Java Fundamentals");
    }
}
