package com.example.swiftshare.presentation.pairing.ui;

import com.example.swiftshare.domain.repository.SettingsRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class QrPairingFragment_MembersInjector implements MembersInjector<QrPairingFragment> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public QrPairingFragment_MembersInjector(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  public static MembersInjector<QrPairingFragment> create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new QrPairingFragment_MembersInjector(settingsRepositoryProvider);
  }

  @Override
  public void injectMembers(QrPairingFragment instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.swiftshare.presentation.pairing.ui.QrPairingFragment.settingsRepository")
  public static void injectSettingsRepository(QrPairingFragment instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
