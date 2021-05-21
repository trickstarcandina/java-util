package com.example.mongodbspringboot.repository;

import com.example.mongodbspringboot.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;


public interface UserRepository extends MongoRepository<User, Long> { // Long: Type of User ID.

    User findByEmpNo(String empNo);

    List<User> findByFullNameLike(String fullName);

    List<User> findByHireDateGreaterThan(Date hireDate);

    // Supports native JSON query string
    @Query("{fullName:'?0'}")
    List<User> findCustomByFullName(String fullName);

}