package com.spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("default")
public abstract class _ServiceTest {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
}

