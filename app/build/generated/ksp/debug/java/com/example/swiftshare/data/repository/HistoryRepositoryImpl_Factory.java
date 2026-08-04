package com.example.swiftshare.data.repository;

import com.example.swiftshare.common.providers.DispatcherProvider;
import com.example.swiftshare.database.dao.TransferSessionDao;
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
public final class HistoryRepositoryImpl_Factory implements Factory<HistoryRepositoryImpl> {
  private final Provider<TransferSessionDao> daoProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  public HistoryRepositoryImpl_Factory(Provider<TransferSessionDao> daoProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    this.daoProvider = daoProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public HistoryRepositoryImpl get() {
    return newInstance(daoProvider.get(), dispatcherProvider.get());
  }

  public static HistoryRepositoryImpl_Factory create(Provider<TransferSessionDao> daoProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    return new HistoryRepositoryImpl_Factory(daoProvider, dispatcherProvider);
  }

  public static HistoryRepositoryImpl newInstance(TransferSessionDao dao,
      DispatcherProvider dispatcherProvider) {
    return new HistoryRepositoryImpl(dao, dispatcherProvider);
  }
}
