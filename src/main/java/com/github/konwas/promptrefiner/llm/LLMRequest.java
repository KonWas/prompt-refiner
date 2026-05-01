package com.github.konwas.promptrefiner.llm;

public record LLMRequest(String systemPrompt, String userPrompt, String model, double temperature) {
}
