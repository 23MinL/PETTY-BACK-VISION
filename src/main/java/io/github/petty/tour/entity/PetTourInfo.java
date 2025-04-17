package io.github.petty.tour.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "pet_tour_info")
@NoArgsConstructor
@ToString(exclude = "content")
public class PetTourInfo {
    @Id
    @Column(name = "contentid", nullable = false)
    private Long contentId;

    // 양방향 연관관계를 위한 필드 추가
    @OneToOne(fetch = FetchType.LAZY) // 필요에 따라 FetchType 변경 가능
    @MapsId // PetTourInfo의 ID(@Id)가 Content의 ID를 사용하도록 매핑
    @JoinColumn(name = "contentid") // DB 상의 외래 키 컬럼 지정 (기존 contentid 컬럼 활용)
    private Content content; // 'content' 필드 추가

    @Lob
    @Column(name = "rela_acdnt_risk_mtr")
    private String relaAcdntRiskMtr;

    @Column(name = "acmpy_type_cd", length = 30)
    private String acmpyTypeCd;

    @Lob
    @Column(name = "rela_poses_fclty")
    private String relaPosesFclty;

    @Lob
    @Column(name = "rela_frnsh_prdlst")
    private String relaFrnshPrdlst;

    @Lob
    @Column(name = "etc_acmpy_info")
    private String etcAcmpyInfo;

    @Lob
    @Column(name = "rela_purc_prdlst")
    private String relaPurcPrdlst;

    @Lob
    @Column(name = "acmpy_psbl_cpam")
    private String acmpyPsblCpam;

    @Lob
    @Column(name = "rela_rntl_prdlst")
    private String relaRntlPrdlst;

    @Lob
    @Column(name = "acmpy_need_mtr")
    private String acmpyNeedMtr;

}