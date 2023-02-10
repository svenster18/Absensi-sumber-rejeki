package com.sumberrejeki.absensi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sumberrejeki.absensi.data.ApiConfig
import com.sumberrejeki.absensi.data.model.AbsensiResponse
import com.sumberrejeki.absensi.data.model.ListAbsensiResponse
import com.sumberrejeki.absensi.databinding.ActivityAbsenBinding
import com.sumberrejeki.absensi.utils.createCustomTempFile
import com.sumberrejeki.absensi.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AbsenActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAbsenBinding
    private var active = false

    private var getFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var handler: Handler
    private lateinit var executor: Executor
    private lateinit var timeFormat: DateFormat

    companion object {
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                getMyLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                showStartMarker(location)
            } else {
                Toast.makeText(
                    this@AbsenActivity,
                    "Location is not found. Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title("Lokasi Pegawai")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findAbsensi()

        timeFormat = SimpleDateFormat("HH:mm")

        executor = Executors.newSingleThreadExecutor()
        handler = Handler(Looper.getMainLooper())

        val now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        active = true
        executor.execute {
            while (active) {
                handler.post {
                    binding.tvJamMasuk.text = timeFormat.format(Date())
                }
                Thread.sleep(60000)
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.cvScanFace.setOnClickListener { startTakePhoto() }
        binding.btnHadir.setOnClickListener {
            if (Objects.equals(
                    binding.ivCamera.drawable.constantState,
                    AppCompatResources.getDrawable(this, R.drawable.camera)!!.constantState
                )
            ) {
                Toast.makeText(this, "Harus ambil foto dahulu", Toast.LENGTH_SHORT).show()
            } else {
                absenMasuk()
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            getMyLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onResume() {
        super.onResume()
        active = true
    }

    override fun onDestroy() {
        super.onDestroy()
        active = false
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AbsenActivity,
                "com.sumberrejeki.absensi",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)

            binding.ivCamera.setImageBitmap(result)
        }
    }

    private fun absenMasuk() {
        showLoading(true)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val nip = "320428180600000201".toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "foto_masuk",
                file.name,
                requestImageFile
            )
            val latitudeMasuk = latitude.toString().toRequestBody("text/plain".toMediaType())
            val longitudeMasuk = longitude.toString().toRequestBody("text/plain".toMediaType())

            val service =
                ApiConfig.getApiService().absen(nip, imageMultipart, latitudeMasuk, longitudeMasuk)

            service.enqueue(object : Callback<AbsensiResponse> {
                override fun onResponse(
                    call: Call<AbsensiResponse>,
                    response: Response<AbsensiResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            handler.post {
                                Toast.makeText(
                                    this@AbsenActivity,
                                    responseBody.messages.success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@AbsenActivity, "Sudah Absen", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AbsensiResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        this@AbsenActivity,
                        "Gagal instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                this@AbsenActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun absenKeluar() {
        showLoading(true)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val nip = "320428180600000201".toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "foto_keluar",
                file.name,
                requestImageFile
            )
            val latitudeMasuk = latitude.toString().toRequestBody("text/plain".toMediaType())
            val longitudeMasuk = longitude.toString().toRequestBody("text/plain".toMediaType())

            val service = ApiConfig.getApiService()
                .absenKeluar(nip, imageMultipart, latitudeMasuk, longitudeMasuk)

            service.enqueue(object : Callback<AbsensiResponse> {
                override fun onResponse(
                    call: Call<AbsensiResponse>,
                    response: Response<AbsensiResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            if (responseBody.error == null) {
                                handler.post {
                                    Toast.makeText(
                                        this@AbsenActivity,
                                        responseBody.messages.success,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        handler.post {
                            Toast.makeText(this@AbsenActivity, "Sudah Absen", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<AbsensiResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        this@AbsenActivity,
                        "Gagal instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                this@AbsenActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun findAbsensi() {
        showLoading(true)
        val client = ApiConfig.getApiService().getAbsensi("320428180600000201")
        client.enqueue(object : Callback<ListAbsensiResponse> {
            override fun onResponse(
                call: Call<ListAbsensiResponse>,
                response: Response<ListAbsensiResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        for (absensi in responseBody.data) {
                            if (absensi.tanggal.equals(dateFormat.format(Date()))) {
                                handler.post {
                                    binding.btnHadir.isEnabled = false
                                    binding.cvAbsenKeluar.isEnabled = true
                                    binding.tvJamMasuk.text = absensi.jamMasuk.substring(0,5)
                                    binding.tvJamKeluar.text = timeFormat.format(Date())
                                    binding.cvAbsenKeluar.setOnClickListener {
                                        if (Objects.equals(
                                                binding.ivCamera.drawable.constantState,
                                                AppCompatResources.getDrawable(this@AbsenActivity, R.drawable.camera)!!.constantState
                                            )
                                        ) {
                                            Toast.makeText(this@AbsenActivity, "Harus ambil foto dahulu", Toast.LENGTH_SHORT).show()
                                        } else {
                                            absenKeluar()
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.e("AbsenActivity", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListAbsensiResponse>, t: Throwable) {
                showLoading(false)
                Log.e("AbsenActivity", "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
