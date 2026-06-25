package edu.hebbible.controller

import edu.hebbible.model.Pasuk
import edu.hebbible.service.Svc
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    @Autowired
    private lateinit var svc: Svc

    @GetMapping("psukim/{args}")
    fun psukim(@PathVariable args: String): ResponseEntity<List<Pasuk>> {
        log.info("/post $args")
        val result = svc.psukim((args), false, false)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("psukim")
    fun psukim(): ResponseEntity<Int> {
        log.info("/get")
        val result = svc.repoSize()
        return ResponseEntity(result, HttpStatus.OK)
    }

    companion object {
        private val log = LoggerFactory.getLogger(Controller::class.java)
    }
}
