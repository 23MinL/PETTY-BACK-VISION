package io.github.petty.tour.repository;


import io.github.petty.tour.entity.ContentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentInfoRepository extends JpaRepository<ContentInfo, Long> {
     List<ContentInfo> findByContent_ContentId(Long contentId);
}