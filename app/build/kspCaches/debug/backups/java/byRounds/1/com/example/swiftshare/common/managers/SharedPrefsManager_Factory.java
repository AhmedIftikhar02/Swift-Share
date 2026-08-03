package com.example.swiftshare.common.managers;

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
public final class SharedPrefsManager_Factory implements Factory<SharedPrefsManager> {
  private final Provider<Context> contextProvider;

  public SharedPrefsManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SharedPrefsManager get() {
    return newInstance(contextProvider.get());
  }

  public static SharedPrefsManager_Factory create(Provider<Context> contextProvider) {
    return new SharedPrefsManager_Factory(contextProvider);
  }

  public static SharedPrefsManager newInstance(Context context) {
    return new SharedPrefsManager(context);
  }
}
