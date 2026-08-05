package com.example.swiftshare.domain.usecase.transfer;

import com.example.swiftshare.domain.repository.TransferRepository;
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
public final class RetryTransferUseCase_Factory implements Factory<RetryTransferUseCase> {
  private final Provider<TransferRepository> transferRepositoryProvider;

  public RetryTransferUseCase_Factory(Provider<TransferRepository> transferRepositoryProvider) {
    this.transferRepositoryProvider = transferRepositoryProvider;
  }

  @Override
  public RetryTransferUseCase get() {
    return newInstance(transferRepositoryProvider.get());
  }

  public static RetryTransferUseCase_Factory create(
      Provider<TransferRepository> transferRepositoryProvider) {
    return new RetryTransferUseCase_Factory(transferRepositoryProvider);
  }

  public static RetryTransferUseCase newInstance(TransferRepository transferRepository) {
    return new RetryTransferUseCase(transferRepository);
  }
}
