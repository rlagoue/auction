package com.scopic.auction.domain;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseDomainObject {
    @Id
    @GeneratedValue
    @Column(name = "c_id")
    protected UUID id;
    @Version
    private long version;

    public BaseDomainObject() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDomainObject)) return false;
        BaseDomainObject that = (BaseDomainObject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
