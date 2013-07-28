package com.github.spring.event.samples.transactional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The Interface ObjectRepository.
 */
public interface ObjectEntityRepository extends JpaRepository<ObjectEntity, Long> {

}
