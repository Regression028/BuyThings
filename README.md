# 🛒 BuyThings – Android E-Commerce App

**BuyThings** is a native Android e-commerce application built using Kotlin and Jetpack Compose. It provides users with a seamless shopping experience, allowing them to explore products, browse categories, manage their shopping cart, and complete purchases through an integrated checkout system.

The application follows modern Android development practices, using the MVVM architecture and Repository Pattern to maintain clean, scalable, and maintainable code.

## ✨ Features

- **User Authentication:** Secure user authentication using Firebase Authentication.
- **Google Sign-In:** Google authentication support.
- **Product Browsing:** Explore and discover available products.
- **Product Categories:** Browse products through different categories.
- **Shopping Cart:** Add, remove, and manage products in the cart.
- **User Profile:** Manage user profile information.
- **Checkout:** Seamless checkout experience.
- **Online Payments:** Razorpay payment gateway integration through a Node.js backend.
- **Cloud Image Storage:** Product images managed using Cloudinary.
- **Cloud Database:** Firebase Firestore for storing application data.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Modern declarative UI |
| Material 3 | UI components and design |
| MVVM | Application architecture |
| Repository Pattern | Data management |
| Hilt | Dependency injection |
| Firebase Authentication | User authentication |
| Firebase Firestore | Cloud database |
| Cloudinary | Image storage and delivery |
| Node.js | Backend development |
| Express.js | REST API |
| Razorpay | Payment gateway |
| Coroutines | Asynchronous programming |

## 🏗️ Architecture

BuyThings follows the **MVVM (Model-View-ViewModel)** architecture combined with the Repository Pattern.

```text
            Presentation Layer
                    |
             Jetpack Compose
                    |
                ViewModel
                    |
             Repository Layer
                    |
          ---------------------
          |                   |
      Firebase            Backend API
          |                   |
      Firestore          Node.js / Express
                              |
                         Razorpay
```

### Architecture Components

- **View:** Jetpack Compose screens responsible for displaying the UI.
- **ViewModel:** Handles UI-related state and business logic.
- **Repository:** Acts as a single source of data and abstracts data operations.
- **Firebase:** Handles authentication and cloud data storage.
- **Backend:** Manages payment-related API operations securely.

## 💳 Payment Integration

BuyThings uses Razorpay for online payment processing.

The payment flow involves:

1. The user proceeds to checkout.
2. The Android application requests an order from the backend.
3. The Node.js backend creates a Razorpay order.
4. The application initiates the payment process.
5. Payment details are verified on the backend.
6. Confirmed payment information can be stored in Firebase Firestore.

**Note:** Razorpay API secret keys are maintained on the backend and are never exposed in the Android application.

## 📁 Project Structure

```text
BuyThings/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── .../
│           │       ├── data/
│           │       ├── di/
│           │       ├── model/
│           │       ├── navigation/
│           │       ├── repository/
│           │       ├── ui/
│           │       └── viewmodel/
│           │
│           └── res/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

*The structure above represents the intended architectural organization; adjust package names to match the actual project.*

## 🚀 Getting Started

Follow these steps to run BuyThings locally.

### Prerequisites

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle configuration
- Firebase project
- Cloudinary account
- Razorpay account (for payment functionality)

### Installation

**1. Clone the repository**

```bash
git clone <YOUR_REPOSITORY_URL>
```

**2. Open the project**

Open the cloned project in Android Studio.

**3. Configure Firebase**

- Create a Firebase project.
- Register your Android application.
- Download `google-services.json`.
- Place it inside the `app/` directory.
- Enable the required Firebase services.

**4. Configure Backend**

Clone and configure the BuyThings backend separately.

```bash
git clone <YOUR_BACKEND_REPOSITORY_URL>
cd BuyThings-Backend
npm install
```

Configure the environment variables using the provided `.env.example` file.

**5. Run the application**

Sync the Gradle project and run the application using Android Studio on an emulator or a physical Android device.

## 🔐 Security

- Firebase Authentication for user identity management.
- Sensitive payment credentials are stored on the backend.
- Payment verification is handled server-side.
- Environment variables are used to manage backend secrets.

## 📸 Screenshots
<p align="center">
  <img src="https://github.com/user-attachments/assets/f8cb3d87-d634-4a1b-9a31-99680f22cb4b" width="150"/>
  <img src="https://github.com/user-attachments/assets/b31ab54f-7e85-4418-bcf2-9b93dae54976" width="150"/>
  <img src="https://github.com/user-attachments/assets/235414a2-3e64-4e1e-b951-2b3b4f0934a4" width="150"/>
  <img src="https://github.com/user-attachments/assets/1b51d93e-0b68-4ece-9fd4-5681ff831d73" width="150"/>
  <img src="https://github.com/user-attachments/assets/1f4a962f-b490-45a3-b903-6b1d08616d38" width="150"/>
  <img src="https://github.com/user-attachments/assets/c26cb863-7f91-4dfd-878e-da6265cca1e3" width="150"/>
  <img src="https://github.com/user-attachments/assets/33455c28-2473-4b6c-abde-965224b7b4cd" width="150"/>
</p>

## 🔮 Future Improvements

- Order history and tracking.
- Wishlist functionality.
- Product search and filtering.
- Improved product recommendations.
- Push notifications.
- Enhanced payment and order management.

## 👨‍💻 Developer

**Gaurav Bansal**

Developed as a native Android e-commerce application using Kotlin and modern Android development technologies.

---

⭐ If you like this project, consider giving it a star!
