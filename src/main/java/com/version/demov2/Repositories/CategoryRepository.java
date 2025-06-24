package com.version.demov2.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.version.demov2.Entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
