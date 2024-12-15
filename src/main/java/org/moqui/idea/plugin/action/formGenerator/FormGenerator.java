package org.moqui.idea.plugin.action.formGenerator;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.AbstractIndex;

import java.util.Optional;

public interface FormGenerator {
    <T extends AbstractIndex> Optional<String> generatorFormSingle(@NotNull T indexItem, @NotNull FormSingleGenerateType generateType);
    <T extends AbstractIndex> Optional<String> generatorFormList(@NotNull T indexItem);
}
