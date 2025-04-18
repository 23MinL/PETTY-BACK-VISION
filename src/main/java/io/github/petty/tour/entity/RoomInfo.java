package io.github.petty.tour.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "room_info")
@NoArgsConstructor
@ToString(exclude = "content")
public class RoomInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contentid") // 실제 DB의 외래 키 컬럼명 지정
    private Content content;

//    @Column(name = "contentid", nullable = false)
//    private Long contentId;


    @Column(name = "roominfono", length = 50)
    private String roomInfoNo;

    @Column(name = "roomtitle", length = 150)
    private String roomTitle;

    @Column(name = "roomsize1", length = 20)
    private String roomSize1;

    @Column(name = "roomcount")
    private Integer roomCount;

    @Column(name = "roombasecount")
    private Integer roomBaseCount;

    @Column(name = "roommaxcount")
    private Integer roomMaxCount;

    @Column(name = "roomoffseasonminfee1", precision = 10)
    private BigDecimal roomOffSeasonMinFee1;

    @Column(name = "roomoffseasonminfee2", precision = 10)
    private BigDecimal roomOffSeasonMinFee2;

    @Column(name = "roompeakseasonminfee1", precision = 10)
    private BigDecimal roomPeakSeasonMinFee1;

    @Column(name = "roompeakseasonminfee2", precision = 10)
    private BigDecimal roomPeakSeasonMinFee2;

    @Lob
    @Column(name = "roomintro")
    private String roomIntro;

    @Column(name = "roombathfacility")
    private Boolean roomBathFacility;

    @Column(name = "roombath")
    private Boolean roomBath;

    @Column(name = "roomhometheater")
    private Boolean roomHomeTheater;

    @Column(name = "roomaircondition")
    private Boolean roomAirCondition;

    @Column(name = "roomtv")
    private Boolean roomTv;

    @Column(name = "roompc")
    private Boolean roomPc;

    @Column(name = "roomcable")
    private Boolean roomCable;

    @Column(name = "roominternet")
    private Boolean roomInternet;

    @Column(name = "roomrefrigerator")
    private Boolean roomRefrigerator;

    @Column(name = "roomtoiletries")
    private Boolean roomToiletries;

    @Column(name = "roomsofa")
    private Boolean roomSofa;

    @Column(name = "roomcook")
    private Boolean roomCook;

    @Column(name = "roomtable")
    private Boolean roomTable;

    @Column(name = "roomhairdryer")
    private Boolean roomHairdryer;

    @Column(name = "roomsize2", length = 30)
    private String roomSize2;

    @Column(name = "roomimg1", length = 2048)
    private String roomImg1;

    @Column(name = "roomimg1alt")
    private String roomImg1Alt;

    @Column(name = "roomimg2", length = 2048)
    private String roomImg2;

    @Column(name = "roomimg2alt")
    private String roomImg2Alt;

    @Column(name = "roomimg3", length = 2048)
    private String roomImg3;

    @Column(name = "roomimg3alt")
    private String roomImg3Alt;

    @Column(name = "roomimg4", length = 2048)
    private String roomImg4;

    @Column(name = "roomimg4alt")
    private String roomImg4Alt;

    @Column(name = "roomimg5", length = 2048)
    private String roomImg5;

    @Column(name = "roomimg5alt")
    private String roomImg5Alt;

}