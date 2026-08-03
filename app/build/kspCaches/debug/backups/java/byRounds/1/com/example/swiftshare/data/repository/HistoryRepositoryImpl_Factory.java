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
public final class HistoryRepositoryImpl_Factory implements Factory<HistoryRepositoryImpl> {
  @Override
  public HistoryRepositoryImpl get() {
    return newInstance();
  }

  public static HistoryRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HistoryRepositoryImpl newInstance() {
    return new HistoryRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final HistoryRepositoryImpl_Factory INSTANCE = new HistoryRepositoryImpl_Factory();
  }
}
