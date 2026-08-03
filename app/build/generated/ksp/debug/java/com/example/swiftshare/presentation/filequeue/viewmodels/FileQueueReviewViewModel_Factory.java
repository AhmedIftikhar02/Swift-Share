package com.example.swiftshare.presentation.filequeue.viewmodels;

import com.example.swiftshare.domain.repository.TransferRepository;
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
public final class FileQueueReviewViewModel_Factory implements Factory<FileQueueReviewViewModel> {
  private final Provider<TransferRepository> transferRepositoryProvider;

  private final Provider<BuildTransferQueueUseCase> buildTransferQueueUseCaseProvider;

  public FileQueueReviewViewModel_Factory(Provider<TransferRepository> transferRepositoryProvider,
      Provider<BuildTransferQueueUseCase> buildTransferQueueUseCaseProvider) {
    this.transferRepositoryProvider = transferRepositoryProvider;
    this.buildTransferQueueUseCaseProvider = buildTransferQueueUseCaseProvider;
  }

  @Override
  public FileQueueReviewViewModel get() {
    return newInstance(transferRepositoryProvider.get(), buildTransferQueueUseCaseProvider.get());
  }

  public static FileQueueReviewViewModel_Factory create(
      Provider<TransferRepository> transferRepositoryProvider,
      Provider<BuildTransferQueueUseCase> buildTransferQueueUseCaseProvider) {
    return new FileQueueReviewViewModel_Factory(transferRepositoryProvider, buildTransferQueueUseCaseProvider);
  }

  public static FileQueueReviewViewModel newInstance(TransferRepository transferRepository,
      BuildTransferQueueUseCase buildTransferQueueUseCase) {
    return new FileQueueReviewViewModel(transferRepository, buildTransferQueueUseCase);
  }
}
