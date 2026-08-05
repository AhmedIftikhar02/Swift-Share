package com.example.swiftshare.presentation.completion.viewmodels;

import androidx.lifecycle.SavedStateHandle;
import com.example.swiftshare.domain.repository.HistoryRepository;
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase;
import com.example.swiftshare.domain.usecase.transfer.RetryTransferUseCase;
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
public final class CompletionViewModel_Factory implements Factory<CompletionViewModel> {
  private final Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<RetryTransferUseCase> retryTransferUseCaseProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public CompletionViewModel_Factory(
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<RetryTransferUseCase> retryTransferUseCaseProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.observeActiveSessionUseCaseProvider = observeActiveSessionUseCaseProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.retryTransferUseCaseProvider = retryTransferUseCaseProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public CompletionViewModel get() {
    return newInstance(observeActiveSessionUseCaseProvider.get(), historyRepositoryProvider.get(), retryTransferUseCaseProvider.get(), savedStateHandleProvider.get());
  }

  public static CompletionViewModel_Factory create(
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<RetryTransferUseCase> retryTransferUseCaseProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new CompletionViewModel_Factory(observeActiveSessionUseCaseProvider, historyRepositoryProvider, retryTransferUseCaseProvider, savedStateHandleProvider);
  }

  public static CompletionViewModel newInstance(
      ObserveActiveSessionUseCase observeActiveSessionUseCase, HistoryRepository historyRepository,
      RetryTransferUseCase retryTransferUseCase, SavedStateHandle savedStateHandle) {
    return new CompletionViewModel(observeActiveSessionUseCase, historyRepository, retryTransferUseCase, savedStateHandle);
  }
}
