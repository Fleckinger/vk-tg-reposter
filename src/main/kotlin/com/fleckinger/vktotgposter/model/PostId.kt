package com.fleckinger.vktotgposter.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "postsId")
class PostId() {

    constructor(id: Long) : this() {
        this.id = id
    }

    @Id
    var id: Long = 0
}