package com.nexushire.service;

import com.nexushire.dto.recruiter.CompanyRequest;
import com.nexushire.dto.recruiter.CompanyResponse;
import com.nexushire.entity.Company;
import com.nexushire.entity.RecruiterProfile;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.CompanyRepository;
import com.nexushire.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    @Transactional
    public CompanyResponse createCompany(CompanyRequest request, User recruiter) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile missing"));
        if (profile.getCompany() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Company already exists for this recruiter");
        }
        Company company = Company.builder()
                .name(request.name())
                .logoUrl(request.logoUrl())
                .website(request.website())
                .industry(request.industry())
                .size(request.size())
                .description(request.description())
                .cultureText(request.cultureText())
                .createdBy(recruiter.getId())
                .build();
        company = companyRepository.save(company);
        profile.setCompany(company);
        profile.setCompanyAdmin(true);
        recruiterProfileRepository.save(profile);
        return toResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(CompanyRequest request, User recruiter) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile missing"));
        Company company = profile.getCompany();
        if (company == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No company associated");
        }
        if (!profile.isCompanyAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only company admin can update");
        }
        company.setName(request.name());
        company.setLogoUrl(request.logoUrl());
        company.setWebsite(request.website());
        company.setIndustry(request.industry());
        company.setSize(request.size());
        company.setDescription(request.description());
        company.setCultureText(request.cultureText());
        return toResponse(companyRepository.save(company));
    }

    public CompanyResponse toResponse(Company c) {
        if (c == null) {
            return null;
        }
        return new CompanyResponse(c.getId(), c.getName(), c.getLogoUrl(), c.getWebsite(),
                c.getIndustry(), c.getSize(), c.getDescription(), c.getCultureText());
    }
}
