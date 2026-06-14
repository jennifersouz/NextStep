package com.example.nextstep

import android.app.Application
import com.example.nextstep.data.local.NextStepDatabase

class NextStepApplication : Application() {

    val database: NextStepDatabase by lazy {
        NextStepDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        @Volatile
        private var INSTANCE: NextStepApplication? = null

        fun getInstance(): NextStepApplication {
            return INSTANCE ?: throw IllegalStateException(
                "NextStepApplication not initialized"
            )
        }
    }
}
