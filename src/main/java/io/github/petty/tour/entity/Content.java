package io.github.petty.tour.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Content {
    @Id
    @Column(name = "contentid", nullable = false)
    private Long contentId;
    @Column(name = "contenttypeid", nullable = false)
    private Integer contentTypeId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "addr1")
    private String addr1;
    @Column(name = "addr2", length = 100)
    private String addr2;

    @Column(name = "areacode")
    private Integer areaCode;

    @Column(name = "sigungucode")
    private Integer sigunguCode;

    @Column(name = "cat1", nullable = false, length = 10)
    private String cat1;

    @Column(name = "cat2", length = 10)
    private String cat2;

    @Column(name = "cat3", length = 10)
    private String cat3;

    @Column(name = "createdtime", nullable = false)
    private Instant createdTime;

    @Column(name = "modifiedtime", nullable = false)
    private Instant modifiedTime;

    @Column(name = "firstimage", length = 2048)
    private String firstImage;

    @Column(name = "firstimage2", length = 2048)
    private String firstImage2;

    @Column(name = "cpyrhtdivcd", length = 20)
    private String cpyrhtDivCd;

    @Column(name = "mapx", precision = 13, scale = 10)
    private BigDecimal mapX;

    @Column(name = "mapy", precision = 13, scale = 10)
    private BigDecimal mapY;

    @Column(name = "mlevel")
    private Integer mlevel;

    @Column(name = "tel", length = 30)
    private String tel;

    @Column(name = "telname", length = 50)
    private String telName;

    @Lob
    @Column(name = "homepage")
    private String homepage;

    @Lob
    @Column(name = "overview")
    private String overview;

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ContentImage> contentImages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ContentInfo> contentInfos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RoomInfo> roomInfos = new LinkedHashSet<>();

    // mappedBy 값은 자식 엔티티의 부모 참조 필드 이름 ('content')과 일치
    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ContentIntro contentIntro;

    // mappedBy 값은 자식 엔티티의 부모 참조 필드 이름 ('content')과 일치
    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PetTourInfo petTourInfo;


}