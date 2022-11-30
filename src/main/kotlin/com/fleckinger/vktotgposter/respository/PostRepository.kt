package com.fleckinger.vktotgposter.respository

import com.fleckinger.vktotgposter.model.PostId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository: CrudRepository<PostId, Long>