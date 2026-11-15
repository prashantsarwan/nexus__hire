package com.nexushire.repository.spec;

import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.SeekerSkill;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SeekerProfileSpecification {

    private SeekerProfileSpecification() {
    }

    public static Specification<SeekerProfile> search(List<Long> skillIds,
                                                      Integer minExperience,
                                                      String location,
                                                      Long minSalary,
                                                      String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                Join<SeekerProfile, Object> userJoin = root.join("user", JoinType.INNER);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("headline")), like),
                        cb.like(cb.lower(root.get("summary")), like),
                        cb.like(cb.lower(userJoin.get("fullName")), like)
                ));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("currentLocation")), "%" + location.toLowerCase() + "%"));
            }
            if (minExperience != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalExperienceYears"), minExperience));
            }
            if (minSalary != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expectedSalaryMax"), minSalary));
            }
            if (skillIds != null && !skillIds.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<SeekerSkill> ssRoot = subquery.from(SeekerSkill.class);
                subquery.select(ssRoot.get("seekerProfile").get("id"))
                        .where(ssRoot.get("skill").get("id").in(skillIds));
                predicates.add(root.get("id").in(subquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
