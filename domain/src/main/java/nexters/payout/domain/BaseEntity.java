package nexters.payout.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 생성일자, 마지막으로 수정된 일자 등 엔티티 별 공통으로 사용되는 클래스입니다.
 *
 * @author Min Ho CHO
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "last_modified_at")
    @LastModifiedDate
    private Instant lastModifiedAt;

}
