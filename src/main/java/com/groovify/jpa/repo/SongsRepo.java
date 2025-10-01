package com.groovify.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.groovify.jpa.model.Songs;

public interface SongsRepo extends JpaRepository<Songs, Long> {

}

