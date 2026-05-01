package com.github.konwas.promptrefiner.prompt;

import com.github.konwas.promptrefiner.context.EditorContext;
import com.github.konwas.promptrefiner.llm.LLMRequest;

public class PromptBuilder {
    public static final String SYSTEM_PROMPT = """
            You are a prompt-refining assistant for software developers. The developer will give you a rough question and a snippet of code with file name and language. Rewrite the question as a clear, structured prompt that includes the relevant code context, makes the developer's intent explicit, and gives a downstream coding assistant everything it needs to answer well in one shot.

            Output the refined prompt and nothing else - no preamble, no "Here is your refined prompt:". Just the prompt itself, ready to paste.

            Format:
            - Start with one sentence stating what the developer wants.
            - Include the code in a fenced code block with the language tag.
            - End with any constraints or success criteria you can infer.
            """;

    public LLMRequest build(EditorContext context, String userQuestion, String model, double temperature) {
        String userPrompt = "Rough question:\n" + userQuestion + "\n\n"
                + "File: " + context.fileName() + "\n"
                + "Language: " + context.language() + "\n"
                + "Code:\n"
                + "```" + context.language() + "\n"
                + context.code() + "\n"
                + "```\n";
        return new LLMRequest(SYSTEM_PROMPT, userPrompt, model, temperature);
    }
}
