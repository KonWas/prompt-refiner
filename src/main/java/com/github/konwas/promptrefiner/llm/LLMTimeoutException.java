package com.github.konwas.promptrefiner.llm;

public class LLMTimeoutException extends LLMException {
    public LLMTimeoutException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
