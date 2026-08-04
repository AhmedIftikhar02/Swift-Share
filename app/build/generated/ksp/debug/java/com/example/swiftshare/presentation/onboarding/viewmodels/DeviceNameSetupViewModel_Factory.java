package com.example.swiftshare.presentation.onboarding.viewmodels;

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
public final class DeviceNameSetupViewModel_Factory implements Factory<DeviceNameSetupViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public DeviceNameSetupViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public DeviceNameSetupViewModel get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static DeviceNameSetupViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new DeviceNameSetupViewModel_Factory(settingsRepositoryProvider);
  }

  public static DeviceNameSetupViewModel newInstance(SettingsRepository settingsRepository) {
    return new DeviceNameSetupViewModel(settingsRepository);
  }
}
