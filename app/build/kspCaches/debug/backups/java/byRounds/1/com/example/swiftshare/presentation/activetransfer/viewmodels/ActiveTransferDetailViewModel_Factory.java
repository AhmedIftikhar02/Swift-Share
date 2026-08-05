package com.example.swiftshare.presentation.activetransfer.viewmodels;

import com.example.swiftshare.domain.usecase.transfer.CancelTransferUseCase;
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase;
import com.example.swiftshare.domain.usecase.transfer.PauseTransferUseCase;
import com.example.swiftshare.domain.usecase.transfer.ResumeTransferUseCase;
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

  private final Provider<PauseTransferUseCase> pauseTransferUseCaseProvider;

  private final Provider<ResumeTransferUseCase> resumeTransferUseCaseProvider;

  public ActiveTransferDetailViewModel_Factory(
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider,
      Provider<CancelTransferUseCase> cancelTransferUseCaseProvider,
      Provider<PauseTransferUseCase> pauseTransferUseCaseProvider,
      Provider<ResumeTransferUseCase> resumeTransferUseCaseProvider) {
    this.observeActiveSessionUseCaseProvider = observeActiveSessionUseCaseProvider;
    this.cancelTransferUseCaseProvider = cancelTransferUseCaseProvider;
    this.pauseTransferUseCaseProvider = pauseTransferUseCaseProvider;
    this.resumeTransferUseCaseProvider = resumeTransferUseCaseProvider;
  }

  @Override
  public ActiveTransferDetailViewModel get() {
    return newInstance(observeActiveSessionUseCaseProvider.get(), cancelTransferUseCaseProvider.get(), pauseTransferUseCaseProvider.get(), resumeTransferUseCaseProvider.get());
  }

  public static ActiveTransferDetailViewModel_Factory create(
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider,
      Provider<CancelTransferUseCase> cancelTransferUseCaseProvider,
      Provider<PauseTransferUseCase> pauseTransferUseCaseProvider,
      Provider<ResumeTransferUseCase> resumeTransferUseCaseProvider) {
    return new ActiveTransferDetailViewModel_Factory(observeActiveSessionUseCaseProvider, cancelTransferUseCaseProvider, pauseTransferUseCaseProvider, resumeTransferUseCaseProvider);
  }

  public static ActiveTransferDetailViewModel newInstance(
      ObserveActiveSessionUseCase observeActiveSessionUseCase,
      CancelTransferUseCase cancelTransferUseCase, PauseTransferUseCase pauseTransferUseCase,
      ResumeTransferUseCase resumeTransferUseCase) {
    return new ActiveTransferDetailViewModel(observeActiveSessionUseCase, cancelTransferUseCase, pauseTransferUseCase, resumeTransferUseCase);
  }
}
