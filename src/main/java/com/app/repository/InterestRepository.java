
package com.app.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.Interest;

@Repository
public interface InterestRepository
        extends JpaRepository<Interest, UUID> {

    List<Interest> findAllByOrderByCategoryAscNameAsc();
}
