package com.fleckinger.vktotgposter.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class VkResponse(
    val response: Response
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Response(
    val count: Int?,
    val items: List<Post>?,
    val nextFrom: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Post(
    val id: Long,
    val ownerId: Int,
    val fromId: Int,
    val createdBy: Int,
    val date: Int,
    val text: String,
    val attachments: List<Attachment>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Attachment(
    val type: String,
    val photo: Photo?,
    val video: Video?,
    val link: Link?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Photo(
    val sizes: List<PhotoSize>
)

data class PhotoSize(
    val type: String,
    val url: String,
    val width: Int,
    val height: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Video(
    val title: String,
    val description: String,
    val accessKey: String,
    val player: String = "",
    val platform: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Link(
    val url: String,
    val description: String,
    val photo: Photo
)
