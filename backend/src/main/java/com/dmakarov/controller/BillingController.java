package com.dmakarov.controller;

import static com.dmakarov.ApiPathsV1.HELLO;
import static com.dmakarov.ApiPathsV1.ROOT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(ROOT)
public class BillingController {

  @GetMapping(HELLO + "/{name}")
  ResponseEntity<String> getHelloResponse(@PathVariable String name) {
    log.info("Get Hello request received, name {}", name);

    String response = "Hello, " + name + "!";

    return ResponseEntity.ok().body(response);
  }
}
