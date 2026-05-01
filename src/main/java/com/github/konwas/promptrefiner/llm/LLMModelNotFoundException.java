package com.github.konwas.promptrefiner.llm;

public class LLMModelNotFoundException extends LLMException {
    public LLMModelNotFoundException(String userMessage) {
        super(userMessage);
    }
}
