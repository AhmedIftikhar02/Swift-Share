package com.example.swiftshare.presentation.permissions.viewmodels;

import com.example.swiftshare.permissions.PermissionManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PermissionRationaleViewModel_Factory implements Factory<PermissionRationaleViewModel> {
  private final Provider<PermissionManager> permissionManagerProvider;

  public PermissionRationaleViewModel_Factory(
      Provider<PermissionManager> permissionManagerProvider) {
    this.permissionManagerProvider = permissionManagerProvider;
  }

  @Override
  public PermissionRationaleViewModel get() {
    return newInstance(permissionManagerProvider.get());
  }

  public static PermissionRationaleViewModel_Factory create(
      Provider<PermissionManager> permissionManagerProvider) {
    return new PermissionRationaleViewModel_Factory(permissionManagerProvider);
  }

  public static PermissionRationaleViewModel newInstance(PermissionManager permissionManager) {
    return new PermissionRationaleViewModel(permissionManager);
  }
}
