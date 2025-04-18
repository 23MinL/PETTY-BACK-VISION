package io.github.petty.tour.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter // lastSyncDate 업데이트를 위해 Setter 필요
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatus {

    @Id
    private Long id; // 고정 ID

    private LocalDate lastSyncDate; // 마지막으로 성공한 동기화의 대상 날짜

    public static final Long DEFAULT_ID = 1L; // 상태 레코드의 고정 ID
    // 필요시 ID를 설정하는 생성자 추가 가능
    public SyncStatus(Long id) {
        this.id = id;
    }
}