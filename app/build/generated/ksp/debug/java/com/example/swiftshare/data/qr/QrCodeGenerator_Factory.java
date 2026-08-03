package com.example.swiftshare.data.qr;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class QrCodeGenerator_Factory implements Factory<QrCodeGenerator> {
  @Override
  public QrCodeGenerator get() {
    return newInstance();
  }

  public static QrCodeGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static QrCodeGenerator newInstance() {
    return new QrCodeGenerator();
  }

  private static final class InstanceHolder {
    private static final QrCodeGenerator_Factory INSTANCE = new QrCodeGenerator_Factory();
  }
}
