package com.og.medicare.data;

import com.google.firebase.auth.FirebaseUser;
import com.og.medicare.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public static Result.Success handleLogin(FirebaseUser user) {
        LoggedInUser fakeUser =
                new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        "Jane Doe");
        return new Result.Success<>(fakeUser);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}