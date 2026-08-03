package com.example.swiftshare.data.repository;

import com.example.swiftshare.data.transfer.FileMetadataResolver;
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

  public TransferRepositoryImpl_Factory(
      Provider<FileMetadataResolver> fileMetadataResolverProvider) {
    this.fileMetadataResolverProvider = fileMetadataResolverProvider;
  }

  @Override
  public TransferRepositoryImpl get() {
    return newInstance(fileMetadataResolverProvider.get());
  }

  public static TransferRepositoryImpl_Factory create(
      Provider<FileMetadataResolver> fileMetadataResolverProvider) {
    return new TransferRepositoryImpl_Factory(fileMetadataResolverProvider);
  }

  public static TransferRepositoryImpl newInstance(FileMetadataResolver fileMetadataResolver) {
    return new TransferRepositoryImpl(fileMetadataResolver);
  }
}
