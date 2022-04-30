package com.lucas.instagram.clone.infrastructure.repository

import com.lucas.instagram.clone.infrastructure.entity.Like
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface LikeRepository: JpaRepository<Like, Long> {
}