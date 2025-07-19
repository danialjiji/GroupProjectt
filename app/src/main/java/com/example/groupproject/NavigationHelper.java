package com.example.groupproject;

import android.content.Context;
import android.content.Intent;

public class NavigationHelper {
    public static void goTo(Context context, Class<?> targetClass, String username, int userId) {
        Intent intent = new Intent(context, targetClass);
        intent.putExtra("username", username);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }
}
