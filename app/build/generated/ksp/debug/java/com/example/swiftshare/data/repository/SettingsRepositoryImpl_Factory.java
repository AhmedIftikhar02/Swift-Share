package com.example.swiftshare.data.repository;

import com.example.swiftshare.data.local.AppPreferences;
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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  private final Provider<AppPreferences> appPreferencesProvider;

  public SettingsRepositoryImpl_Factory(Provider<AppPreferences> appPreferencesProvider) {
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public SettingsRepositoryImpl get() {
    return newInstance(appPreferencesProvider.get());
  }

  public static SettingsRepositoryImpl_Factory create(
      Provider<AppPreferences> appPreferencesProvider) {
    return new SettingsRepositoryImpl_Factory(appPreferencesProvider);
  }

  public static SettingsRepositoryImpl newInstance(AppPreferences appPreferences) {
    return new SettingsRepositoryImpl(appPreferences);
  }
}
