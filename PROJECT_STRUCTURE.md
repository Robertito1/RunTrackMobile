# RunTrack Project Structure

## Package Organization

The project follows a clean architecture pattern with clear separation of concerns:

```
week11.st269142.RunTrack/
├── MainActivity.kt
├── data/
│   └── repository/
│       └── AuthRepository.kt
├── model/
│   ├── AuthResult.kt
│   ├── UiState.kt
│   └── User.kt
├── navigation/
│   ├── NavGraph.kt
│   └── Screen.kt
├── ui/
│   ├── components/
│   │   ├── AppButton.kt
│   │   ├── AppTextField.kt
│   │   ├── ErrorMessage.kt
│   │   └── LoadingScreen.kt
│   ├── screen/
│   │   ├── FeatureScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── LoginScreen.kt
│   │   └── RegisterScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── viewmodel/
    ├── ForgotPasswordViewModel.kt
    ├── LoginViewModel.kt
    └── RegisterViewModel.kt
```

## Architecture Layers

### 1. **Model Layer** (`model/`)
- Data classes and sealed classes
- `User`: User data model
- `AuthResult`: Sealed class for auth operations
- `UiState`: Generic state wrapper for UI

### 2. **Data Layer** (`data/`)
- `repository/`: Data access and business logic
  - `AuthRepository`: Firebase Authentication operations

### 3. **ViewModel Layer** (`viewmodel/`)
- MVVM pattern ViewModels
- State management with StateFlow
- Input validation logic
- `LoginViewModel`, `RegisterViewModel`, `ForgotPasswordViewModel`

### 4. **UI Layer** (`ui/`)
- **`components/`**: Reusable UI components
  - `AppButton`, `AppTextField`, `ErrorMessage`, `LoadingScreen`
- **`screen/`**: Screen composables
  - Auth screens: `LoginScreen`, `RegisterScreen`, `ForgotPasswordScreen`
  - App screens: `HomeScreen`, `FeatureScreen`
- **`theme/`**: Material 3 theming
  - `Color`, `Theme`, `Type`

### 5. **Navigation Layer** (`navigation/`)
- `Screen`: Sealed class for routes
- `NavGraph`: Navigation graph with auth guards

### 6. **Entry Point**
- `MainActivity`: App entry point with navigation setup

## Key Features

✅ **MVVM Architecture**: Clear separation between UI and business logic  
✅ **Repository Pattern**: Centralized data access  
✅ **State Management**: Reactive UI with StateFlow  
✅ **Reusable Components**: DRY principle with custom components  
✅ **Type Safety**: Sealed classes for navigation and results  
✅ **Material 3**: Modern UI design system  
✅ **Auth Guards**: Automatic routing based on auth state  

## Package Naming Convention

- `model`: Data models and state wrappers
- `data`: Data sources and repositories
- `viewmodel`: ViewModels for screens
- `ui.screen`: Screen composables
- `ui.components`: Reusable UI components
- `ui.theme`: Theme configuration
- `navigation`: Navigation logic

This structure provides:
- Easy navigation through the codebase
- Clear responsibility for each package
- Scalability for future features
- Testability with separated concerns
