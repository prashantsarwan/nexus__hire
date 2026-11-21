package com.nexushire.service;

import com.nexushire.entity.Skill;
import com.nexushire.exception.ResourceNotFoundException;
import com.nexushire.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<Skill> listAll() {
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Skill> search(String query) {
        return skillRepository.findByNameContainingIgnoreCase(query == null ? "" : query);
    }

    @Transactional
    public Skill getOrCreate(String name) {
        return skillRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> skillRepository.save(Skill.builder().name(name).build()));
    }

    @Transactional(readOnly = true)
    public Skill getById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
    }
}
