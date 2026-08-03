package com.example.swiftshare.domain.usecase.pairing;

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
public final class AcceptConnectionUseCase_Factory implements Factory<AcceptConnectionUseCase> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  public AcceptConnectionUseCase_Factory(Provider<NearbyRepository> nearbyRepositoryProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
  }

  @Override
  public AcceptConnectionUseCase get() {
    return newInstance(nearbyRepositoryProvider.get());
  }

  public static AcceptConnectionUseCase_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider) {
    return new AcceptConnectionUseCase_Factory(nearbyRepositoryProvider);
  }

  public static AcceptConnectionUseCase newInstance(NearbyRepository nearbyRepository) {
    return new AcceptConnectionUseCase(nearbyRepository);
  }
}
