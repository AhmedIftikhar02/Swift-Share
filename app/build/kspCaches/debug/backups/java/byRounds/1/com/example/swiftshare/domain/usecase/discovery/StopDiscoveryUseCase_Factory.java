package com.example.swiftshare.domain.usecase.discovery;

import com.example.swiftshare.domain.repository.NearbyRepository;
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
public final class StopDiscoveryUseCase_Factory implements Factory<StopDiscoveryUseCase> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  public StopDiscoveryUseCase_Factory(Provider<NearbyRepository> nearbyRepositoryProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
  }

  @Override
  public StopDiscoveryUseCase get() {
    return newInstance(nearbyRepositoryProvider.get());
  }

  public static StopDiscoveryUseCase_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider) {
    return new StopDiscoveryUseCase_Factory(nearbyRepositoryProvider);
  }

  public static StopDiscoveryUseCase newInstance(NearbyRepository nearbyRepository) {
    return new StopDiscoveryUseCase(nearbyRepository);
  }
}
