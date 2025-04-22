package io.github.petty.tour.repository;


import io.github.petty.tour.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByContentId(Long contentId);
    @Query("""
        SELECT c FROM Content c
        LEFT JOIN FETCH c.petTourInfo
    """)

    /**
     * 주어진 contentId 목록에 해당하는 Content 및 연관된 모든 데이터(Cascade)를 삭제합니다.
     * @param contentIds 삭제할 contentId 리스트
     * @return 삭제된 레코드 수 (반환 타입은 void 또는 long 등 조정 가능)
     */
    long deleteAllByContentIdIn(List<Long> contentIds);

    // 테스트용 10개 추출
    List<Content> findTop10ByOrderByContentIdAsc();
}
