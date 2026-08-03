package com.example.swiftshare.presentation.pairing.viewmodels;

import com.example.swiftshare.domain.repository.NearbyRepository;
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
public final class PinPairingViewModel_Factory implements Factory<PinPairingViewModel> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  private final Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider;

  public PinPairingViewModel_Factory(Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
    this.requestConnectionUseCaseProvider = requestConnectionUseCaseProvider;
  }

  @Override
  public PinPairingViewModel get() {
    return newInstance(nearbyRepositoryProvider.get(), requestConnectionUseCaseProvider.get());
  }

  public static PinPairingViewModel_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider) {
    return new PinPairingViewModel_Factory(nearbyRepositoryProvider, requestConnectionUseCaseProvider);
  }

  public static PinPairingViewModel newInstance(NearbyRepository nearbyRepository,
      RequestConnectionUseCase requestConnectionUseCase) {
    return new PinPairingViewModel(nearbyRepository, requestConnectionUseCase);
  }
}
