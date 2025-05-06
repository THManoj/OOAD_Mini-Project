# Java Blog MVC Application

This project is a pure Java MVC blog application with a Swing GUI. It implements four GoF design patterns: Adapter, Facade, Proxy, and Flyweight. The application allows users to manage blog posts, comments, and user accounts.

## Project Structure

```
src
└── com
    └── example
        └── blog
            ├── App.java
            ├── model
            │   ├── Category.java
            │   ├── Comment.java
            │   ├── Post.java
            │   ├── Role.java
            │   ├── Tag.java
            │   └── User.java
            ├── repository
            │   └── InMemoryStore.java
            ├── controller
            │   ├── AuthController.java
            │   ├── CommentController.java
            │   ├── PostController.java
            │   └── UserController.java
            ├── service
            │   ├── AuthService.java
            │   ├── CommentService.java
            │   ├── CommentServiceProxy.java
            │   ├── LikeService.java
            │   ├── PostService.java
            │   └── UserService.java
            ├── view
            │   ├── CommentView.java
            │   ├── LoginView.java
            │   ├── MainView.java
            │   ├── PostManagementView.java
            │   └── UserManagementView.java
            ├── util
            │   ├── Hasher.java
            │   ├── NotificationFacade.java
            │   └── PasswordHasherAdapter.java
            └── exception
                ├── AppException.java
                └── GlobalExceptionHandler.java
```

## Features

- **User Authentication**: Users can log in and manage their accounts.
- **Post Management**: Users can create, edit, and delete blog posts.
- **Comment Management**: Users can add, edit, and delete comments on posts.
- **Role-Based Access Control**: Different roles (ADMIN, AUTHOR, READER) determine user permissions.
- **Notifications**: The application provides notifications for actions like comment replies.

## Design Patterns Implemented

- **Adapter**: The `PasswordHasherAdapter` wraps a third-party password hashing utility to provide a consistent interface.
- **Facade**: The `NotificationFacade` simplifies notification handling, allowing for easy integration of different notification methods.
- **Proxy**: The `CommentServiceProxy` enforces ownership checks before allowing modifications to comments.
- **Flyweight**: The application uses shared instances of `Tag` and `Category` to optimize memory usage.

## Setup Instructions

1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Compile the Java files using your preferred IDE or command line.
4. Run the `App.java` file to start the application.

## Usage Guidelines

- Use the login view to authenticate as a user.
- Navigate through the main view to manage posts, comments, and users.
- Admin users have additional privileges to manage all aspects of the application.

## License

This project is licensed under the MIT License.