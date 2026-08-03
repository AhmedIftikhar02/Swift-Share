package com.example.swiftshare.common.providers;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ResourceProvider_Factory implements Factory<ResourceProvider> {
  private final Provider<Context> contextProvider;

  public ResourceProvider_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ResourceProvider get() {
    return newInstance(contextProvider.get());
  }

  public static ResourceProvider_Factory create(Provider<Context> contextProvider) {
    return new ResourceProvider_Factory(contextProvider);
  }

  public static ResourceProvider newInstance(Context context) {
    return new ResourceProvider(context);
  }
}
