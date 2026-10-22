package com.hoopoe.launcher.data

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class TripRecord(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val distanceKm: Double,
    val avgSpeedKmh: Double,
    val fuelConsumedLiters: Double,
    val economyKmL: Double,
    val source: String // "OBD2_AUTO" | "GPS_MANUAL"
)

data class RefuelRecord(
    val id: String,
    val timestamp: Long,
    val odometerKm: Double,
    val liters: Double,
    val costTotal: Double,
    val costPerLiter: Double,
    val isFullTank: Boolean,
    val source: String // "AUTO_DETECTED" | "MANUAL"
)

class TripFuelRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("hoopoe_trip_fuel_store", Context.MODE_PRIVATE)

    private val _isObdOnline = MutableStateFlow(prefs.getBoolean("obd_online", true))
    val isObdOnline: StateFlow<Boolean> = _isObdOnline.asStateFlow()

    private val _fuelPercent = MutableStateFlow(prefs.getFloat("fuel_pct", 68.0f))
    val fuelPercent: StateFlow<Float> = _fuelPercent.asStateFlow()

    private val _odometerKm = MutableStateFlow(prefs.getFloat("odometer_km", 48290.0f).toDouble())
    val odometerKm: StateFlow<Double> = _odometerKm.asStateFlow()

    private val _fuelPricePerLiter = MutableStateFlow(prefs.getFloat("fuel_price", 102.50f))
    val fuelPricePerLiter: StateFlow<Float> = _fuelPricePerLiter.asStateFlow()

    private val _refuelList = MutableStateFlow(loadRefuels())
    val refuelList: StateFlow<List<RefuelRecord>> = _refuelList.asStateFlow()

    private val _currentTrip = MutableStateFlow(
        TripRecord(
            id = "trip-active",
            startTime = System.currentTimeMillis() - 8140000,
            endTime = System.currentTimeMillis(),
            distanceKm = 142.8,
            avgSpeedKmh = 62.4,
            fuelConsumedLiters = 7.76,
            economyKmL = 18.4,
            source = if (_isObdOnline.value) "OBD2_AUTO" else "GPS_MANUAL"
        )
    )
    val currentTrip: StateFlow<TripRecord> = _currentTrip.asStateFlow()

    fun toggleObdConnection() {
        val newVal = !_isObdOnline.value
        _isObdOnline.value = newVal
        prefs.edit().putBoolean("obd_online", newVal).apply()
        _currentTrip.value = _currentTrip.value.copy(
            source = if (newVal) "OBD2_AUTO" else "GPS_MANUAL"
        )
        playTactileBeep()
    }

    fun addRefuel(liters: Double, costPerLiter: Double, odometer: Double, isFullTank: Boolean) {
        val totalCost = liters * costPerLiter
        val newRecord = RefuelRecord(
            id = "refuel-${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            odometerKm = odometer,
            liters = liters,
            costTotal = totalCost,
            costPerLiter = costPerLiter,
            isFullTank = isFullTank,
            source = if (_isObdOnline.value) "AUTO_DETECTED" else "MANUAL"
        )

        val updatedList = listOf(newRecord) + _refuelList.value
        _refuelList.value = updatedList
        saveRefuels(updatedList)

        // Update tank percent (42.0 Liter standard tank capacity)
        val addedPct = (liters / 42.0 * 100.0).toFloat()
        val newPct = if (isFullTank) 100f else (_fuelPercent.value + addedPct).coerceIn(0f, 100f)
        _fuelPercent.value = newPct
        _odometerKm.value = odometer

        prefs.edit()
            .putFloat("fuel_pct", newPct)
            .putFloat("odometer_km", odometer.toFloat())
            .apply()

        playConfirmBeep()
    }

    private fun loadRefuels(): List<RefuelRecord> {
        val jsonStr = prefs.getString("refuels_json", null) ?: return listOf(
            RefuelRecord(
                id = "refuel-seed",
                timestamp = System.currentTimeMillis() - 86400000L * 3L,
                odometerKm = 47950.0,
                liters = 32.5,
                costTotal = 3331.25,
                costPerLiter = 102.50,
                isFullTank = true,
                source = "AUTO_DETECTED"
            )
        )
        val list = mutableListOf<RefuelRecord>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RefuelRecord(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("timestamp"),
                        odometerKm = obj.getDouble("odometerKm"),
                        liters = obj.getDouble("liters"),
                        costTotal = obj.getDouble("costTotal"),
                        costPerLiter = obj.getDouble("costPerLiter"),
                        isFullTank = obj.getBoolean("isFullTank"),
                        source = obj.getString("source")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveRefuels(list: List<RefuelRecord>) {
        try {
            val arr = JSONArray()
            list.take(20).forEach { item ->
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("timestamp", item.timestamp)
                    put("odometerKm", item.odometerKm)
                    put("liters", item.liters)
                    put("costTotal", item.costTotal)
                    put("costPerLiter", item.costPerLiter)
                    put("isFullTank", item.isFullTank)
                    put("source", item.source)
                }
                arr.put(obj)
            }
            prefs.edit().putString("refuels_json", arr.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var instance: TripFuelRepository? = null

        fun getInstance(context: Context): TripFuelRepository =
            instance ?: synchronized(this) {
                instance ?: TripFuelRepository(context.applicationContext).also { instance = it }
            }

        fun playTactileBeep() {
            // Intentionally silent / no-op to prevent ToneGenerator audio server crash on automotive head units
        }

        fun playConfirmBeep() {
            // Intentionally silent / no-op to prevent ToneGenerator audio server crash on automotive head units
        }
    }
}
