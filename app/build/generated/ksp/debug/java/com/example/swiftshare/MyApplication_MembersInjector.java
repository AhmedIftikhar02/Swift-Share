package com.example.swiftshare;

import com.example.swiftshare.common.managers.ThemeManager;
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
public final class MyApplication_MembersInjector implements MembersInjector<MyApplication> {
  private final Provider<ThemeManager> themeManagerProvider;

  public MyApplication_MembersInjector(Provider<ThemeManager> themeManagerProvider) {
    this.themeManagerProvider = themeManagerProvider;
  }

  public static MembersInjector<MyApplication> create(Provider<ThemeManager> themeManagerProvider) {
    return new MyApplication_MembersInjector(themeManagerProvider);
  }

  @Override
  public void injectMembers(MyApplication instance) {
    injectThemeManager(instance, themeManagerProvider.get());
  }

  @InjectedFieldSignature("com.example.swiftshare.MyApplication.themeManager")
  public static void injectThemeManager(MyApplication instance, ThemeManager themeManager) {
    instance.themeManager = themeManager;
  }
}
