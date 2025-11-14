package com.fantasyfootball.fantasy_analyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for Fantasy Analyzer Application.
 * Tests that the Spring context loads correctly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FantasyAnalyzerApplicationTests {

	@Test
	void contextLoads() {
		// This test passes if the application context loads successfully
	}

}
