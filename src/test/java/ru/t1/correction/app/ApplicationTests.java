package ru.t1.correction.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.application.scheduling.enable=false")
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
