package com.example.swiftshare.presentation.discovery.viewmodels;

import com.example.swiftshare.domain.repository.SettingsRepository;
import com.example.swiftshare.domain.usecase.discovery.ObserveNearbyDevicesUseCase;
import com.example.swiftshare.domain.usecase.discovery.StartDiscoveryUseCase;
import com.example.swiftshare.domain.usecase.discovery.StopDiscoveryUseCase;
import com.example.swiftshare.domain.usecase.pairing.RequestConnectionUseCase;
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
public final class DiscoveryViewModel_Factory implements Factory<DiscoveryViewModel> {
  private final Provider<StartDiscoveryUseCase> startDiscoveryUseCaseProvider;

  private final Provider<StopDiscoveryUseCase> stopDiscoveryUseCaseProvider;

  private final Provider<ObserveNearbyDevicesUseCase> observeNearbyDevicesUseCaseProvider;

  private final Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public DiscoveryViewModel_Factory(Provider<StartDiscoveryUseCase> startDiscoveryUseCaseProvider,
      Provider<StopDiscoveryUseCase> stopDiscoveryUseCaseProvider,
      Provider<ObserveNearbyDevicesUseCase> observeNearbyDevicesUseCaseProvider,
      Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.startDiscoveryUseCaseProvider = startDiscoveryUseCaseProvider;
    this.stopDiscoveryUseCaseProvider = stopDiscoveryUseCaseProvider;
    this.observeNearbyDevicesUseCaseProvider = observeNearbyDevicesUseCaseProvider;
    this.requestConnectionUseCaseProvider = requestConnectionUseCaseProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public DiscoveryViewModel get() {
    return newInstance(startDiscoveryUseCaseProvider.get(), stopDiscoveryUseCaseProvider.get(), observeNearbyDevicesUseCaseProvider.get(), requestConnectionUseCaseProvider.get(), settingsRepositoryProvider.get());
  }

  public static DiscoveryViewModel_Factory create(
      Provider<StartDiscoveryUseCase> startDiscoveryUseCaseProvider,
      Provider<StopDiscoveryUseCase> stopDiscoveryUseCaseProvider,
      Provider<ObserveNearbyDevicesUseCase> observeNearbyDevicesUseCaseProvider,
      Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new DiscoveryViewModel_Factory(startDiscoveryUseCaseProvider, stopDiscoveryUseCaseProvider, observeNearbyDevicesUseCaseProvider, requestConnectionUseCaseProvider, settingsRepositoryProvider);
  }

  public static DiscoveryViewModel newInstance(StartDiscoveryUseCase startDiscoveryUseCase,
      StopDiscoveryUseCase stopDiscoveryUseCase,
      ObserveNearbyDevicesUseCase observeNearbyDevicesUseCase,
      RequestConnectionUseCase requestConnectionUseCase, SettingsRepository settingsRepository) {
    return new DiscoveryViewModel(startDiscoveryUseCase, stopDiscoveryUseCase, observeNearbyDevicesUseCase, requestConnectionUseCase, settingsRepository);
  }
}
