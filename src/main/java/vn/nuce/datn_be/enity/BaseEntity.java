package vn.nuce.datn_be.enity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
@Setter
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    protected Long id;

    @Version
    protected Integer version;

    @CreatedDate
    protected OffsetDateTime created;

    @CreatedBy
    protected Long createdBy;

    @LastModifiedDate
    protected OffsetDateTime updated;

    @LastModifiedBy
    protected Long updatedBy;
}
