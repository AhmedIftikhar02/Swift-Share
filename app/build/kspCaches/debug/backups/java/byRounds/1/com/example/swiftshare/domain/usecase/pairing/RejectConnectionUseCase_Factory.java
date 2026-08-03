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
public final class RejectConnectionUseCase_Factory implements Factory<RejectConnectionUseCase> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  public RejectConnectionUseCase_Factory(Provider<NearbyRepository> nearbyRepositoryProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
  }

  @Override
  public RejectConnectionUseCase get() {
    return newInstance(nearbyRepositoryProvider.get());
  }

  public static RejectConnectionUseCase_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider) {
    return new RejectConnectionUseCase_Factory(nearbyRepositoryProvider);
  }

  public static RejectConnectionUseCase newInstance(NearbyRepository nearbyRepository) {
    return new RejectConnectionUseCase(nearbyRepository);
  }
}
