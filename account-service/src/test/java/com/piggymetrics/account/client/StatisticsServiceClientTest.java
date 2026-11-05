package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StatisticsServiceClientTest {

	@Test
	void sendsServiceTokenToStatistics() {
		RestClient.Builder builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://api.test/statistics/test"))
				.andExpect(header("Authorization", "Bearer service-token")).andRespond(withSuccess());
		new StatisticsServiceClient(builder, () -> "service-token", "https://api.test")
				.updateStatistics("test", new Account());
		server.verify();
	}

	@Test
	void statisticsFailureDoesNotFailAccountUpdate() {
		RestClient.Builder builder = RestClient.builder();
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		server.expect(requestTo("https://api.test/statistics/test")).andRespond(withServerError());
		assertDoesNotThrow(() -> new StatisticsServiceClient(builder, () -> "t", "https://api.test")
				.updateStatistics("test", new Account()));
	}
}
