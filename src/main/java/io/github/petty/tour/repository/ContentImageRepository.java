package io.github.petty.tour.repository;


import io.github.petty.tour.entity.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
     List<ContentImage> findByContent_ContentId(Long contentId);
}