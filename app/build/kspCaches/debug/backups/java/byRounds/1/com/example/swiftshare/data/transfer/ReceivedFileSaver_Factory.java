package com.example.swiftshare.data.transfer;

import android.content.Context;
import com.example.swiftshare.common.providers.DispatcherProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ReceivedFileSaver_Factory implements Factory<ReceivedFileSaver> {
  private final Provider<Context> contextProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  public ReceivedFileSaver_Factory(Provider<Context> contextProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    this.contextProvider = contextProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public ReceivedFileSaver get() {
    return newInstance(contextProvider.get(), dispatcherProvider.get());
  }

  public static ReceivedFileSaver_Factory create(Provider<Context> contextProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    return new ReceivedFileSaver_Factory(contextProvider, dispatcherProvider);
  }

  public static ReceivedFileSaver newInstance(Context context,
      DispatcherProvider dispatcherProvider) {
    return new ReceivedFileSaver(context, dispatcherProvider);
  }
}
