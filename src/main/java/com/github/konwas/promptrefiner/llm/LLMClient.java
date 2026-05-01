package com.github.konwas.promptrefiner.llm;

public interface LLMClient {
    LLMResponse refine(LLMRequest request) throws LLMException;
}
