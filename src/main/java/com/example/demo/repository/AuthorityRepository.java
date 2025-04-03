package com.example.demo.repository;

import com.example.demo.entity.Authorities;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authorities, Integer> {
}
