package edu.hebbible.controller;

import edu.hebbible.service.impl.ChatClientService;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.Model;
//import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Controller
//@AllArgsConstructor
@RequestMapping(value = "v1/chat", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ChatController {

    @Autowired
    private ChatClientService chatClientService;

    @PostMapping
    public @ResponseBody String chat() throws OllamaBaseException, IOException, InterruptedException {
        return chatClientService.chat();
    }

    @GetMapping("/list-models")
    public ResponseEntity<List<Model>> listModels() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        List<Model> models = chatClientService.listModels();
        return ResponseEntity.ok(models);
    }
}
