package com.groovify.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.groovify.jpa.model.Songs;

public interface SongsRepository extends JpaRepository<Songs, Long> {

}
