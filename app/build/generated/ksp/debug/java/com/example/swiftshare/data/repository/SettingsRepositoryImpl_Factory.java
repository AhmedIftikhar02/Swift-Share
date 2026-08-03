package com.example.swiftshare.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class SettingsRepositoryImpl_Factory implements Factory<SettingsRepositoryImpl> {
  @Override
  public SettingsRepositoryImpl get() {
    return newInstance();
  }

  public static SettingsRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SettingsRepositoryImpl newInstance() {
    return new SettingsRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final SettingsRepositoryImpl_Factory INSTANCE = new SettingsRepositoryImpl_Factory();
  }
}
