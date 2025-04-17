package io.github.petty.tour.repository;


import io.github.petty.tour.entity.PetTourInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PetTourInfoRepository extends JpaRepository<PetTourInfo, Long> {
     // PetTourInfo는 Content와 1:1 관계이며 PK를 공유하므로,
     // findById(contentId)를 사용하면 됩니다.
}