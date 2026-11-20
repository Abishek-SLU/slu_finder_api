
package com.app.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.entity.Interest;
import com.app.repository.InterestRepository;

@Repository
public class InterestDAO {

    @Autowired
    private InterestRepository interestRepository;

    public List<Interest> getAllInterests() {
        return interestRepository.findAllByOrderByCategoryAscNameAsc();
    }

    public Optional<Interest> findById(UUID id) {
        return interestRepository.findById(id);
    }

    public List<Interest> findAllByIds(List<UUID> ids) {
        return interestRepository.findAllById(ids);
    }
}
