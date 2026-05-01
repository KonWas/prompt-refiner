package com.github.konwas.promptrefiner.llm;

public class LLMException extends Exception {
    private final String userMessage;

    public LLMException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }

    public LLMException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
