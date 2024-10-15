package edu.hebbible.controller;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private Svc svc;

    @PostMapping("psukim")
    public ResponseEntity<Collection<Pasuk>> psukim(@RequestBody String args) {
        log.info("/post " + args);
        List<Pasuk> result = svc.psukim(ServiceImpl.engTx(args), false);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("dilugim")
    public ResponseEntity<Integer> dilugim(@RequestBody String args, @RequestParam Integer skipMin, @RequestParam Integer skipMax) {
        log.info("/dilugim " + args);
        int result = svc.dilugim(ServiceImpl.engTx(args), skipMin, skipMax);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("psukim")
    public ResponseEntity<Integer> psukim() {
        log.info("/get");
        int result = svc.repoSize();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}