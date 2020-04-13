package com.example.vote;

import com.example.vote.entity.VoteActivity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
class VoteApplicationTests {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    void contextLoads() {
        VoteActivity activity = null;
        System.out.println((Integer) Optional.ofNullable(activity).map(e -> e.getId()).orElse(null));
        logger.info("logger success!");
    }

}
