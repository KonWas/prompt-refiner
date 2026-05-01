package com.github.konwas.promptrefiner.llm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public class OllamaClient implements LLMClient {
    private static final Logger LOG = Logger.getInstance(OllamaClient.class);

    private final String endpoint;
    private final Duration timeout;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    public OllamaClient(String endpoint, Duration timeout) {
        this.endpoint = endpoint;
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    @Override
    public LLMResponse refine(LLMRequest request) throws LLMException {
        JsonObject options = new JsonObject();
        options.addProperty("temperature", request.temperature());

        JsonObject body = new JsonObject();
        body.addProperty("model", request.model());
        body.addProperty("system", request.systemPrompt());
        body.addProperty("prompt", request.userPrompt());
        body.addProperty("stream", false);
        body.add("options", options);

        String url = endpoint.replaceAll("/+$", "") + "/api/generate";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        LOG.debug("POST " + url + " model=" + request.model());

        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new LLMTimeoutException(
                    "Ollama timed out. Try a smaller model or increase the timeout in settings.", e);
        } catch (ConnectException e) {
            throw new LLMConnectionException(
                    "Ollama is not running. Start it with `ollama serve`, then try again.", e);
        } catch (IOException e) {
            if (isConnectionRefused(e)) {
                throw new LLMConnectionException(
                        "Ollama is not running. Start it with `ollama serve`, then try again.", e);
            }
            throw new LLMException(
                    "Unexpected error: " + e.getMessage() + ". See idea.log for details.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LLMException("Request was interrupted.", e);
        }

        int status = response.statusCode();
        String responseBody = response.body() == null ? "" : response.body();

        if (status < 200 || status >= 300) {
            LOG.warn("Ollama returned HTTP " + status + ": " + responseBody);
            if (responseBody.toLowerCase().contains("not found")) {
                throw new LLMModelNotFoundException(
                        "Model " + request.model() + " not found. Pull it with `ollama pull "
                                + request.model() + "`.");
            }
            throw new LLMException(
                    "Unexpected error: Ollama returned HTTP " + status + ". See idea.log for details.");
        }

        JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
        if (responseJson == null || !responseJson.has("response")) {
            throw new LLMException(
                    "Unexpected error: malformed Ollama response. See idea.log for details.");
        }
        return new LLMResponse(responseJson.get("response").getAsString());
    }

    private boolean isConnectionRefused(Throwable t) {
        for (Throwable cur = t; cur != null; cur = (cur.getCause() == cur ? null : cur.getCause())) {
            if (cur instanceof ConnectException) {
                return true;
            }
            if (cur.getMessage() != null
                    && cur.getMessage().toLowerCase().contains("connection refused")) {
                return true;
            }
        }
        return false;
    }
}
