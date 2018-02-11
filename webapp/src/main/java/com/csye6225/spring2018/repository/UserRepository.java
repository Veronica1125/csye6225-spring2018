package com.csye6225.spring2018.repository;

import com.csye6225.spring2018.pojo.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String>{
  User findUserByEmail(String email);
}
