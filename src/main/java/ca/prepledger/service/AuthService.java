package ca.prepledger.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.prepledger.session.UserSession;

public class AuthService {

	//private static final String LOCALHOST_ROOT = "http://localhost:8080";
	private static final String RAILWAY_ROOT = "https://prepledger-server-production.up.railway.app";	
	private static final String LOGIN_URL = RAILWAY_ROOT + "/auth/login";
	private static final String REGISTER_URL = RAILWAY_ROOT + "/auth/register";
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();


	public UserSession login(String email, String password) 
			throws IOException, InterruptedException {

		var credentials = mapper.createObjectNode();
		credentials.put("email", email);
		credentials.put("password", password);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(LOGIN_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(
						mapper.writeValueAsString(credentials),
						StandardCharsets.UTF_8))
				.build();

		HttpResponse<String> response = client.send(
				request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() != 200) {
			throw new IOException("Login failed. Check your email and password.");
		}

		JsonNode json = mapper.readTree(response.body());

		return new UserSession(
				json.get("token").asText(),
				json.get("userId").asLong(),
				json.get("email").asText()
				);
	}

	public void register(String email, String password)
			throws IOException, InterruptedException {

		var credentials = mapper.createObjectNode();
		credentials.put("email", email);
		credentials.put("password", password);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(REGISTER_URL))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(
						mapper.writeValueAsString(credentials),
						StandardCharsets.UTF_8))
				.build();

		HttpResponse<String> response = client.send(
				request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() != 201) {
			throw new IOException(
					response.statusCode() == 409
					? "An account with this email already exists."
							: "Registration failed. Please check your details."
					);
		}
	}
	
}
