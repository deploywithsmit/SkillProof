package com.skillproof.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final UserActivityRepository activityRepository;

    public ActivityService(UserActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void logActivity(User user, String actionType, String description, Long referenceId) {
        UserActivity activity = new UserActivity(user, actionType, description, referenceId);
        activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<UserActivity> getRecentActivities(Long userId) {
        return activityRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    }
}
