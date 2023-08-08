package com.alephri.parvadaclone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alephri.sdk.data.base.State
import com.alephri.sdk.facade.AlephSDK
import com.aleprhi.sdk.GetRiskRouterQuery
import com.aleprhi.sdk.type.GeoPointInput
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class MapActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private val alephSDK = AlephSDK(this, apiKey = "1010101010101")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
        getRiskRouter()
    }

    private fun getRiskRouter() {
        MainScope().launch {
            alephSDK.getRoute(
                GeoPointInput(lat = 19.3316323, lon = -99.2040659),
                GeoPointInput(lat = 19.3015333, lon = -99.1590659)
            ).collect {
                when (it) {
                    is State.Success -> {
                        Log.d("Success", "Router ${it.data}")
                        drawLines(it.data)

                    }
                    is State.Failure -> {
                        Log.d("Failure", "Error ${it.exception}")
                    }
                    is State.Progress -> {
                        Log.d("Progress", "Is Loading")
                    }
                }
            }
        }
    }

    private fun drawLines(router: GetRiskRouterQuery.RiskRouter) {
        router.routes?.forEach {
            val routeScheme = it.routeSchema
            val featureCollectionJson = FeatureCollection.fromJson(routeScheme)
            drawLine(featureCollectionJson, it.riskLevel)
            setUpZoom(featureCollectionJson)
        }
    }

    private fun getHexColor(riskLevel: String): String {
        var color = "#000000"
        when(riskLevel) {
            "low" -> {
                color = "#FFFF00"
            }
            "medium" -> {
                color = "#FF8000"
            }
            "high" -> {
                color = "#FF0000"
            }
        }
        return color
    }

    private fun drawLine(data: FeatureCollection, riskLevel: String) {
        mapView?.getMapboxMap()?.getStyle { style ->
            data.features()?.forEach { feature ->
                val tag = riskLevel + Random()
                val lineLayer = lineLayer(tag, tag) {
                    lineCap(LineCap.ROUND)
                    lineJoin(LineJoin.ROUND)
                    lineOpacity(0.7)
                    lineWidth(5.0)
                    lineColor(getHexColor(riskLevel))
                }
                val geoJsonSource = geoJsonSource(tag) {
                    feature(feature)
                }
                style.addLayer(lineLayer)
                style.addSource(geoJsonSource)
            }
        }
    }

    private fun setUpZoom(data: FeatureCollection) {
        val triangleCoordinates = mutableListOf<Point>()
        data.features()?.forEach { feature ->
            feature.geometry()?.let { geometry ->
                if (geometry is LineString) {
                    geometry.coordinates().forEach { point ->
                        val latitudeFocus = point.latitude()
                        val longitudeFocus = point.longitude()
                        triangleCoordinates.add(Point.fromLngLat(longitudeFocus, latitudeFocus))
                    }
                }
            }
        }

        if (triangleCoordinates.isNotEmpty()) {
            val polygon = Polygon.fromLngLats(listOf(triangleCoordinates))
            val cameraPosition = mapView?.getMapboxMap()
                ?.cameraForGeometry(polygon, EdgeInsets(30.0, 30.0, 30.0, 30.0))
            if (cameraPosition != null) {
                mapView?.getMapboxMap()?.setCamera(cameraPosition)
            }
        }
    }
}