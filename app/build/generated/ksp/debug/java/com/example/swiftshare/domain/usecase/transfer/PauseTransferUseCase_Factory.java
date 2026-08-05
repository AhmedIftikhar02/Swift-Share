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
public final class PauseTransferUseCase_Factory implements Factory<PauseTransferUseCase> {
  private final Provider<TransferRepository> transferRepositoryProvider;

  public PauseTransferUseCase_Factory(Provider<TransferRepository> transferRepositoryProvider) {
    this.transferRepositoryProvider = transferRepositoryProvider;
  }

  @Override
  public PauseTransferUseCase get() {
    return newInstance(transferRepositoryProvider.get());
  }

  public static PauseTransferUseCase_Factory create(
      Provider<TransferRepository> transferRepositoryProvider) {
    return new PauseTransferUseCase_Factory(transferRepositoryProvider);
  }

  public static PauseTransferUseCase newInstance(TransferRepository transferRepository) {
    return new PauseTransferUseCase(transferRepository);
  }
}
