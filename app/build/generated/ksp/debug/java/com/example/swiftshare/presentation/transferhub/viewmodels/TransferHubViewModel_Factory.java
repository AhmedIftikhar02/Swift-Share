package com.example.swiftshare.presentation.transferhub.viewmodels;

import com.example.swiftshare.domain.repository.NearbyRepository;
import com.example.swiftshare.domain.usecase.transfer.BuildTransferQueueUseCase;
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
public final class TransferHubViewModel_Factory implements Factory<TransferHubViewModel> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  private final Provider<BuildTransferQueueUseCase> buildTransferQueueUseCaseProvider;

  public TransferHubViewModel_Factory(Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<BuildTransferQueueUseCase> buildTransferQueueUseCaseProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
    this.buildTransferQueueUseCaseProvider = buildTransferQueueUseCaseProvider;
  }

  @Override
  public TransferHubViewModel get() {
    return newInstance(nearbyRepositoryProvider.get(), buildTransferQueueUseCaseProvider.get());
  }

  public static TransferHubViewModel_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<BuildTransferQueueUseCase> buildTransferQueueUseCaseProvider) {
    return new TransferHubViewModel_Factory(nearbyRepositoryProvider, buildTransferQueueUseCaseProvider);
  }

  public static TransferHubViewModel newInstance(NearbyRepository nearbyRepository,
      BuildTransferQueueUseCase buildTransferQueueUseCase) {
    return new TransferHubViewModel(nearbyRepository, buildTransferQueueUseCase);
  }
}
