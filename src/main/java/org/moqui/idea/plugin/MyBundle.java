// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.moqui.idea.plugin;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public final class MyBundle {
  private static final String BUNDLE = "messages.CodeBundle";
  private static final DynamicBundle INSTANCE = new DynamicBundle(MyBundle.class, BUNDLE);

  private MyBundle() {}

  public static @NotNull @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,  @NotNull Object ... params) {
    return INSTANCE.getMessage(key, params);
  }

  public static @NotNull Supplier<String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,@NotNull  Object ... params) {
    return INSTANCE.getLazyMessage(key, params);
  }
}