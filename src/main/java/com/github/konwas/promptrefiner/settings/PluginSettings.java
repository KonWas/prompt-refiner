package com.github.konwas.promptrefiner.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(
        name = "PromptRefinerSettings",
        storages = @Storage("PromptRefinerSettings.xml")
)
public final class PluginSettings implements PersistentStateComponent<PluginSettings.State> {

    public static class State {
        public String endpoint = "http://localhost:11434";
        public String model = "llama3.2:3b";
        public int timeoutSeconds = 30;
        public double temperature = 0.3;
    }

    private State state = new State();

    public static PluginSettings getInstance() {
        return ApplicationManager.getApplication().getService(PluginSettings.class);
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State newState) {
        XmlSerializerUtil.copyBean(newState, this.state);
    }

    public String getEndpoint() {
        return state.endpoint;
    }

    public void setEndpoint(String endpoint) {
        state.endpoint = endpoint;
    }

    public String getModel() {
        return state.model;
    }

    public void setModel(String model) {
        state.model = model;
    }

    public int getTimeoutSeconds() {
        return state.timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        state.timeoutSeconds = timeoutSeconds;
    }

    public double getTemperature() {
        return state.temperature;
    }

    public void setTemperature(double temperature) {
        state.temperature = temperature;
    }
}
