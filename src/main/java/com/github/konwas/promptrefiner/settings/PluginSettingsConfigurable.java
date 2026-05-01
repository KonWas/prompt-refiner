package com.github.konwas.promptrefiner.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class PluginSettingsConfigurable implements Configurable {

    private JBTextField endpointField;
    private JBTextField modelField;
    private JBTextField timeoutField;
    private JBTextField temperatureField;
    private JPanel panel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Prompt Refiner";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        endpointField = new JBTextField();
        modelField = new JBTextField();
        timeoutField = new JBTextField();
        temperatureField = new JBTextField();

        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Endpoint:"), endpointField, 1, false)
                .addLabeledComponent(new JBLabel("Model:"), modelField, 1, false)
                .addLabeledComponent(new JBLabel("Timeout (seconds):"), timeoutField, 1, false)
                .addLabeledComponent(new JBLabel("Temperature:"), temperatureField, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        PluginSettings settings = PluginSettings.getInstance();
        return !endpointField.getText().equals(settings.getEndpoint())
                || !modelField.getText().equals(settings.getModel())
                || !timeoutField.getText().equals(String.valueOf(settings.getTimeoutSeconds()))
                || !temperatureField.getText().equals(String.valueOf(settings.getTemperature()));
    }

    @Override
    public void apply() throws ConfigurationException {
        int timeout;
        double temperature;
        try {
            timeout = Integer.parseInt(timeoutField.getText().trim());
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Timeout must be an integer (seconds).");
        }
        if (timeout <= 0) {
            throw new ConfigurationException("Timeout must be greater than zero.");
        }
        try {
            temperature = Double.parseDouble(temperatureField.getText().trim());
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Temperature must be a number (e.g., 0.3).");
        }

        PluginSettings settings = PluginSettings.getInstance();
        settings.setEndpoint(endpointField.getText().trim());
        settings.setModel(modelField.getText().trim());
        settings.setTimeoutSeconds(timeout);
        settings.setTemperature(temperature);
    }

    @Override
    public void reset() {
        PluginSettings settings = PluginSettings.getInstance();
        endpointField.setText(settings.getEndpoint());
        modelField.setText(settings.getModel());
        timeoutField.setText(String.valueOf(settings.getTimeoutSeconds()));
        temperatureField.setText(String.valueOf(settings.getTemperature()));
    }

    @Override
    public void disposeUIResources() {
        endpointField = null;
        modelField = null;
        timeoutField = null;
        temperatureField = null;
        panel = null;
    }
}
