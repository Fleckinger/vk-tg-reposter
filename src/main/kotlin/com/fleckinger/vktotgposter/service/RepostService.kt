package com.fleckinger.vktotgposter.service

import com.fleckinger.vktotgposter.dto.Photo
import com.fleckinger.vktotgposter.dto.Post
import com.fleckinger.vktotgposter.model.PostId
import com.fleckinger.vktotgposter.respository.PostRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.media.InputMedia
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo

@Service
class RepostService(
    val vkService: VkService,
    val telegramService: TelegramService,
    val postRepository: PostRepository
) {
    private val log: Logger = LoggerFactory.getLogger(RepostService::class.java)
    @Value("\${telegram.channel.wordBlacklist}")
    lateinit var invalidWords: List<String>

    @Scheduled(fixedDelayString = "\${application.sleep.time}")
    fun repostFromVkToTelegram() {
        log.info("Reposting start")
        val posts = vkService.getPosts().items
        if (posts != null) {
            for (post in posts) {
                post.text = filterString(post.text, invalidWords)
                val text = post.text
                val attachmentsSize = post.attachments.size
                if (!postRepository.existsById(post.id)) {
                    if (post.attachments.first().type == "photo" && attachmentsSize == 1) {
                        val photo = formInputMediaPhoto(post)
                        telegramService.sendPhotoToChannel(photo)
                        log.info("Photo with text: $text")
                    } else if (post.attachments.first().type == "video" && attachmentsSize == 1) {
                        //TODO Implement sending video using multipart/form-data and if video from Youtube - using URL
                        /*val video = formInputMediaVideo(post)
                        telegramService.sendVideoToChannel(video)*/
                        log.info("Video with text: $text")
                    } else {
                        val media = formInputMediaGroup(post)
                        telegramService.sendMediaGroupToChannel(media)
                        log.info("Post with text: $text. Number of attachments: ${media.size}")
                    }
                    postRepository.save(PostId(post.id))
                    log.info("Post with id: ${post.id} saved to database")
                } else {
                    log.info("Post with id: ${post.id} was already posted")
                }
            }
            log.info("Reposting finish")
        }
    }

    //TODO consider about relocate this method to Utils class
    fun formInputMediaGroup(post: Post): List<InputMedia> {
        val inputMedia = mutableListOf<InputMedia>()
        val attachments = post.attachments
        //TODO refactor this section, because in current state this code might put different type of media in one list,
        // that just restricted by telegram
        if (attachments.isNotEmpty()) {
            attachments.forEachIndexed { index, attachment ->
                if (attachment.type.equals("photo", ignoreCase = true) && attachment.photo != null) {
                    val photo = findLargestImageUrl(attachment.photo)
                    val media = if (index == 0) {
                        InputMediaPhoto.builder().media(photo).caption(post.text).build()
                    } else {
                        InputMediaPhoto(photo)
                    }
                    inputMedia.add(media)
                } else if (attachment.type.equals("video", ignoreCase = true) && attachment.video != null) {
                    val video = attachment.video.player
                    val media = if (index == 0) {
                        InputMediaVideo.builder().media(video).caption(post.text).build()
                    } else {
                        InputMediaVideo(video)
                    }
                    inputMedia.add(media)
                }
            }
        }
        return inputMedia
    }

    fun formInputMediaPhoto(post: Post): InputMediaPhoto {
        if (post.attachments.isNotEmpty() && post.attachments.first().type == "photo") {
            val photo = post.attachments.first().photo
            val url = findLargestImageUrl(photo!!)
            val text = post.text

            return InputMediaPhoto.builder().media(url).caption(text).build()
        } else {
            throw IllegalArgumentException("This method allow only post with one photo")
        }
    }

    fun formInputMediaVideo(post: Post): InputMediaVideo {
        if (post.attachments.isNotEmpty() && post.attachments.first().type == "photo") {
            val photo = post.attachments.first().photo
            val url = findLargestImageUrl(photo!!)
            val text = post.text

            return InputMediaVideo.builder().media(url).caption(text).build()
        } else {
            throw IllegalArgumentException("This method allow only post with one video")
        }
    }

    private fun findLargestImageUrl(photo: Photo): String {
        if (photo.sizes.isEmpty()) {
            return ""
        }
        var largestImageLink = photo.sizes[0].url

        var largestResolution = 0
        for (size in photo.sizes) {
            val resolution = size.height + size.width
            if (resolution <= 10_000 && resolution > largestResolution) {
                largestResolution = resolution
                largestImageLink = size.url
            }
        }
        return largestImageLink
    }

    private fun filterString(text: String, invalidWords: List<String>): String {
        var result = text
        for (word in invalidWords) {
            result = result.replace(word, "")
        }
        return result
    }
}
