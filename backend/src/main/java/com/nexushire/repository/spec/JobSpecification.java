package com.nexushire.repository.spec;

import com.nexushire.entity.Job;
import com.nexushire.entity.JobSkill;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> search(String keyword,
                                            String location,
                                            Boolean remote,
                                            Long minSalary,
                                            Integer minExperience,
                                            List<Long> skillIds,
                                            Job.JobStatus status) {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class) {
                root.fetch("company", JoinType.LEFT);
            }
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }
            if (location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (Boolean.TRUE.equals(remote)) {
                predicates.add(cb.isTrue(root.get("remote")));
            }
            if (minSalary != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryMax"), minSalary));
            }
            if (minExperience != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minExperience"), minExperience));
            }
            if (skillIds != null && !skillIds.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<JobSkill> jobSkillRoot = subquery.from(JobSkill.class);
                subquery.select(jobSkillRoot.get("job").get("id"))
                        .where(jobSkillRoot.get("skill").get("id").in(skillIds));
                predicates.add(root.get("id").in(subquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
