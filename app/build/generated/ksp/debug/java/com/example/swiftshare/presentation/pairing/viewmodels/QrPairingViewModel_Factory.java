package com.example.swiftshare.presentation.pairing.viewmodels;

import com.example.swiftshare.data.qr.QrCodeGenerator;
import com.example.swiftshare.domain.repository.NearbyRepository;
import com.example.swiftshare.domain.usecase.pairing.RequestConnectionUseCase;
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
public final class QrPairingViewModel_Factory implements Factory<QrPairingViewModel> {
  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  private final Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider;

  private final Provider<QrCodeGenerator> qrCodeGeneratorProvider;

  public QrPairingViewModel_Factory(Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider,
      Provider<QrCodeGenerator> qrCodeGeneratorProvider) {
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
    this.requestConnectionUseCaseProvider = requestConnectionUseCaseProvider;
    this.qrCodeGeneratorProvider = qrCodeGeneratorProvider;
  }

  @Override
  public QrPairingViewModel get() {
    return newInstance(nearbyRepositoryProvider.get(), requestConnectionUseCaseProvider.get(), qrCodeGeneratorProvider.get());
  }

  public static QrPairingViewModel_Factory create(
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<RequestConnectionUseCase> requestConnectionUseCaseProvider,
      Provider<QrCodeGenerator> qrCodeGeneratorProvider) {
    return new QrPairingViewModel_Factory(nearbyRepositoryProvider, requestConnectionUseCaseProvider, qrCodeGeneratorProvider);
  }

  public static QrPairingViewModel newInstance(NearbyRepository nearbyRepository,
      RequestConnectionUseCase requestConnectionUseCase, QrCodeGenerator qrCodeGenerator) {
    return new QrPairingViewModel(nearbyRepository, requestConnectionUseCase, qrCodeGenerator);
  }
}
