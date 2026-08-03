package com.example.swiftshare.presentation.discovery.ui;

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
public final class DiscoveryFragment_MembersInjector implements MembersInjector<DiscoveryFragment> {
  private final Provider<PermissionManager> permissionManagerProvider;

  public DiscoveryFragment_MembersInjector(Provider<PermissionManager> permissionManagerProvider) {
    this.permissionManagerProvider = permissionManagerProvider;
  }

  public static MembersInjector<DiscoveryFragment> create(
      Provider<PermissionManager> permissionManagerProvider) {
    return new DiscoveryFragment_MembersInjector(permissionManagerProvider);
  }

  @Override
  public void injectMembers(DiscoveryFragment instance) {
    injectPermissionManager(instance, permissionManagerProvider.get());
  }

  @InjectedFieldSignature("com.example.swiftshare.presentation.discovery.ui.DiscoveryFragment.permissionManager")
  public static void injectPermissionManager(DiscoveryFragment instance,
      PermissionManager permissionManager) {
    instance.permissionManager = permissionManager;
  }
}
