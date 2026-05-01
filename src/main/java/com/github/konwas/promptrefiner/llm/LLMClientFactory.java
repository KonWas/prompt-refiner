package com.github.konwas.promptrefiner.llm;

import com.github.konwas.promptrefiner.settings.PluginSettings;
import com.intellij.openapi.application.ApplicationManager;

import java.time.Duration;

public class LLMClientFactory {

    private LLMClientFactory() {
    }

    public static LLMClient create() {
        PluginSettings settings = ApplicationManager.getApplication().getService(PluginSettings.class);
        return new OllamaClient(settings.getEndpoint(), Duration.ofSeconds(settings.getTimeoutSeconds()));
    }
}
