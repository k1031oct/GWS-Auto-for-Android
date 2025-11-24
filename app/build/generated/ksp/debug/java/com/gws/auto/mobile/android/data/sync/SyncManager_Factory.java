package com.gws.auto.mobile.android.data.sync;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gws.auto.mobile.android.data.local.db.WorkflowDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SyncManager_Factory implements Factory<SyncManager> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> authProvider;

  private final Provider<WorkflowDao> workflowDaoProvider;

  private SyncManager_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider, Provider<WorkflowDao> workflowDaoProvider) {
    this.firestoreProvider = firestoreProvider;
    this.authProvider = authProvider;
    this.workflowDaoProvider = workflowDaoProvider;
  }

  @Override
  public SyncManager get() {
    return newInstance(firestoreProvider.get(), authProvider.get(), workflowDaoProvider.get());
  }

  public static SyncManager_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<FirebaseAuth> authProvider, Provider<WorkflowDao> workflowDaoProvider) {
    return new SyncManager_Factory(firestoreProvider, authProvider, workflowDaoProvider);
  }

  public static SyncManager newInstance(FirebaseFirestore firestore, FirebaseAuth auth,
      WorkflowDao workflowDao) {
    return new SyncManager(firestore, auth, workflowDao);
  }
}
