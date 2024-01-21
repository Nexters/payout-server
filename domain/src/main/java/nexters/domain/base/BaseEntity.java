package nexters.domain.base;

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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "last_modified_at")
    @LastModifiedDate
    private Instant lastModifiedAt;

    /**
     * 엔티티 클래스의 hash code 함수를 재정의한 메서드입니다.
     * @return object의 id 기반으로 생성된 hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 엔티티 클래스의 equals 함수를 재정의한 메서드입니다.
     * @param obj 비교할 object
     * @return 해당 object가 BaseEntity 타입이면서, 같은 id를 가지고 있는지 여부
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BaseEntity && this.id.equals(((BaseEntity) obj).getId());
    }
}
