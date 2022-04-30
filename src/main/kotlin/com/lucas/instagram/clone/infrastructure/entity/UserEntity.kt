package com.lucas.instagram.clone.infrastructure.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lucas.instagram.clone.common.annotations.NoArg
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.util.UUID
import javax.persistence.*

@NoArg
@Entity
@Table(name = "USERS")
data class UserEntity(
    @field:Id
    @field:Column(unique = true)
    var id: UUID? = null,
    @field:Column(unique = true)
    var username: String,
    var password: String,
    @field:Column(nullable = true)
    var name: String? = null,
    @field:Column(nullable = true)
    var phoneNumber: String? = null,
    @field:Column(unique = true, nullable = true)
    var email: String? = null,
    @field:Column(nullable = true)
    var perfilImage: String? = null,
    @OneToMany()
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    @JoinTable(name = "USER_FOLLOWERS")
    @field:Column(nullable = true)
    var followers: List<UserEntity> = ArrayList(),
    @OneToMany()
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    @JoinTable(name = "USER_FOLLOWING")
    @field:Column(nullable = true)
    var following: List<UserEntity> = ArrayList(),
    @OneToOne(mappedBy = "userLike", cascade = [CascadeType.ALL])
    var likePosts: Like? = null,
    @OneToMany(cascade = [CascadeType.REMOVE])
    @LazyCollection(LazyCollectionOption.FALSE)
    @field:Column(nullable = true)
    @JsonIgnore
    var posts: List<Post>? = ArrayList()
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}