package com.umbrastack.ecommerce.repositories;

import com.umbrastack.ecommerce.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    long countByIdIn(Set<Long> ids);
}
