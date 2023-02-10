package com.sumberrejeki.absensi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumberrejeki.absensi.data.model.DataItem
import com.sumberrejeki.absensi.databinding.ItemKehadiranBinding
import java.text.SimpleDateFormat

class AbsensiAdapter(private val listAbsensi: List<DataItem>) : RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsensiViewHolder {
        val binding = ItemKehadiranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbsensiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbsensiViewHolder, position: Int) {
        val absensi = listAbsensi[position]
        holder.binding.tvTanggalListAbsensi.text = absensi.tanggal.substring(8,10)
        val monthFormat = SimpleDateFormat("MMM")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val hourFormat = SimpleDateFormat("HH:mm")
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val tanggal = dateFormat.parse(absensi.tanggal)
        val jamMasuk = timeFormat.parse(absensi.jamMasuk)

        holder.binding.tvBulanListAbsensi.text = monthFormat.format(tanggal)
        holder.binding.tvJamMasuk.text = hourFormat.format(jamMasuk)
        if (absensi.jamKeluar != null) {
            val jamKeluar = timeFormat.parse(absensi.jamKeluar)
            holder.binding.tvJamKeluar.text = hourFormat.format(jamKeluar)
        }
    }

    override fun getItemCount(): Int = listAbsensi.size

    class AbsensiViewHolder(val binding: ItemKehadiranBinding) : RecyclerView.ViewHolder(binding.root)
}