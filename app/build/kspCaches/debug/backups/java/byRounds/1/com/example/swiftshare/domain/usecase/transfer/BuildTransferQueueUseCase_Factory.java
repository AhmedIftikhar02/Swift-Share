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
public final class BuildTransferQueueUseCase_Factory implements Factory<BuildTransferQueueUseCase> {
  private final Provider<TransferRepository> transferRepositoryProvider;

  public BuildTransferQueueUseCase_Factory(
      Provider<TransferRepository> transferRepositoryProvider) {
    this.transferRepositoryProvider = transferRepositoryProvider;
  }

  @Override
  public BuildTransferQueueUseCase get() {
    return newInstance(transferRepositoryProvider.get());
  }

  public static BuildTransferQueueUseCase_Factory create(
      Provider<TransferRepository> transferRepositoryProvider) {
    return new BuildTransferQueueUseCase_Factory(transferRepositoryProvider);
  }

  public static BuildTransferQueueUseCase newInstance(TransferRepository transferRepository) {
    return new BuildTransferQueueUseCase(transferRepository);
  }
}
