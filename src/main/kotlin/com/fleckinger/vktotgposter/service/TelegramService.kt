package com.fleckinger.vktotgposter.service

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMedia
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class TelegramService : TelegramLongPollingBot() {
    private val log: Logger = LoggerFactory.getLogger(TelegramService::class.java)

    @Value("\${telegram.bot.token}")
    private lateinit var botToken: String

    @Value("\${telegram.channel.id}")
    private lateinit var channelId: String

    private lateinit var botApiAddress: String

    @PostConstruct
    fun init() {
        botApiAddress = "https://api.telegram.org/bot${botToken}"
    }

    override fun getBotToken(): String {
        return botToken
    }

    override fun getBotUsername(): String {
        return channelId
    }

    override fun onUpdateReceived(update: Update?) {
        TODO("Not yet implemented")
    }

    fun sendMediaGroupToChannel(mediaGroup: List<InputMedia>) {
        val message = SendMediaGroup.builder().chatId(channelId).medias(mediaGroup).build()
        try {
            execute(message)
        } catch (apiException: TelegramApiException) {
            log.error(
                """"
                |Telegram API error. 
                |Cause: ${apiException.cause}. 
                |Message: ${apiException.message}
                |${apiException.stackTrace}
                |""".trimMargin()
            )
        }
    }
        //TODO need refactoring too avoid code duplicate
    fun sendPhotoToChannel(photoMedia: InputMedia) {
        val message =
            SendPhoto.builder().chatId(channelId).caption(photoMedia.caption).photo(InputFile(photoMedia.media)).build()
        try {
            execute(message)
        } catch (apiException: TelegramApiException) {
            log.error(
                """"
                |Telegram API error. 
                |Cause: ${apiException.cause}. 
                |Message: ${apiException.message}
                |${apiException.stackTrace}
                |""".trimMargin()
            )
        }

    }

    fun sendVideoToChannel(videoMedia: InputMedia) {
        val message =
            SendPhoto.builder().chatId(channelId).caption(videoMedia.caption).photo(InputFile(videoMedia.media)).build()
        try {
            execute(message)
        } catch (apiException: TelegramApiException) {
            log.error(
                """"
                |Telegram API error. 
                |Cause: ${apiException.cause}. 
                |Message: ${apiException.message}
                |${apiException.stackTrace}
                |""".trimMargin()
            )
        }
    }
}