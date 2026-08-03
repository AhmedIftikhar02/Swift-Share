package com.example.swiftshare.presentation.splash.viewmodels;

import com.example.swiftshare.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public SplashViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static SplashViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new SplashViewModel_Factory(settingsRepositoryProvider);
  }

  public static SplashViewModel newInstance(SettingsRepository settingsRepository) {
    return new SplashViewModel(settingsRepository);
  }
}
