package com.example.swiftshare.data.transfer;

import android.content.ContentResolver;
import com.example.swiftshare.common.providers.DispatcherProvider;
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
public final class FileMetadataResolver_Factory implements Factory<FileMetadataResolver> {
  private final Provider<ContentResolver> contentResolverProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  public FileMetadataResolver_Factory(Provider<ContentResolver> contentResolverProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    this.contentResolverProvider = contentResolverProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public FileMetadataResolver get() {
    return newInstance(contentResolverProvider.get(), dispatcherProvider.get());
  }

  public static FileMetadataResolver_Factory create(
      Provider<ContentResolver> contentResolverProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    return new FileMetadataResolver_Factory(contentResolverProvider, dispatcherProvider);
  }

  public static FileMetadataResolver newInstance(ContentResolver contentResolver,
      DispatcherProvider dispatcherProvider) {
    return new FileMetadataResolver(contentResolver, dispatcherProvider);
  }
}
