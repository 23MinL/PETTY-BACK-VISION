package io.github.petty.tour.repository;


import io.github.petty.tour.entity.RoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoomInfoRepository extends JpaRepository<RoomInfo, Long> {
    // Content ID로 연관된 객실 정보들을 찾는 메소드 (필요시 추가)
     List<RoomInfo> findByContent_ContentId(Long contentId);
}