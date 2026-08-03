package com.example.swiftshare.common.managers;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ThemeManager_Factory implements Factory<ThemeManager> {
  private final Provider<SharedPrefsManager> sharedPrefsManagerProvider;

  public ThemeManager_Factory(Provider<SharedPrefsManager> sharedPrefsManagerProvider) {
    this.sharedPrefsManagerProvider = sharedPrefsManagerProvider;
  }

  @Override
  public ThemeManager get() {
    return newInstance(sharedPrefsManagerProvider.get());
  }

  public static ThemeManager_Factory create(
      Provider<SharedPrefsManager> sharedPrefsManagerProvider) {
    return new ThemeManager_Factory(sharedPrefsManagerProvider);
  }

  public static ThemeManager newInstance(SharedPrefsManager sharedPrefsManager) {
    return new ThemeManager(sharedPrefsManager);
  }
}
