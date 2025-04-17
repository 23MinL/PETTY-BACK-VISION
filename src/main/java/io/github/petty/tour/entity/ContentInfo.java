package io.github.petty.tour.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "content_info")
@NoArgsConstructor
@ToString(exclude = "content")
public class ContentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contentid")
    private Content content;


    @Column(name = "fldgubun", length = 10)
    private String fldGubun;

    @Column(name = "infoname", length = 100)
    private String infoName;

    @Lob
    @Column(name = "infotext")
    private String infoText;

    @Column(name = "serialnum", length = 10)
    private String serialNum;

}