package com.example.swiftshare.di;

import com.example.swiftshare.database.AppDatabase;
import com.example.swiftshare.database.dao.TransferSessionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideTransferSessionDaoFactory implements Factory<TransferSessionDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideTransferSessionDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TransferSessionDao get() {
    return provideTransferSessionDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTransferSessionDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideTransferSessionDaoFactory(dbProvider);
  }

  public static TransferSessionDao provideTransferSessionDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTransferSessionDao(db));
  }
}
