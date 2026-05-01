package com.github.konwas.promptrefiner.action;

import com.github.konwas.promptrefiner.context.EditorContext;
import com.github.konwas.promptrefiner.context.EditorContextCollector;
import com.github.konwas.promptrefiner.llm.LLMClient;
import com.github.konwas.promptrefiner.llm.LLMClientFactory;
import com.github.konwas.promptrefiner.llm.LLMException;
import com.github.konwas.promptrefiner.llm.LLMRequest;
import com.github.konwas.promptrefiner.llm.LLMResponse;
import com.github.konwas.promptrefiner.prompt.PromptBuilder;
import com.github.konwas.promptrefiner.settings.PluginSettings;
import com.github.konwas.promptrefiner.ui.InputDialog;
import com.github.konwas.promptrefiner.ui.ResultDialog;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class RefinePromptAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(RefinePromptAction.class);

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        EditorContext context = new EditorContextCollector().collect(editor);

        InputDialog inputDialog = new InputDialog(project);
        if (!inputDialog.showAndGet()) {
            return;
        }

        String userQuestion = inputDialog.getQuestion();
        if (userQuestion.isEmpty()) {
            Messages.showWarningDialog(project, "Please enter a question.", "Prompt Refiner");
            return;
        }

        PluginSettings settings = PluginSettings.getInstance();
        LLMRequest request = new PromptBuilder().build(
                context, userQuestion, settings.getModel(), settings.getTemperature());

        AtomicReference<String> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Task.Backgroundable task = new Task.Backgroundable(project, "Refining prompt...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    LLMClient client = LLMClientFactory.create();
                    LLMResponse response = client.refine(request);
                    result.set(response.text());
                } catch (Throwable t) {
                    failure.set(t);
                }
            }

            @Override
            public void onSuccess() {
                if (failure.get() != null) {
                    showErrorDialog(project, failure.get());
                    return;
                }
                String refined = result.get();
                if (refined == null || refined.isBlank()) {
                    Messages.showWarningDialog(project, "The model returned an empty response.", "Prompt Refiner");
                    return;
                }
                new ResultDialog(project, refined.trim()).show();
            }
        };

        ProgressManager.getInstance().run(task);
    }

    private void showErrorDialog(Project project, Throwable t) {
        LOG.warn("Prompt refinement failed", t);
        String message = (t instanceof LLMException llm)
                ? llm.getUserMessage()
                : "Unexpected error: "
                        + (t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName())
                        + ". See idea.log for details.";
        Messages.showErrorDialog(project, message, "Prompt Refiner");
    }
}
