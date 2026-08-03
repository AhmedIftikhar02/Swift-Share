package com.example.swiftshare.presentation.pairing.viewmodels;

import com.example.swiftshare.domain.repository.NearbyRepository;
import com.example.swiftshare.domain.usecase.pairing.AcceptConnectionUseCase;
import com.example.swiftshare.domain.usecase.pairing.RejectConnectionUseCase;
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
public final class ConnectionConfirmationViewModel_Factory implements Factory<ConnectionConfirmationViewModel> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  private final Provider<AcceptConnectionUseCase> acceptConnectionUseCaseProvider;

  private final Provider<RejectConnectionUseCase> rejectConnectionUseCaseProvider;

  public ConnectionConfirmationViewModel_Factory(
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<AcceptConnectionUseCase> acceptConnectionUseCaseProvider,
      Provider<RejectConnectionUseCase> rejectConnectionUseCaseProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
    this.acceptConnectionUseCaseProvider = acceptConnectionUseCaseProvider;
    this.rejectConnectionUseCaseProvider = rejectConnectionUseCaseProvider;
  }

  @Override
  public ConnectionConfirmationViewModel get() {
    return newInstance(nearbyRepositoryProvider.get(), acceptConnectionUseCaseProvider.get(), rejectConnectionUseCaseProvider.get());
  }

  public static ConnectionConfirmationViewModel_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<AcceptConnectionUseCase> acceptConnectionUseCaseProvider,
      Provider<RejectConnectionUseCase> rejectConnectionUseCaseProvider) {
    return new ConnectionConfirmationViewModel_Factory(nearbyRepositoryProvider, acceptConnectionUseCaseProvider, rejectConnectionUseCaseProvider);
  }

  public static ConnectionConfirmationViewModel newInstance(NearbyRepository nearbyRepository,
      AcceptConnectionUseCase acceptConnectionUseCase,
      RejectConnectionUseCase rejectConnectionUseCase) {
    return new ConnectionConfirmationViewModel(nearbyRepository, acceptConnectionUseCase, rejectConnectionUseCase);
  }
}
