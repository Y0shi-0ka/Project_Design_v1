package com.example.project_design.data.store

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "step_baseline")

class StepBaselineStore(private val context: Context) {

    private val KEY_DATE = stringPreferencesKey("baseline_date")
    private val KEY_BASELINE = longPreferencesKey("baseline_steps_since_boot")

    suspend fun getBaseline(date: String): Long? {
        val prefs = context.dataStore.data.first()
        val savedDate = prefs[KEY_DATE] ?: return null
        if (savedDate != date) return null
        return prefs[KEY_BASELINE]
    }

    suspend fun setBaseline(date: String, baselineStepsSinceBoot: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DATE] = date
            prefs[KEY_BASELINE] = baselineStepsSinceBoot
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}