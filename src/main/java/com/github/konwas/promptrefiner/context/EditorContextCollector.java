package com.github.konwas.promptrefiner.context;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

public class EditorContextCollector {

    public EditorContext collect(Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        String selected = selectionModel.getSelectedText();
        String code = (selected != null && !selected.isEmpty())
                ? selected
                : editor.getDocument().getText();

        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());

        String fileName = file != null ? file.getName() : "untitled";
        String language = resolveLanguage(file);

        return new EditorContext(code, fileName, language);
    }

    private String resolveLanguage(VirtualFile file) {
        if (file == null) {
            return "text";
        }
        String ext = file.getExtension();
        if (ext != null && !ext.isEmpty()) {
            return ext;
        }
        if (file.getFileType() != null) {
            return file.getFileType().getName().toLowerCase();
        }
        return "text";
    }
}
