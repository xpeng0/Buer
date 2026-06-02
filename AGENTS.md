# Repository Guidelines

## Architecture

Keep dependencies one-way:

```text
UI -> ViewModel -> Repository -> DataSource
```

Repositories map DTOs and Entities to domain models. UI must not access repositories or data sources directly; ViewModels must not access data sources.

Modules:

- `:app`: application shell, app navigation, cross-feature wiring.
- `:feature:bookkeeping`: Room-backed bookkeeping feature.
- `:feature:fitness`: Compose fitness UI and navigation.
- `:finance`: finance and watchlist feature.
- `:xpviews`: reusable UI components without business logic.

Feature modules must not depend on each other.

## Compose UI

Use Compose only; do not add XML layouts or Fragments. Split each page into:

- `XXXRoute`: obtains a Hilt ViewModel, uses `collectAsStateWithLifecycle()`, and wires callbacks.
- `XXXScreen`: stateless renderer receiving immutable state and lambdas.

Keep one page-level `XXXScreen` per `{Page}Screen.kt`. Add a realistic `@Preview`. Put reusable composables in dedicated `ui/composable` files. Optional modifiers use `modifier: Modifier = Modifier`.

Never hardcode displayed text or colors in composables. Add English strings to `src/main/res/values/strings.xml`, Simplified Chinese strings to `src/main/res/values-zh-rCN/strings.xml`, and read them with `stringResource()`. Store module colors in `ui/theme/{Module}Colors.kt`.

## Feature Packages

Organize feature modules by capability:

```text
fitness/
  exercise/ui/{state,composable}/
  exercise/vm/
  template/ui/{state,composable}/
  template/vm/
```

Each capability owns its `ui`, `ui/state`, `ui/composable`, and `vm` packages.

## ViewModels and Navigation

Expose one `StateFlow<XXScreenUiState>` per screen. Use sealed loading, success, and error states when loading data. Do not expose DTOs or Entities.

Use Hilt (`@HiltViewModel`, `@Inject constructor`, `hiltViewModel()`) with KSP. Define type-safe `@Serializable` routes in each module's `navigation` package. Expose navigation through `NavGraphBuilder` and `NavController` extensions.

## Build and Test

- `./gradlew assembleDebug`: build the debug APK.
- `./gradlew test`: run JVM tests.
- `./gradlew :feature:fitness:compileDebugKotlin`: compile fitness UI.
- `./gradlew connectedDebugAndroidTest`: run device tests.

Place JVM tests in `src/test/java`, instrumented tests in `src/androidTest/java`, and commit Room schemas under `schemas/`.
