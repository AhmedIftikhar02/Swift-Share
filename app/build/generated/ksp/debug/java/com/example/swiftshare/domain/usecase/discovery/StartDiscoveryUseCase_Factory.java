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
public final class StartDiscoveryUseCase_Factory implements Factory<StartDiscoveryUseCase> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  public StartDiscoveryUseCase_Factory(Provider<NearbyRepository> nearbyRepositoryProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
  }

  @Override
  public StartDiscoveryUseCase get() {
    return newInstance(nearbyRepositoryProvider.get());
  }

  public static StartDiscoveryUseCase_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider) {
    return new StartDiscoveryUseCase_Factory(nearbyRepositoryProvider);
  }

  public static StartDiscoveryUseCase newInstance(NearbyRepository nearbyRepository) {
    return new StartDiscoveryUseCase(nearbyRepository);
  }
}
