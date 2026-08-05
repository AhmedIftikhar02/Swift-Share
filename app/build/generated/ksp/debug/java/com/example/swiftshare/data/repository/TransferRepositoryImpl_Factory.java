package com.example.swiftshare.data.repository;

import com.example.swiftshare.common.providers.DispatcherProvider;
import com.example.swiftshare.data.transfer.FileMetadataResolver;
import com.example.swiftshare.data.transfer.FileTransferDataSource;
import com.example.swiftshare.data.transfer.SourceFileValidator;
import com.example.swiftshare.domain.repository.HistoryRepository;
import com.example.swiftshare.domain.repository.NearbyRepository;
import com.example.swiftshare.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TransferRepositoryImpl_Factory implements Factory<TransferRepositoryImpl> {
  private final Provider<FileMetadataResolver> fileMetadataResolverProvider;

  private final Provider<FileTransferDataSource> fileTransferDataSourceProvider;

  private final Provider<SourceFileValidator> sourceFileValidatorProvider;

  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  public TransferRepositoryImpl_Factory(Provider<FileMetadataResolver> fileMetadataResolverProvider,
      Provider<FileTransferDataSource> fileTransferDataSourceProvider,
      Provider<SourceFileValidator> sourceFileValidatorProvider,
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    this.fileMetadataResolverProvider = fileMetadataResolverProvider;
    this.fileTransferDataSourceProvider = fileTransferDataSourceProvider;
    this.sourceFileValidatorProvider = sourceFileValidatorProvider;
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public TransferRepositoryImpl get() {
    return newInstance(fileMetadataResolverProvider.get(), fileTransferDataSourceProvider.get(), sourceFileValidatorProvider.get(), nearbyRepositoryProvider.get(), historyRepositoryProvider.get(), settingsRepositoryProvider.get(), dispatcherProvider.get());
  }

  public static TransferRepositoryImpl_Factory create(
      Provider<FileMetadataResolver> fileMetadataResolverProvider,
      Provider<FileTransferDataSource> fileTransferDataSourceProvider,
      Provider<SourceFileValidator> sourceFileValidatorProvider,
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    return new TransferRepositoryImpl_Factory(fileMetadataResolverProvider, fileTransferDataSourceProvider, sourceFileValidatorProvider, nearbyRepositoryProvider, historyRepositoryProvider, settingsRepositoryProvider, dispatcherProvider);
  }

  public static TransferRepositoryImpl newInstance(FileMetadataResolver fileMetadataResolver,
      FileTransferDataSource fileTransferDataSource, SourceFileValidator sourceFileValidator,
      NearbyRepository nearbyRepository, HistoryRepository historyRepository,
      SettingsRepository settingsRepository, DispatcherProvider dispatcherProvider) {
    return new TransferRepositoryImpl(fileMetadataResolver, fileTransferDataSource, sourceFileValidator, nearbyRepository, historyRepository, settingsRepository, dispatcherProvider);
  }
}
