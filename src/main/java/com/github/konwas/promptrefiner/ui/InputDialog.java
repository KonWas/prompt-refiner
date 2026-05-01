package com.github.konwas.promptrefiner.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class InputDialog extends DialogWrapper {

    private JTextArea textArea;

    public InputDialog(@Nullable Project project) {
        super(project);
        setTitle("Refine Prompt with AI");
        setOKButtonText("Refine");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setPreferredSize(new Dimension(520, 200));

        JBLabel label = new JBLabel("What do you want to ask the AI about this code?");
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JBScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return textArea;
    }

    public String getQuestion() {
        return textArea.getText().trim();
    }
}
