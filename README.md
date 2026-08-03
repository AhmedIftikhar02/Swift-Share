# My-Boilerplate — Android Starter Template (Full Reference)

This is the complete reference document for this boilerplate. It is written so that any AI
tool (Claude, ChatGPT, Copilot, etc.) or any developer can read it with zero other context
and immediately understand the architecture, where every piece of logic lives, and how to
extend it correctly for a new project.

**How to use this file**: clone this repo for a new project, paste this entire README into
your first message to an AI tool along with what you're building (see Section 9), and the
AI should be able to generate new features that match this structure instead of inventing
its own.

---

## Table of Contents

1. [Tech stack](#1-tech-stack)
2. [High-level architecture](#2-high-level-architecture)
3. [Full package structure](#3-full-package-structure)
4. [The complete data flow, end to end](#4-the-complete-data-flow-end-to-end)
5. [File-by-file reference](#5-file-by-file-reference)
6. [Theming system in detail](#6-theming-system-in-detail)
7. [How to add a brand-new feature module](#7-how-to-add-a-brand-new-feature-module)
8. [How to add a new API to an EXISTING feature](#8-how-to-add-a-new-api-to-an-existing-feature)
9. [Starting a new project from this boilerplate](#9-starting-a-new-project-from-this-boilerplate)
10. [Known limitations / intentional TODOs](#10-known-limitations--intentional-todos)
11. [Quick command reference](#11-quick-command-reference)

---

## 1. Tech stack

- **Language**: Kotlin
- **Min/Target/Compile SDK**: minSdk 24, target/compileSdk 34, AGP 8.2.2, Gradle 8.4
- **UI**: XML layouts + ViewBinding (Compose is NOT enabled in this boilerplate)
- **Architecture**: MVVM + Clean Architecture (data / domain / presentation layers per feature)
- **DI**: Dagger Hilt
- **Networking**: Retrofit2 + OkHttp3 + Moshi (codegen, not reflection)
- **Async**: Kotlin Coroutines + Flow (StateFlow for UI state, SharedFlow for one-shot events)
- **Navigation**: Jetpack Navigation Component, single-activity
- **Local DB**: Room (scaffolded, zero entities until you add your first one)
- **Local prefs**: DataStore (session/auth, coroutine-native) + classic SharedPreferences
  (general app settings)
- **Image loading**: Coil
- **Logging**: Timber, wrapping an OkHttp logging interceptor with header redaction
- **Annotation processing**: KSP (not KAPT, faster builds)

---

## 2. High-level architecture

Each feature (e.g. `home`, and any feature you add later like `auth`, `profile`, `cart`)
is structured in three layers, Clean-Architecture style:

```
feature/
├── data/            <- talks to the outside world (network, DB)
│   ├── remote/       (Retrofit interface, DTOs, DTO-to-domain mappers)
│   └── repository/   (Repository implementation)
├── domain/          <- pure Kotlin, no Android/Retrofit/Room imports
│   ├── model/         (clean domain model used by the UI)
│   └── repository/    (Repository INTERFACE - what the domain layer expects)
└── presentation/    <- UI layer
    ├── ui/            (Fragments)
    ├── viewmodels/    (ViewModels)
    └── adapters/      (RecyclerView adapters)
```

**The dependency rule**: presentation depends on domain. data depends on domain (implements
its repository interface). domain depends on nothing else in the app. This means:

- ViewModels only ever talk to a `XxxRepository` interface (domain), never to
  `XxxRepositoryImpl` (data) directly. Hilt wires the real implementation in behind the
  interface via `di/RepositoryModule.kt`.
- The domain model (`Product`, or `User`, etc.) never has nullable fields just because the
  API might return null - the mapper in the data layer resolves that once, so the
  ViewModel/UI layer never null-checks API quirks.
- If the API changes shape, or you swap Retrofit for something else, only the `data/`
  package for that feature changes. `domain/` and `presentation/` are untouched.

Cross-feature shared code lives outside any single feature package, in `base/`, `common/`,
`core/`, and `di/` (explained in detail in Section 5).

---

## 3. Full package structure

```
com.example.my_boilerplate/
|
|-- MyApplication.kt          Hilt entry point, Timber setup, applies saved theme
|-- MainActivity.kt           Single Activity, hosts NavHostFragment, listens to LoginEventBus
|
|-- base/                     Generic base classes every screen/adapter extends
|   |-- BaseActivity.kt
|   |-- BaseFragment.kt
|   |-- BaseViewModel.kt
|   |-- BaseAdapter.kt
|   |-- BaseBottomSheet.kt
|   `-- BaseDialog.kt
|
|-- core/                     App-wide infrastructure, NOT feature-specific
|   |-- network/
|   |   |-- NoAuth.kt              Annotation to skip auth token on a Retrofit method
|   |   |-- AuthInterceptor.kt     Attaches Authorization header unless @NoAuth present
|   |   |-- HttpLoggerFactory.kt   OkHttp logging interceptor + Timber ReleaseTree
|   |   |-- NetworkMonitor.kt      isOnline() connectivity check
|   |   `-- SafeApiCall.kt         Wraps every Retrofit call into Result<T>
|   |-- result/
|   |   |-- Result.kt              Sealed class: Success<T> / Error(AppException)
|   |   |-- AppException.kt        Typed exceptions: NetworkError, ServerError, etc.
|   |   `-- UiState.kt             Sealed class: Idle/Loading/Success/Error/Empty
|   `-- session/
|       `-- SessionManager.kt      DataStore-backed token/session storage
|
|-- common/                    Reusable helpers, NOT infrastructure-critical
|   |-- bus/
|   |   `-- LoginEventBus.kt       SharedFlow broadcasting LoggedIn/LoggedOut/SessionExpired
|   |-- extensions/
|   |   |-- FlowExt.kt             collectLifecycleFlow / collectLifecycleFlowActivity
|   |   |-- ViewExt.kt             visible()/gone()/setDebouncedClickListener() etc
|   |   `-- ContextExt.kt          showToast(), isDarkModeOn(), dpToPx()
|   |-- managers/
|   |   |-- SharedPrefsManager.kt  Classic SharedPreferences wrapper (non-session settings)
|   |   `-- ThemeManager.kt        Manual dark/light/system theme override
|   |-- providers/
|   |   |-- ResourceProvider.kt    Lets ViewModels get string resources without holding Context
|   |   `-- DispatcherProvider.kt  Injectable coroutine dispatchers (testability)
|   |-- utils/
|   |   |-- Validators.kt          isValidEmail/isValidPassword/etc
|   |   `-- Constants.kt           Shared constants (page size, bundle keys, etc)
|   `-- widgets/
|       `-- StateLayout.kt         Custom view: shows loading/content/error/empty automatically
|
|-- database/                  Room scaffold
|   `-- AppDatabase.kt             Zero entities yet, see Section 5 to activate
|
|-- di/                        All Hilt modules
|   |-- AppModule.kt               Binds DispatcherProvider interface to impl
|   |-- NetworkModule.kt           Provides Moshi, OkHttpClient, Retrofit, every ApiService
|   |-- RepositoryModule.kt        Binds every Repository interface to impl
|   `-- DatabaseModule.kt          Provides Room AppDatabase instance
|
`-- home/                      EXAMPLE FEATURE - copy this shape for new features
    |-- data/
    |   |-- remote/
    |   |   |-- ProductApiService.kt   Retrofit interface (DummyJSON products API)
    |   |   |-- ProductDto.kt          @JsonClass DTOs matching raw API JSON shape
    |   |   `-- ProductMapper.kt       ProductDto -> Product (domain) mapping
    |   `-- repository/
    |       `-- ProductRepositoryImpl.kt  Implements domain repo interface, uses safeApiCall
    |-- domain/
    |   |-- model/
    |   |   `-- Product.kt             Clean domain model, no nullable fields
    |   `-- repository/
    |       `-- ProductRepository.kt   Interface the ViewModel depends on
    `-- presentation/
        |-- viewmodels/
        |   `-- HomeViewModel.kt       Exposes StateFlow<UiState<List<Product>>>
        |-- ui/
        |   `-- HomeFragment.kt        Collects uiState, drives StateLayout + RecyclerView
        `-- adapters/
            `-- ProductAdapter.kt      BaseAdapter<Product, HomeItemProductBinding>
```

### Resources (`res/`)

```
res/
|-- values/
|   |-- colors.xml      Light theme semantic color tokens (color_primary, color_surface, ...)
|   |-- themes.xml      Theme.MyBoilerplate (Material3 Light parent) + .Splash variant
|   |-- dimens.xml      dp_2 .. dp_50 scale, text sizes, icon sizes, corner radii, elevations
|   |-- styles.xml      Widget.App.Button.*, Widget.App.EditText.*, TextAppearance.App.*
|   `-- strings.xml
|-- values-night/
|   |-- colors.xml      SAME color names as values/colors.xml, dark hex values
|   `-- themes.xml      SAME style name as values/themes.xml, Material3 Dark parent
|-- drawable/
|   |-- bg_card.xml / bg_card_outlined.xml
|   |-- bg_button_primary.xml / bg_button_primary_gradient.xml / bg_button_outline.xml
|   |-- bg_edittext_filled.xml / bg_edittext_outline.xml
|   `-- ic_launcher_foreground.xml   PLACEHOLDER, regenerate via Image Asset tool
|-- layout/
|   |-- activity_main.xml            Just a FragmentContainerView hosting nav_graph
|   |-- home_fragment.xml            Header + StateLayout(SwipeRefresh + RecyclerView)
|   |-- home_item_product.xml        Single product card row
|   `-- widget_state_layout.xml      Internal layout for the StateLayout custom view
|-- navigation/
|   `-- nav_graph.xml                homeFragment is startDestination
|-- mipmap-anydpi-v26/ + mipmap-anydpi/   Adaptive + legacy launcher icon fallback
`-- xml/
    |-- data_extraction_rules.xml    Excludes session DataStore from cloud backup
    |-- backup_rules.xml             Same, for pre-Android-12 backup
    `-- network_security_config.xml  Blocks plaintext HTTP app-wide
```

**Naming convention for new features**: prefix feature-specific layout/drawable files with
the feature name, e.g. `auth_fragment_login.xml`, `auth_bg_input.xml`. This is a naming
convention only (everything still lives in the same `res/layout/`, `res/drawable/` folders).
True per-feature `res/` folder separation requires converting to a multi-module Gradle
project, which is a bigger structural change not currently set up. See Section 10.

---

## 4. The complete data flow, end to end

Using the existing Home feature as the concrete example, here is exactly what happens when
`HomeFragment` loads, step by step:

1. **Fragment created**, `HomeViewModel` is obtained via `by viewModels()` (Hilt provides it
   because of `@HiltViewModel` + `@Inject constructor`).
2. **`HomeViewModel.init {}`** calls `loadProducts()`.
3. **`loadProducts()`** calls `launchSafe { ... }` (from `BaseViewModel`, wraps the coroutine
   in a `CoroutineExceptionHandler` so an unexpected crash becomes a `UiState.Error` instead
   of crashing the app).
4. Inside, `_uiState.value = UiState.Loading` is set first.
5. `productRepository.getProducts()` is called, this is the domain interface
   (`ProductRepository`), but Hilt actually injected `ProductRepositoryImpl` behind it
   (wired in `di/RepositoryModule.kt`).
6. **`ProductRepositoryImpl.getProducts()`** calls
   `safeApiCall(networkMonitor) { api.getProducts(...).products }`.
7. **`safeApiCall`** (`core/network/SafeApiCall.kt`) first checks `networkMonitor.isOnline()`.
   If offline, it returns `Result.Error(AppException.NetworkError())` immediately, no
   network call is even attempted.
8. If online, it runs the actual Retrofit call on `Dispatchers.IO`, inside a try/catch that
   converts every possible exception (`HttpException`, `SocketTimeoutException`,
   `JsonDataException`, `IOException`, anything else) into the correct `AppException`
   subtype, wrapped in `Result.Error`. On success, returns `Result.Success(data)`.
9. **Before the actual HTTP request goes out**, `AuthInterceptor` runs (it's wired into the
   `OkHttpClient` in `di/NetworkModule.kt`). It reads the Retrofit `Invocation` tag off the
   request to find which interface method was called, checks if that method has `@NoAuth`,
   `ProductApiService.getProducts()` does, so no Authorization header is added. If it
   didn't have `@NoAuth`, the interceptor would call `sessionManager.getAccessToken()` and
   attach `Authorization: Bearer <token>`.
10. The logging interceptor (`HttpLoggerFactory`) logs the full request/response via Timber
    (in debug builds only, controlled by `BuildConfig.ENABLE_LOGGING`), with the
    Authorization header redacted even in debug logs.
11. Response comes back as `ProductListResponse` (raw DTO, matches the JSON shape exactly,
    via Moshi codegen adapter generated at compile time from
    `@JsonClass(generateAdapter = true)`).
12. Back in `ProductRepositoryImpl`, `.map { it.toDomainList() }` runs the mapper
    (`ProductMapper.kt`), converting `List<ProductDto>` to `List<Product>`, resolving every
    nullable DTO field to a safe default.
13. The `Result<List<Product>>` comes back up to `HomeViewModel`.
14. `HomeViewModel` checks: `Result.Success` with empty list becomes `UiState.Empty`.
    Non-empty becomes `UiState.Success(data)`. `Result.Error` becomes
    `UiState.Error(exception)`.
15. `_uiState.value` is updated, this is a `StateFlow`, so anyone collecting it gets the
    new value immediately.
16. **`HomeFragment.observeData()`** is collecting `viewModel.uiState` via
    `collectLifecycleFlow(this) { state -> ... }` (from `common/extensions/FlowExt.kt`),
    which safely starts/stops collection based on `STARTED`/`STOPPED` lifecycle state,
    this is what prevents "fragment crashes because it updated a view after
    onDestroyView" bugs.
17. Based on the `UiState` variant, the Fragment calls `binding.stateLayout.showLoading()`,
    `.showContent()`, `.showError(message) { viewModel.retry() }`, or `.showEmpty(message)`.
18. On `Success`, `adapter.submitList(state.data)` is also called, `ProductAdapter` extends
    `BaseAdapter` (which wraps `ListAdapter` + `DiffUtil`), so the RecyclerView animates the
    diff automatically.
19. `ProductAdapter.bind()` populates each row's views, including loading the thumbnail image
    via Coil's `imageView.load(url) { crossfade(true) }`.

That's the full loop: Fragment -> ViewModel -> Repository interface -> Repository impl ->
safeApiCall -> AuthInterceptor/Logging -> Retrofit/Moshi -> Mapper -> Result -> UiState ->
back to Fragment -> StateLayout + Adapter.

### Session / login event flow (separate from the above)

1. Anywhere in the app, after a successful login API call, you'd call
   `sessionManager.saveSession(accessToken, refreshToken, userId)`.
2. `SessionManager.saveSession()` writes to DataStore AND emits `SessionEvent.LoggedIn`
   through `LoginEventBus`.
3. From then on, every Retrofit call that ISN'T `@NoAuth` automatically gets
   `Authorization: Bearer <token>` attached by `AuthInterceptor`, because it reads the token
   from `SessionManager` on every request.
4. If any API call ever returns 401/403, `SafeApiCall` converts it to
   `AppException.UnauthorizedError`. Note: nothing currently auto-calls
   `sessionManager.clearSession()` on a 401, see Section 10, you'd add that call at the
   ViewModel level or via an OkHttp `Authenticator` if you want automatic logout-on-401.
5. When `sessionManager.clearSession()` IS called (manual logout button, or wherever you
   wire 401-handling to), it clears DataStore and emits `SessionEvent.LoggedOut`.
6. `MainActivity.observeData()` is permanently collecting `loginEventBus.events`, on
   `LoggedOut`/`SessionExpired` it's meant to navigate to an auth flow and clear the back
   stack (currently a TODO comment, there's no auth flow built yet in this boilerplate).

---

## 5. File-by-file reference

### `base/` - generic base classes

- **`BaseActivity<VB>`**: takes a ViewBinding inflater function reference in its
  constructor, handles `setContentView`, calls abstract `setupViews()` then
  `observeData()` (open, default no-op).
- **`BaseFragment<VB>`**: same idea, but also nulls out the binding in `onDestroyView()`,
  this is the most common ViewBinding+Fragment memory leak bug, fixed once here for
  every Fragment in the app.
- **`BaseViewModel`**: provides `launchSafe { }` (coroutine launch with a
  `CoroutineExceptionHandler` so unhandled exceptions don't crash the app, they emit a
  `UiEvent.ShowError` instead) and a `uiEvent: SharedFlow<UiEvent>` for one-shot events
  (toasts, navigation, "show this error once") that shouldn't replay on rotation the way a
  StateFlow would.
- **`BaseAdapter<T, VB>`**: wraps `ListAdapter` + `DiffUtil.ItemCallback`, constructed with
  `areItemsTheSame`/`areContentsTheSame` lambdas instead of needing a new DiffUtil class
  per adapter. Subclasses only implement `bind(binding, item, position)`.
- **`BaseBottomSheet<VB>` / `BaseDialog<VB>`**: same ViewBinding pattern, for
  `BottomSheetDialogFragment` and `DialogFragment` respectively.

### `core/network/` - networking infrastructure

- **`NoAuth`**: a `@Target(AnnotationTarget.FUNCTION)` annotation. Put it on a Retrofit
  interface method to skip attaching the auth token. Default behavior (no annotation) means
  the token IS attached, most endpoints need it, public endpoints are the exception.
- **`AuthInterceptor`**: an OkHttp `Interceptor`. Reads `request.tag(Invocation::class.java)`
  to get the calling Retrofit method via reflection, checks
  `method.isAnnotationPresent(NoAuth::class.java)`. If not annotated, calls
  `runBlocking { sessionManager.getAccessToken() }` (safe here, interceptors run on
  OkHttp's dispatcher thread, never main) and adds the header.
- **`HttpLoggerFactory`**: builds an `HttpLoggingInterceptor` that logs through Timber
  instead of println, and calls `redactHeader("Authorization")` and
  `redactHeader("Cookie")` so tokens never leak into logs even in debug builds. Verbosity
  (`BODY` vs `NONE`) controlled by `BuildConfig.ENABLE_LOGGING`. Also defines `ReleaseTree`,
  a Timber tree meant to forward WARN/ERROR logs to a crash reporter (TODO, no SDK chosen
  yet).
- **`NetworkMonitor`**: `isOnline()` checks `ConnectivityManager` for an active, validated,
  internet-capable network.
- **`safeApiCall(networkMonitor) { ... }`**: the function every Repository wraps its
  Retrofit calls in. Checks connectivity first, runs on `Dispatchers.IO`, catches every
  exception type and converts to the matching `AppException`, re-throws
  `CancellationException` (critical, never swallow this or coroutine cancellation breaks).

### `core/result/` - the Result/Exception/UiState trio

- **`Result<T>`**: sealed class, `Success<T>(data)` / `Error(exception: AppException)`.
  Has `.map { }`, `.onSuccess { }`, `.onError { }`, `.getOrNull()` helpers. This is the
  Repository layer's return type.
- **`AppException`**: sealed class of typed exceptions: `NetworkError`, `ServerError(code,
  message)`, `UnauthorizedError`, `TimeoutError`, `ParseError`, `UnknownError`. The rest of
  the app (ViewModel, UI) only ever sees these, never raw `IOException`/`HttpException`.
- **`UiState<T>`**: sealed class, `Idle` / `Loading` / `Success<T>(data)` /
  `Error(exception)` / `Empty`. This is the ViewModel layer's exposed type, distinct
  from `Result` because the UI cares about a `Loading` state that the data layer doesn't
  need to know about.

### `core/session/SessionManager.kt`

DataStore-backed (`Context.sessionDataStore` via
`preferencesDataStore(name = "session_prefs")`). Stores access token, refresh token, user
ID, logged-in flag. `saveSession()` and `clearSession()` both emit through `LoginEventBus`.
Excluded from Android auto-backup (see `xml/data_extraction_rules.xml` and
`backup_rules.xml`) so a token never gets silently restored onto a different device.

### `common/bus/LoginEventBus.kt`

A `MutableSharedFlow<SessionEvent>` wrapped as a singleton, this is the modern
coroutine-native replacement for old "EventBus" libraries. `SessionEvent` is `LoggedIn` /
`LoggedOut` / `SessionExpired`. Anything can `emit()`, anything can collect `.events`.

### `common/managers/`

- **`SharedPrefsManager`**: plain `SharedPreferences` wrapper, for general non-session
  settings (onboarding seen, selected language, theme mode string). Deliberately separate
  from `SessionManager` (DataStore), session/token data and general settings are
  different concerns with different security expectations.
- **`ThemeManager`**: `applyTheme(ThemeMode.LIGHT/DARK/SYSTEM)` calls
  `AppCompatDelegate.setDefaultNightMode(...)`, which is what makes the in-app dark mode
  toggle work, it's the same mechanism as the system "force dark mode" setting, just
  triggered manually. Persists the choice via `SharedPrefsManager`.

### `common/providers/`

- **`ResourceProvider`**: injects `@ApplicationContext` once, exposes `getString(resId)`.
  Why: ViewModels shouldn't hold Activity/Fragment Context directly (leak risk and makes
  unit testing harder); Application Context is safe to hold since it lives as long as the
  app, and this wrapper makes it trivial to fake in unit tests.
- **`DispatcherProvider`** (interface) / **`DefaultDispatcherProvider`** (impl, bound in
  `di/AppModule.kt`): inject this instead of referencing `Dispatchers.IO` etc directly, so
  unit tests can swap in `TestDispatcher`s.

### `common/widgets/StateLayout.kt`

A custom `FrameLayout`. Inflates `widget_state_layout.xml` (progress bar + error view +
empty view) as its first children, then on `onFinishInflate()` captures whatever OTHER
child the layout-author placed inside the `<StateLayout>...</StateLayout>` tags in their
own XML as "the content view". Exposes `showLoading()`, `showContent()`,
`showError(message, onRetry)`, `showEmpty(message)`, toggling visibility between content
and the three internal state views. This is what every Fragment's `observeData()` should
drive based on the `UiState` it collects.

### `database/AppDatabase.kt`

`@Database(entities = [], version = 1, exportSchema = false)`. Empty on purpose, see
Section 7 for the steps to add your first entity.

### `di/` - all four Hilt modules

- **`NetworkModule`**: `@Provides` for `Moshi` (codegen only, no reflection adapter
  needed), `OkHttpClient` (interceptor order: `AuthInterceptor` first, then the logging
  interceptor, so the logger sees the final request including the auth header, which it
  then redacts), `Retrofit`, and one `@Provides` per `ApiService` interface (currently just
  `provideProductApiService`).
- **`RepositoryModule`**: `@Binds` for each domain Repository interface to its Impl
  (currently `ProductRepository` to `ProductRepositoryImpl`). Uses `@Binds` not `@Provides`
  since the Impl already has an `@Inject constructor`.
- **`DatabaseModule`**: `@Provides` for the `AppDatabase` Room instance.
  `fallbackToDestructiveMigration()`, fine for development, replace before a real release.
- **`AppModule`**: catch-all for bindings that don't fit the other three. Currently just
  `DispatcherProvider` to `DefaultDispatcherProvider`.

Important: classes with their own `@Inject constructor` (`SessionManager`,
`SharedPrefsManager`, `ThemeManager`, `ResourceProvider`, `NetworkMonitor`,
`LoginEventBus`, `AuthInterceptor`, `ProductRepositoryImpl`, `DefaultDispatcherProvider`)
do not need a `@Provides`/`@Binds` entry, Hilt constructs them automatically. You only
write a module entry when (a) binding an interface to an implementation, or (b) the thing
being provided comes from a third-party library/builder pattern you don't control
(Retrofit, OkHttpClient, Room, Moshi).

### `home/` - the full example feature (study this to copy the pattern)

- **`data/remote/ProductDto.kt`**: `ProductDto` + `ProductListResponse`, both
  `@JsonClass(generateAdapter = true)`, matching DummyJSON's raw JSON exactly (nullable
  fields where the API could plausibly omit them).
- **`data/remote/ProductApiService.kt`**: two `@NoAuth` endpoints (`getProducts`,
  `getProductDetail`) since DummyJSON needs no auth at all.
- **`data/remote/ProductMapper.kt`**: `ProductDto.toDomain()` and
  `List<ProductDto>.toDomainList()`, the one place nullable fields get resolved
  (`description.orEmpty()`, `rating ?: 0.0`, etc).
- **`data/repository/ProductRepositoryImpl.kt`**: implements `ProductRepository`, every
  method wrapped in `safeApiCall(networkMonitor) { ... }.map { it.toDomain...() }`.
- **`domain/model/Product.kt`**: clean model, zero nullable fields, plus domain logic
  living here (`discountedPrice`, `formattedPrice` computed properties) instead of
  scattered across the UI layer.
- **`domain/repository/ProductRepository.kt`**: the interface, `getProducts()`,
  `getProductDetail(id)`, both returning `Result<...>`.
- **`presentation/viewmodels/HomeViewModel.kt`**: `@HiltViewModel`, exposes
  `uiState: StateFlow<UiState<List<Product>>>`, `loadProducts()` and `retry()`.
- **`presentation/ui/HomeFragment.kt`**: `@AndroidEntryPoint`, extends
  `BaseFragment<HomeFragmentBinding>`, sets up the RecyclerView + SwipeRefreshLayout in
  `setupViews()`, collects `uiState` in `observeData()` and drives `StateLayout` +
  `adapter.submitList()`.
- **`presentation/adapters/ProductAdapter.kt`**: extends
  `BaseAdapter<Product, HomeItemProductBinding>`, loads the thumbnail via Coil.

---

## 6. Theming system in detail

Why two `themes.xml` files with the identical style name work: Android resolves
resources by configuration qualifier at runtime. `res/values/themes.xml` is the default
(light). `res/values-night/themes.xml` has a style with the exact same name
(`Theme.MyBoilerplate`) but a Dark Material3 parent. When the device (or an explicit
`AppCompatDelegate.setDefaultNightMode()` call) is in night mode, Android resolves
`Theme.MyBoilerplate` from the `values-night/` file instead of `values/`, no code branch
needed, no second theme name to remember.

The exact same mechanism applies to `colors.xml`: both files define identical color names
(`color_primary`, `color_surface`, etc) with different hex values. Every layout/style in
the app should reference `@color/color_*` semantic names, never `@color/brand_*` or
`@color/neutral_*` directly outside of `colors.xml` itself, or dark mode will silently break
for that one usage.

Manual override: `ThemeManager.applyTheme(ThemeMode.DARK)` calls
`AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)`, which forces the same resolution
mechanism above to pick `values-night/` regardless of system setting. This is called once
in `MyApplication.onCreate()` with whatever was last saved, and again any time the user
changes a theme setting in-app.

Dimens scale: `dp_2` through `dp_50` in steps of 2, plus named text sizes
(`text_caption` through `text_display`), icon sizes, corner radii, elevations, stroke
widths. Use these instead of hardcoding `8dp` etc in new layouts.

Styles: `Widget.App.Button.Primary` (filled), `.Secondary` (tonal), `.Outline`, `.Text`;
`Widget.App.EditText.Filled` / `.Outline`; `TextAppearance.App.Headline/Title/Body/Caption`;
`Widget.App.Card`. Apply via `style="@style/Widget.App.Button.Primary"` in XML.

---

## 7. How to add a brand-new feature module

Walking through adding, say, an "Auth" feature with a login screen, copy the `home`
package's exact shape:

**Step 1, create the folders**:
```
auth/data/remote/
auth/data/repository/
auth/domain/model/
auth/domain/repository/
auth/presentation/ui/
auth/presentation/viewmodels/
```

**Step 2, DTO** (`auth/data/remote/AuthDto.kt`):
```kotlin
@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResponse(val accessToken: String, val refreshToken: String?, val userId: String)
```

**Step 3, API service** (`auth/data/remote/AuthApiService.kt`):
```kotlin
interface AuthApiService {
    @NoAuth   // no token exists yet at login time
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // no @NoAuth here -> token attaches automatically once the user is logged in
    @GET("auth/me")
    suspend fun getCurrentUser(): UserDto
}
```

**Step 4, domain model** (`auth/domain/model/User.kt`): plain data class, no nullable
fields you don't have to handle.

**Step 5, mapper** (`auth/data/remote/AuthMapper.kt`): `fun UserDto.toDomain(): User`.

**Step 6, repository interface** (`auth/domain/repository/AuthRepository.kt`):
```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
}
```

**Step 7, repository impl** (`auth/data/repository/AuthRepositoryImpl.kt`):
```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> =
        safeApiCall(networkMonitor) { api.login(LoginRequest(email, password)) }
            .onSuccess { response ->
                sessionManager.saveSession(response.accessToken, response.refreshToken, response.userId)
            }
            .map { }

    override suspend fun getCurrentUser(): Result<User> =
        safeApiCall(networkMonitor) { api.getCurrentUser() }.map { it.toDomain() }
}
```

**Step 8, wire DI**:
In `di/NetworkModule.kt`, add:
```kotlin
@Provides @Singleton
fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
    retrofit.create(AuthApiService::class.java)
```
In `di/RepositoryModule.kt`, add:
```kotlin
@Binds @Singleton
abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
```

**Step 9, ViewModel** (`auth/presentation/viewmodels/LoginViewModel.kt`):
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        launchSafe {
            _uiState.value = UiState.Loading
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> _uiState.value = UiState.Success(Unit)
                is Result.Error -> _uiState.value = UiState.Error(result.exception)
            }
        }
    }
}
```

**Step 10, layout + Fragment**: `res/layout/auth_fragment_login.xml` (prefix `auth_` per
the naming convention), then:
```kotlin
@AndroidEntryPoint
class LoginFragment : BaseFragment<AuthFragmentLoginBinding>(AuthFragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun setupViews() {
        binding.btnLogin.setDebouncedClickListener {
            viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            when (state) {
                is UiState.Loading -> { /* show a spinner */ }
                is UiState.Success -> findNavController().navigate(R.id.action_login_to_home)
                is UiState.Error -> toast(state.exception.message)
                else -> Unit
            }
        }
    }
}
```

**Step 11, nav graph**: add the `<fragment>` node + `<action>` in
`res/navigation/nav_graph.xml`, and consider making it the new `app:startDestination` if
auth should gate the rest of the app.

---

## 8. How to add a new API to an EXISTING feature

This is simpler than a new feature, say `home` needs a new "search products" endpoint.

**Step 1**: add the method to the existing `ProductApiService.kt`:
```kotlin
@NoAuth
@GET("products/search")
suspend fun searchProducts(@Query("q") query: String): ProductListResponse
```
Decide `@NoAuth` or not based on whether this specific endpoint needs the token, it's
per-method, not per-file, so a feature's ApiService can freely mix authenticated and public
endpoints.

**Step 2**: add the method to the domain interface `ProductRepository.kt`:
```kotlin
suspend fun searchProducts(query: String): Result<List<Product>>
```

**Step 3**: implement it in `ProductRepositoryImpl.kt`, same pattern as the existing methods:
```kotlin
override suspend fun searchProducts(query: String): Result<List<Product>> =
    safeApiCall(networkMonitor) { api.searchProducts(query).products }
        .map { it.toDomainList() }
```

**Step 4**: no DI changes needed, `ProductApiService` and `ProductRepository` are already
provided/bound; you only edited methods on already-wired interfaces/classes.

**Step 5**: call it from `HomeViewModel.kt` (or add a new `SearchViewModel` if it's a
genuinely separate screen) the same way `loadProducts()` already does, with its own
`UiState` field if the result needs separate loading/error/success state from the main list.

That's the whole pattern: new method on the ApiService, new method on the domain
Repository interface, implement it in RepositoryImpl using `safeApiCall`, call it from a
ViewModel. No new DTO is needed if the response shape matches an existing one (as above,
reusing `ProductListResponse`); add a new `@JsonClass` DTO in the same `data/remote/`
package if the new endpoint returns a different shape.

---

## 9. Starting a new project from this boilerplate

When you clone this repo for a new app and hand it to an AI tool, the AI also needs to know
specifics about the new project, this README explains the boilerplate's structure, but not
what you're building on top of it. Provide, at minimum:

1. App name + package name (changes `namespace`/`applicationId` and the package
   declarations throughout, `com.example.my_boilerplate` needs to be renamed everywhere).
2. What the app does, plus a list of v1 screens/features.
3. Backend details: real API base URL + auth mechanism + which endpoints need the
   token, or "use a free public API, pick one for me."
4. Design direction: color/style preference, or "match my [previous project] theme."
5. Anything beyond the defaults: Room actually needed (offline caching)? Maps, camera,
   push notifications, payments, pagination, search, multi-language?

See the separate New-Project-Brief.md template (if you generated one previously) for a
fill-in-the-blanks version of the above.

---

## 10. Known limitations / intentional TODOs

- No automatic 401 -> logout flow: `AuthInterceptor`/`SafeApiCall` correctly classify a
  401/403 as `AppException.UnauthorizedError`, but nothing automatically calls
  `sessionManager.clearSession()` when that happens. Add that call wherever you handle
  `UnauthorizedError` in a ViewModel, or implement an OkHttp `Authenticator` for a
  transparent token-refresh-and-retry flow if your backend supports refresh tokens.
- Room has zero entities: to activate it for a feature: (1) create an `@Entity` data
  class in `database/entity/`, (2) create a `@Dao` interface in `database/dao/`, (3) add
  the entity to `AppDatabase`'s `entities = [...]` list and bump `version`, (4) add an
  abstract fun for the DAO in `AppDatabase`, (5) provide the DAO in `di/DatabaseModule.kt`
  via `@Provides fun provideXDao(db: AppDatabase): XDao = db.xDao()`.
- `fallbackToDestructiveMigration()` is used for Room, fine pre-release, replace with
  real `Migration` objects before shipping an update that must preserve existing user data.
- No crash reporting wired: `ReleaseTree` (`core/network/HttpLoggerFactory.kt`) has a
  marked TODO to forward warnings/errors to Crashlytics/Sentry/etc once you pick one.
- Placeholder launcher icon: regenerate via Android Studio's res -> New -> Image Asset
  -> Launcher Icons before shipping.
- No true per-feature `res/` folder isolation: current setup uses a file-naming
  convention (`home_*.xml`, `auth_*.xml`) within shared `res/layout`, `res/drawable`, etc.
  Genuine folder-level isolation requires converting to a multi-module Gradle project
  (`:feature:home`, `:feature:auth`, each with its own `res/`), a deliberate scope
  decision to keep this boilerplate a single, simple, clone-and-go module. Worth
  revisiting if the project grows to multiple feature owners or very large build times.
- No Compose: this boilerplate is XML + ViewBinding only. Re-enabling Compose means
  adding back `buildFeatures { compose = true }`, the Compose BOM dependencies, and the
  Compose compiler plugin in `app/build.gradle.kts`.
- compileSdk/targetSdk pinned at 34: AGP 8.2.2 (used here) supports up to API 34.
  Targeting API 36 requires upgrading to AGP 9.x + Gradle 9.x, which involves some breaking
  DSL changes for certain plugins, a deliberate, larger upgrade decision, not done here.

---

## 11. Quick command reference

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Clean build (use if Hilt/KSP generated code gets out of sync after big refactors)
./gradlew clean build
```

First Gradle sync will be slow, Hilt + KSP + Room annotation processing all run for
the first time. Subsequent syncs are much faster due to incremental processing.
