package io.github.petty.tour.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "content_intro")
@NoArgsConstructor
@ToString(exclude = "content")
public class ContentIntro {
    @Id
    @Column(name = "contentid", nullable = false)
    private Long contentId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "contentid")
    private Content content;

    @Column(name = "intro_details")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> introDetails;

}