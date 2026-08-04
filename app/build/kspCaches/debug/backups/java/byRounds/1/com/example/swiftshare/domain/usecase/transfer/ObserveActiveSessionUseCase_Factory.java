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
public final class ObserveActiveSessionUseCase_Factory implements Factory<ObserveActiveSessionUseCase> {
  private final Provider<TransferRepository> transferRepositoryProvider;

  public ObserveActiveSessionUseCase_Factory(
      Provider<TransferRepository> transferRepositoryProvider) {
    this.transferRepositoryProvider = transferRepositoryProvider;
  }

  @Override
  public ObserveActiveSessionUseCase get() {
    return newInstance(transferRepositoryProvider.get());
  }

  public static ObserveActiveSessionUseCase_Factory create(
      Provider<TransferRepository> transferRepositoryProvider) {
    return new ObserveActiveSessionUseCase_Factory(transferRepositoryProvider);
  }

  public static ObserveActiveSessionUseCase newInstance(TransferRepository transferRepository) {
    return new ObserveActiveSessionUseCase(transferRepository);
  }
}
