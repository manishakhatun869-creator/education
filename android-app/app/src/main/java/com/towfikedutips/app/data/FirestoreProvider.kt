package com.towfikedutips.app.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreProvider {
    private const val API_KEY = "AIzaSyAsURYh7jU--SbNgRatHOK-xngKwnTo_qw"
    private const val APP_ID = "1:450434272070:web:384e6d50eb5efa2477b347"
    private const val PROJECT_ID = "gen-lang-client-0495775898"
    private const val DATABASE_ID = "ai-studio-towfikedutips-dc762f0f-26df-4eb7-bc66-e61e13a7b0a6"

    fun getFirestore(context: Context): FirebaseFirestore {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey(API_KEY)
                    .setApplicationId(APP_ID)
                    .setProjectId(PROJECT_ID)
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return try {
            // Retrieve instance referencing the custom Firestore database specified in configuration
            FirebaseFirestore.getInstance(DATABASE_ID)
        } catch (e: Exception) {
            try {
                // Graceful fallback to default database instance if the version does not support multi-db parameter
                FirebaseFirestore.getInstance()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ex
            }
        }
    }
}
