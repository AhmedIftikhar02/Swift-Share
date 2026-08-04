package com.example.swiftshare.presentation.activetransfer.viewmodels;

import com.example.swiftshare.domain.usecase.transfer.CancelTransferUseCase;
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase;
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
public final class ActiveTransferDetailViewModel_Factory implements Factory<ActiveTransferDetailViewModel> {
  private final Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider;

  private final Provider<CancelTransferUseCase> cancelTransferUseCaseProvider;

  public ActiveTransferDetailViewModel_Factory(
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider,
      Provider<CancelTransferUseCase> cancelTransferUseCaseProvider) {
    this.observeActiveSessionUseCaseProvider = observeActiveSessionUseCaseProvider;
    this.cancelTransferUseCaseProvider = cancelTransferUseCaseProvider;
  }

  @Override
  public ActiveTransferDetailViewModel get() {
    return newInstance(observeActiveSessionUseCaseProvider.get(), cancelTransferUseCaseProvider.get());
  }

  public static ActiveTransferDetailViewModel_Factory create(
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider,
      Provider<CancelTransferUseCase> cancelTransferUseCaseProvider) {
    return new ActiveTransferDetailViewModel_Factory(observeActiveSessionUseCaseProvider, cancelTransferUseCaseProvider);
  }

  public static ActiveTransferDetailViewModel newInstance(
      ObserveActiveSessionUseCase observeActiveSessionUseCase,
      CancelTransferUseCase cancelTransferUseCase) {
    return new ActiveTransferDetailViewModel(observeActiveSessionUseCase, cancelTransferUseCase);
  }
}
