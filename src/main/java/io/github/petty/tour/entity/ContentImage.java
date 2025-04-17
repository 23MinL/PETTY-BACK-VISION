package io.github.petty.tour.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "content_image")
@NoArgsConstructor
@ToString(exclude = "content") // 양방향 관계 시 toString 무한 루프 방지
public class ContentImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;


    // Content 참조 필드 추가
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contentid") // 실제 DB의 외래 키 컬럼명 지정
    private Content content;




    @Column(name = "imgname")
    private String imgName;

    @Column(name = "originimgurl", length = 2048)
    private String originImgUrl;

    @Column(name = "serialnum", length = 20)
    private String serialNum;

    @Column(name = "smallimageurl", length = 2048)
    private String smallImageUrl;

    @Column(name = "cpyrhtdivcd", length = 20)
    private String cpyrhtDivCd;

}