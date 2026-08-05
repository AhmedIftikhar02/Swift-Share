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
public final class ResumeTransferUseCase_Factory implements Factory<ResumeTransferUseCase> {
  private final Provider<TransferRepository> transferRepositoryProvider;

  public ResumeTransferUseCase_Factory(Provider<TransferRepository> transferRepositoryProvider) {
    this.transferRepositoryProvider = transferRepositoryProvider;
  }

  @Override
  public ResumeTransferUseCase get() {
    return newInstance(transferRepositoryProvider.get());
  }

  public static ResumeTransferUseCase_Factory create(
      Provider<TransferRepository> transferRepositoryProvider) {
    return new ResumeTransferUseCase_Factory(transferRepositoryProvider);
  }

  public static ResumeTransferUseCase newInstance(TransferRepository transferRepository) {
    return new ResumeTransferUseCase(transferRepository);
  }
}
