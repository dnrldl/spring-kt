package com.springkt.global.audit

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Instant

@MappedSuperclass
abstract class AuditableEntity {

    @Column(updatable = false)
    var createdAt: Instant? = null
        protected set

    @Column(length = 100, updatable = false)
    var createdBy: String? = null
        protected set

    @Column(length = 45, updatable = false)
    var createdIp: String? = null
        protected set

    @Column
    var updatedAt: Instant? = null
        protected set

    @Column(length = 100)
    var updatedBy: String? = null
        protected set

    @Column(length = 45)
    var updatedIp: String? = null
        protected set

    @PrePersist
    fun prePersist() {
        val now = Instant.now()
        val actor = AuditContext.currentActor()
        val ip = AuditContext.currentIp()

        createdAt = now
        createdBy = actor
        createdIp = ip
        updatedAt = now
        updatedBy = actor
        updatedIp = ip
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
        updatedBy = AuditContext.currentActor()
        updatedIp = AuditContext.currentIp()
    }
}
