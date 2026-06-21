package com.nasanbus.common.model

import java.time.LocalDateTime

interface Auditable {
    val addedOn: LocalDateTime?
    val addedBy: String
    val updatedBy: String
    val updatedOn: LocalDateTime?
    val deletedBy: String?
    val deletedOn: LocalDateTime?
}
