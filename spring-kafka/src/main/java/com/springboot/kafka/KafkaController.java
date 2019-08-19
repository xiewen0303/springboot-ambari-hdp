package com.springboot.kafka;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Resource
    private KafkaSender kafkaSender;

    @RequestMapping("/send")
    public String send(){
        kafkaSender.send();
        return "SUCCESS";
    }
}
