package com.gonuclei.allcaughtup.repository.jpa;

import com.gonuclei.allcaughtup.model.AppUser;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {

  Optional<AppUser> findByEmail(String email);
}
