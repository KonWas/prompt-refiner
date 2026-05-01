package com.github.konwas.promptrefiner.ui;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class ResultDialog extends DialogWrapper {

    private final String refinedPrompt;

    public ResultDialog(@Nullable Project project, String refinedPrompt) {
        super(project);
        this.refinedPrompt = refinedPrompt;
        setTitle("Refined Prompt");
        setOKButtonText("Close");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(640, 400));

        JTextArea textArea = new JTextArea(refinedPrompt);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);

        panel.add(new JBScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        Action copyAction = new AbstractAction("Copy to Clipboard") {
            @Override
            public void actionPerformed(ActionEvent e) {
                CopyPasteManager.getInstance().setContents(new StringSelection(refinedPrompt));
            }
        };
        return new Action[]{copyAction, getOKAction()};
    }
}
