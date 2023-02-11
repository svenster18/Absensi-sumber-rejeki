package com.sumberrejeki.absensi

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sumberrejeki.absensi.data.model.Absensi
import com.sumberrejeki.absensi.databinding.ActivityAbsenBinding
import com.sumberrejeki.absensi.databinding.ActivityDetailBinding
import java.io.IOException
import java.util.*
import kotlin.math.abs

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_ABSENSI = "extra_absensi"
    }

    private lateinit var binding: ActivityDetailBinding

    private var absensi: Absensi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        absensi = intent.getParcelableExtra(EXTRA_ABSENSI)

        binding.tvJamMasuk.text = absensi?.jamMasuk?.substring(0, 5)
        if (absensi?.jamKeluar != null) binding.tvJamKeluar.text = absensi?.jamKeluar?.substring(0, 5)

//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.detail_map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val mMap = googleMap

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (absensi != null) {
            var latitude = absensi!!.latitudeMasuk.toDouble()
            var longitude = absensi!!.longitudeMasuk.toDouble()
            var foto = absensi!!.fotoMasuk
            if(hour >= 17 && absensi!!.latitudeKeluar != null && absensi!!.longitudeKeluar != null) {
                latitude = absensi!!.latitudeKeluar!!.toDouble()
                longitude = absensi!!.longitudeKeluar!!.toDouble()
                if (absensi!!.fotoKeluar != null)
                    foto = absensi!!.fotoKeluar.toString()
            }
            Glide.with(this)
                .load("https://sumberrejekiapi.000webhostapp.com/img/$foto")
                .into(binding.ivCamera)
            val startLocation = LatLng(latitude, longitude)
            if (getAddressName(latitude, longitude) != null) {
                binding.tvLokasi.text = getAddressName(latitude, longitude)
            }
            mMap.addMarker(
                MarkerOptions()
                    .position(startLocation)
                    .title("Lokasi Pegawai")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
        }

    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@DetailActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }
}