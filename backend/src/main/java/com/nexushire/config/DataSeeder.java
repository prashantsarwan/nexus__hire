package com.nexushire.config;

import com.nexushire.entity.Assessment;
import com.nexushire.entity.Company;
import com.nexushire.entity.Job;
import com.nexushire.entity.JobSkill;
import com.nexushire.entity.RecruiterProfile;
import com.nexushire.entity.Role;
import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.SeekerSkill;
import com.nexushire.entity.Skill;
import com.nexushire.entity.User;
import com.nexushire.repository.AssessmentRepository;
import com.nexushire.repository.CompanyRepository;
import com.nexushire.repository.JobRepository;
import com.nexushire.repository.JobSkillRepository;
import com.nexushire.repository.RecruiterProfileRepository;
import com.nexushire.repository.SeekerProfileRepository;
import com.nexushire.repository.SeekerSkillRepository;
import com.nexushire.repository.SkillRepository;
import com.nexushire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;
    private final SeekerSkillRepository seekerSkillRepository;
    private final AssessmentRepository assessmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        String encoded = passwordEncoder.encode("Password@123");

        User admin = userRepository.save(User.builder()
                .email("admin@nexus.dev").passwordHash(encoded).fullName("Nexus Admin")
                .role(Role.ADMIN).verified(true).active(true).build());

        User recruiter1 = userRepository.save(User.builder()
                .email("recruiter@nexus.dev").passwordHash(encoded).fullName("Aarav Mehta")
                .role(Role.RECRUITER).verified(true).active(true).build());
        User recruiter2 = userRepository.save(User.builder()
                .email("priya@nexus.dev").passwordHash(encoded).fullName("Priya Sharma")
                .role(Role.RECRUITER).verified(true).active(true).build());

        User seeker1 = userRepository.save(User.builder()
                .email("seeker@nexus.dev").passwordHash(encoded).fullName("Rohan Verma")
                .role(Role.JOB_SEEKER).verified(true).active(true).build());
        User seeker2 = userRepository.save(User.builder()
                .email("sneha@nexus.dev").passwordHash(encoded).fullName("Sneha Iyer")
                .role(Role.JOB_SEEKER).verified(true).active(true).build());

        Company c1 = companyRepository.save(Company.builder()
                .name("QuantumForge").logoUrl("https://ui-avatars.com/api/?name=QuantumForge&background=0a1a2a&color=4df")
                .website("https://quantumforge.dev").industry("Cloud Infrastructure").size("200-500")
                .description("Building next-gen distributed systems for global enterprises.")
                .cultureText("Deep tech, remote-first, engineering-obsessed.").createdBy(recruiter1.getId()).build());

        Company c2 = companyRepository.save(Company.builder()
                .name("Neuralis AI").logoUrl("https://ui-avatars.com/api/?name=Neuralis+AI&background=1a0a2a&color=f0f")
                .website("https://neuralis.ai").industry("Artificial Intelligence").size("50-200")
                .description("Applied AI products for healthcare and fintech.")
                .cultureText("Research-driven, ship fast, learn faster.").createdBy(recruiter2.getId()).build());

        recruiterProfileRepository.save(RecruiterProfile.builder().user(recruiter1).company(c1).designation("Tech Recruiter").companyAdmin(true).build());
        recruiterProfileRepository.save(RecruiterProfile.builder().user(recruiter2).company(c2).designation("Talent Lead").companyAdmin(true).build());

        SeekerProfile sp1 = seekerProfileRepository.save(SeekerProfile.builder().user(seeker1)
                .headline("Full Stack Engineer").summary("3 years building scalable web apps with Java + React.")
                .totalExperienceYears(3).currentLocation("Bengaluru").preferredLocations("Bengaluru,Mumbai,Remote")
                .expectedSalaryMin(1200000L).expectedSalaryMax(1800000L).noticePeriodDays(30).build());
        SeekerProfile sp2 = seekerProfileRepository.save(SeekerProfile.builder().user(seeker2)
                .headline("ML Engineer").summary("Data scientist transitioning into ML engineering.")
                .totalExperienceYears(2).currentLocation("Hyderabad").preferredLocations("Hyderabad,Remote")
                .expectedSalaryMin(900000L).expectedSalaryMax(1400000L).noticePeriodDays(15).build());

        Skill java = skillRepository.save(Skill.builder().name("Java").build());
        Skill spring = skillRepository.save(Skill.builder().name("Spring Boot").build());
        Skill react = skillRepository.save(Skill.builder().name("React").build());
        Skill sql = skillRepository.save(Skill.builder().name("SQL").build());
        Skill docker = skillRepository.save(Skill.builder().name("Docker").build());
        Skill aws = skillRepository.save(Skill.builder().name("AWS").build());
        Skill python = skillRepository.save(Skill.builder().name("Python").build());
        Skill tensorflow = skillRepository.save(Skill.builder().name("TensorFlow").build());
        Skill kubernetes = skillRepository.save(Skill.builder().name("Kubernetes").build());
        Skill typescript = skillRepository.save(Skill.builder().name("JavaScript").build());

        seekerSkillRepository.save(SeekerSkill.builder().seekerProfile(sp1).skill(java).proficiency(SeekerSkill.Proficiency.EXPERT).verified(true).build());
        seekerSkillRepository.save(SeekerSkill.builder().seekerProfile(sp1).skill(spring).proficiency(SeekerSkill.Proficiency.EXPERT).verified(true).build());
        seekerSkillRepository.save(SeekerSkill.builder().seekerProfile(sp1).skill(react).proficiency(SeekerSkill.Proficiency.INTERMEDIATE).verified(false).build());
        seekerSkillRepository.save(SeekerSkill.builder().seekerProfile(sp1).skill(sql).proficiency(SeekerSkill.Proficiency.INTERMEDIATE).verified(false).build());
        seekerSkillRepository.save(SeekerSkill.builder().seekerProfile(sp2).skill(python).proficiency(SeekerSkill.Proficiency.EXPERT).verified(true).build());
        seekerSkillRepository.save(SeekerSkill.builder().seekerProfile(sp2).skill(tensorflow).proficiency(SeekerSkill.Proficiency.INTERMEDIATE).verified(false).build());

        Job j1 = jobRepository.save(Job.builder().company(c1).postedBy(recruiter1.getId())
                .title("Senior Full Stack Engineer").description("Own end-to-end delivery of our control plane dashboard using Java + React.")
                .employmentType(Job.EmploymentType.FULL_TIME).minExperience(3).maxExperience(7)
                .salaryMin(1500000L).salaryMax(2500000L).location("Bengaluru").remote(true)
                .status(Job.JobStatus.ACTIVE).build());
        Job j2 = jobRepository.save(Job.builder().company(c1).postedBy(recruiter1.getId())
                .title("Backend Engineer (Spring Boot)").description("Build high-throughput microservices handling millions of requests daily.")
                .employmentType(Job.EmploymentType.FULL_TIME).minExperience(2).maxExperience(5)
                .salaryMin(1200000L).salaryMax(2000000L).location("Remote").remote(true)
                .status(Job.JobStatus.ACTIVE).build());
        Job j3 = jobRepository.save(Job.builder().company(c2).postedBy(recruiter2.getId())
                .title("ML Engineer").description("Ship production ML models for healthcare anomaly detection.")
                .employmentType(Job.EmploymentType.FULL_TIME).minExperience(2).maxExperience(5)
                .salaryMin(1400000L).salaryMax(2200000L).location("Hyderabad").remote(false)
                .status(Job.JobStatus.ACTIVE).build());
        Job j4 = jobRepository.save(Job.builder().company(c2).postedBy(recruiter2.getId())
                .title("Frontend Engineer (React)").description("Design and build beautiful, accessible UIs for our AI products.")
                .employmentType(Job.EmploymentType.FULL_TIME).minExperience(1).maxExperience(4)
                .salaryMin(900000L).salaryMax(1600000L).location("Remote").remote(true)
                .status(Job.JobStatus.ACTIVE).build());

        jobSkillRepository.save(JobSkill.builder().job(j1).skill(java).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j1).skill(spring).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j1).skill(react).mandatory(false).build());
        jobSkillRepository.save(JobSkill.builder().job(j1).skill(sql).mandatory(false).build());
        jobSkillRepository.save(JobSkill.builder().job(j2).skill(java).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j2).skill(spring).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j2).skill(docker).mandatory(false).build());
        jobSkillRepository.save(JobSkill.builder().job(j3).skill(python).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j3).skill(tensorflow).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j4).skill(react).mandatory(true).build());
        jobSkillRepository.save(JobSkill.builder().job(j4).skill(typescript).mandatory(true).build());

        String questions = "[{\"q\":\"Which keyword creates a class in Java?\",\"options\":[\"class\",\"def\",\"struct\",\"object\"],\"correct\":0},"
                + "{\"q\":\"Which collection does not allow duplicates?\",\"options\":[\"List\",\"Set\",\"Map\",\"Queue\"],\"correct\":1},"
                + "{\"q\":\"Default JVM heap is managed by?\",\"options\":[\"OS\",\"Garbage Collector\",\"Manual\",\"Stack\"],\"correct\":1},"
                + "{\"q\":\"Which annotation marks a Spring bean?\",\"options\":[\"@Bean\",\"@Component\",\"@Service only\",\"@Autowired\"],\"correct\":1},"
                + "{\"q\":\"Which is immutable in Java?\",\"options\":[\"String\",\"StringBuilder\",\"StringBuffer\",\"char[]\"],\"correct\":0}]";
        assessmentRepository.save(Assessment.builder().skill(java).title("Java Fundamentals")
                .durationMinutes(15).passingScore(60).questionsJson(questions).build());
    }
}
