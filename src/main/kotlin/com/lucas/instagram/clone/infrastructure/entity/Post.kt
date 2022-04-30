package com.lucas.instagram.clone.infrastructure.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lucas.instagram.clone.common.annotations.NoArg
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToOne
import javax.persistence.Table

@NoArg
@Entity
@Table(name = "POSTS")
data class Post (
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(unique = true)
    val id: Long,
    val title: String? = null,
    @ManyToMany
    @JoinTable(
        name = "POST_LIKE",
        joinColumns = [JoinColumn(name = "likes")],
        inverseJoinColumns = [JoinColumn(name = "post")]

    )
    val likes: List<Like> = ArrayList()
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@NoArg
@Entity
@Table(name = "LIKES")
data class Like(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(unique = true)
    val id: Long? = null,
    @OneToOne
    @JoinColumn(name = "userLike")
    @JsonIgnore
    val userLike: UserEntity,
    @Column(nullable = false)
    @ManyToMany(mappedBy = "likes")
    @JsonIgnore
    val post: List<Post> = ArrayList()
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Like

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}