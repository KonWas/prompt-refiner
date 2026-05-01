package com.github.konwas.promptrefiner.llm;

public class LLMConnectionException extends LLMException {
    public LLMConnectionException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
