package com.fleckinger.vktotgposter.service

import com.fleckinger.vktotgposter.dto.Response
import com.fleckinger.vktotgposter.dto.VkResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.net.URI

@Service
class VkService {
    private val log: Logger = LoggerFactory.getLogger(VkService::class.java)

    @Value("-\${vk.group.id}")
    lateinit var groupId: String

    @Value("\${vk.group.count}")
    var count: Int = 1

    @Value("\${vk.apiVersion}")
    lateinit var apiVersion: String

    @Value("\${vk.token}")
    private lateinit var vkOAuthToken: String

    val vkApiAddress = "https://api.vk.com/method"
    val vkGetMethod = "wall.get"


    fun getPosts(): Response {
        val uri = URI("$vkApiAddress/$vkGetMethod?owner_id=${groupId}&count=${count}&v=${apiVersion}")
        log.info("Getting posts. URI: $uri")
        val restTemplate = RestTemplate()
        val headers = Utils.bearerAuthHeaders(vkOAuthToken)
        val entity = HttpEntity<Response>(headers)
        val posts = restTemplate.exchange(uri, HttpMethod.GET, entity, VkResponse::class.java)
        //TODO а нужен ли тут лимит реквестов? Все равно в апи можно стучаться пару раз в секунду, а у меня раз в 10 сек
        var requestsLimit = 5
        while (requestsLimit != 0) {
            if (posts.statusCode == HttpStatus.OK) {
                return posts.body!!.response
            } else {
                log.error("Request failed with status code: ${posts.statusCode}")
                log.info("Wait 10 sec before new request")
                Thread.sleep(10_000)
                requestsLimit--
            }
        }
        throw Exception("Vk server does not respond")
    }
}