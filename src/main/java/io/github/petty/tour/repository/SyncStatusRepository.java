package io.github.petty.tour.repository;


import io.github.petty.tour.entity.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SyncStatusRepository extends JpaRepository<SyncStatus, Long> {
    // SyncStatus는 고정 ID(예: 1L)를 사용할 가능성이 높습니다.
    // 필요하다면 특정 ID로 조회하는 기본 메소드 외에,
    // 항상 첫 번째(또는 유일한) 상태 레코드를 가져오는 메소드를 정의할 수 있습니다.
    Optional<SyncStatus> findTopByOrderByIdAsc();
}