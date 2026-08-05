package com.example.swiftshare;

import com.example.swiftshare.domain.repository.NearbyRepository;
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase;
import com.example.swiftshare.permissions.PermissionManager;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<PermissionManager> permissionManagerProvider;

  private final Provider<NearbyRepository> nearbyRepositoryProvider;

  private final Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider;

  public MainActivity_MembersInjector(Provider<PermissionManager> permissionManagerProvider,
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider) {
    this.permissionManagerProvider = permissionManagerProvider;
    this.nearbyRepositoryProvider = nearbyRepositoryProvider;
    this.observeActiveSessionUseCaseProvider = observeActiveSessionUseCaseProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<PermissionManager> permissionManagerProvider,
      Provider<NearbyRepository> nearbyRepositoryProvider,
      Provider<ObserveActiveSessionUseCase> observeActiveSessionUseCaseProvider) {
    return new MainActivity_MembersInjector(permissionManagerProvider, nearbyRepositoryProvider, observeActiveSessionUseCaseProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPermissionManager(instance, permissionManagerProvider.get());
    injectNearbyRepository(instance, nearbyRepositoryProvider.get());
    injectObserveActiveSessionUseCase(instance, observeActiveSessionUseCaseProvider.get());
  }

  @InjectedFieldSignature("com.example.swiftshare.MainActivity.permissionManager")
  public static void injectPermissionManager(MainActivity instance,
      PermissionManager permissionManager) {
    instance.permissionManager = permissionManager;
  }

  @InjectedFieldSignature("com.example.swiftshare.MainActivity.nearbyRepository")
  public static void injectNearbyRepository(MainActivity instance,
      NearbyRepository nearbyRepository) {
    instance.nearbyRepository = nearbyRepository;
  }

  @InjectedFieldSignature("com.example.swiftshare.MainActivity.observeActiveSessionUseCase")
  public static void injectObserveActiveSessionUseCase(MainActivity instance,
      ObserveActiveSessionUseCase observeActiveSessionUseCase) {
    instance.observeActiveSessionUseCase = observeActiveSessionUseCase;
  }
}
