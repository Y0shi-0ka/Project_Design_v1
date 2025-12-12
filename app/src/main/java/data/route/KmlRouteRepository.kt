package com.example.project_design.data.route

import android.content.Context
import com.google.android.gms.maps.model.LatLng

class KmlRouteRepository(
    private val context: Context
) {

    fun loadRouteFromAssets(
        fileName: String,
        id: String,
        name: String
    ): StaticRoute {
        // assets/togashi_no_sato.kml を文字列として読む
        val kmlText = context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }

        val points = parseKmlToLatLngList(kmlText)

        return StaticRoute(
            id = id,
            name = name,
            points = points
        )
    }

    // KML内の <coordinates>〜</coordinates> を全部 LatLng にする簡易パーサ
    private fun parseKmlToLatLngList(kmlText: String): List<LatLng> {
        val result = mutableListOf<LatLng>()

        val regex = Regex("<coordinates>([\\s\\S]*?)</coordinates>")
        val matches = regex.findAll(kmlText)

        for (match in matches) {
            val coordText = match.groupValues[1].trim()

            coordText.split("\\s+".toRegex())
                .filter { it.isNotBlank() }
                .forEach { coord ->
                    val parts = coord.split(",")
                    if (parts.size >= 2) {
                        val lon = parts[0].toDoubleOrNull()
                        val lat = parts[1].toDoubleOrNull()
                        if (lat != null && lon != null) {
                            result.add(LatLng(lat, lon))
                        }
                    }
                }
        }

        return result
    }
}