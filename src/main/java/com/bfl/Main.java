package com.bfl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	static class WebhookRequest {
		public String name;
		public String regNo;
		public String email;
	}

	static class WebhookResponse {
		public String webhook;
		public String accessToken;
	}

	static class SolutionRequest {
		public String finalQuery;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {
		
			final String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

			WebhookRequest request = new WebhookRequest();
			request.name = "Varshith Y";
			request.regNo = "REG43218"; 
			request.email = "varshithsocial@gmail.com";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

			String webhookUrl = null;
			String accessToken = null;
			ObjectMapper mapper = new ObjectMapper();

			System.out.println("Attempting to generate webhook...");
			try {
				ResponseEntity<String> response = restTemplate.exchange(
					generateWebhookUrl,
					HttpMethod.POST,
					entity,
					String.class
				);
				if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
					Map<String, String> responseBody = mapper.readValue(response.getBody(), Map.class);
					webhookUrl = responseBody.get("webhook");
					accessToken = responseBody.get("accessToken");

					System.out.println("Webhook generated successfully.");
					System.out.println("Webhook URL: " + webhookUrl);
					System.out.println("Access Token: " + accessToken.substring(0, 10) + "..."); 
				} else {
					System.err.println("Failed to generate webhook. Status code: " + response.getStatusCode());
					return; 
				}
			} catch (Exception e) {
				System.err.println("Error during webhook generation: " + e.getMessage());
				return; 
			}

		
			String lastTwoDigitsStr = request.regNo.replaceAll("[^0-9]", "");
			int lastTwoDigits = Integer.parseInt(lastTwoDigitsStr.substring(Math.max(0, lastTwoDigitsStr.length() - 2)));
			String finalQuery;

			if (lastTwoDigits % 2 != 0) {
				System.out.println("Solving the SQL problem for ODD regNo...");
				finalQuery = "SELECT\n" +
						"    MAX(P.AMOUNT) AS SALARY,\n" +
						"    CONCAT(E.FIRST_NAME, ' ', E.LAST_NAME) AS NAME,\n" +
						"    (YEAR(CURDATE()) - YEAR(E.DOB)) AS AGE,\n" +
						"    D.DEPARTMENT_NAME\n" +
						"FROM\n" +
						"    EMPLOYEE E\n" +
						"JOIN\n" +
						"    PAYMENTS P ON E.EMP_ID = P.EMP_ID\n" +
						"JOIN\n" +
						"    DEPARTMENT D ON E.DEPARTMENT = D.DEPARTMENT_ID\n" +
						"WHERE\n" +
						"    DAYOFMONTH(P.PAYMENT_TIME) != 1\n" +
						"GROUP BY\n" +
						"    NAME, AGE, D.DEPARTMENT_NAME\n" +
						"ORDER BY\n" +
						"    SALARY DESC\n" +
						"LIMIT 1;";
			} else {
		
				System.out.println("Solving the SQL problem for EVEN regNo...");
				finalQuery = "SELECT\n" +
						"    e.EMP_ID,\n" +
						"    e.FIRST_NAME,\n" +
						"    e.LAST_NAME,\n" +
						"    d.DEPARTMENT_NAME,\n" +
						"    COUNT(y.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT\n" +
						"FROM\n" +
						"    EMPLOYEE e\n" +
						"JOIN\n" +
						"    DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID\n" +
						"LEFT JOIN\n" +
						"    EMPLOYEE y ON e.DEPARTMENT = y.DEPARTMENT AND e.DOB > y.DOB\n" +
						"GROUP BY\n" +
						"    e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME\n" +
						"ORDER BY\n" +
						"    e.EMP_ID DESC;";
			}

			System.out.println("Final SQL Query determined.");

		
			System.out.println("Submitting the final query to the webhook...");

			HttpHeaders submitHeaders = new HttpHeaders();
			submitHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			submitHeaders.setContentType(MediaType.APPLICATION_JSON);
			submitHeaders.set("Authorization", "Bearer " + accessToken);

			SolutionRequest solution = new SolutionRequest();
			solution.finalQuery = finalQuery;

			HttpEntity<SolutionRequest> submitEntity = new HttpEntity<>(solution, submitHeaders);

			try {
				
				ResponseEntity<String> submitResponse = restTemplate.exchange(
						webhookUrl,
						HttpMethod.POST,
						submitEntity,
						String.class
				);

				if (submitResponse.getStatusCode().is2xxSuccessful()) {
					System.out.println("Solution submitted successfully!");
					System.out.println("Response: " + submitResponse.getBody());
				} else {
					System.err.println("Failed to submit solution. Status code: " + submitResponse.getStatusCode());
					System.err.println("Response Body: " + submitResponse.getBody());
				}
			} catch (Exception e) {
				System.err.println("Error during solution submission: " + e.getMessage());
			}

			System.out.println("Application process finished.");
		};
	}
}
