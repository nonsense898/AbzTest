# AbzTest

## Description

AbzTest is an Android application that displays a list of users and allows you to register new users. It includes functionality such as network connectivity checks, real-time data updates, user list sorting, and animations.

## Features
  • The app follows a single activity architecture using a navigation framework, ensuring smooth transitions between different screens while simplifying lifecycle management.
  •	Splash Screen: Displays at app launch.
	•	No Connection Screen: Includes a retry button to handle network issues.
	•	User List: Sorted by registration date, dynamically loaded using Paging3 for efficient data handling with smooth animations.
	•	User Registration: Includes fields for name, email, phone number, and job position (e.g., Frontend, Backend, Designer, QA, Security).
	•	Custom TextInput: Flexible input handling with custom text fields.
	•	Real-Time Network Monitoring: Powered by LiveData and NetworkManager, ensuring the app responds instantly to connectivity changes.
	•	Glide Integration: For efficient image loading, especially for avatar management.


## Libraries Used

- Retrofit: for network requests
- Paging3: for paginated data loading
- Glide: for image handling
- Custom TextInput Library: for flexible input field customization
- LiveData: for real-time network status updates
- NetworkManager: for monitoring internet connectivity

## Troubleshooting and Common Issues

### Custom TextInput Library
The standard TextInput library lacked flexibility for customization, such as appearance customization, input validation, and handling dynamic input errors. A custom TextInput library was used to solve these issues and enhance the user experience.

### Paging3
Standard pagination methods did not offer the flexibility and smoothness needed for infinite scrolling. Paging3 provided a more elegant solution with better loading animations, improving the app's performance and user interface.

### No Connection Fragment
The app includes a NoConnectionFragment to handle network connectivity issues. This fragment uses LiveData to monitor network status in real-time and a NetworkManager to track internet connectivity. Upon reconnection, the app automatically redirects users to the main screen.

## Getting Started

### Prerequisites
- Android Studio
- Minimum SDK version: 21
- Kotlin 1.5+

### Installation

1. Clone the repo:
   
    git clone https://github.com/your-username/your-repo-name.git
    
2. Open the project in Android Studio.
3. Sync the project with Gradle files.

### Usage

- Run the app in the emulator or on a physical device.
- Ensure the internet connection is active to retrieve the list of users.
- Register a new user by filling out the registration form, then check the user list.

### License
Distributed under the MIT License. See LICENSE for more information.

---
