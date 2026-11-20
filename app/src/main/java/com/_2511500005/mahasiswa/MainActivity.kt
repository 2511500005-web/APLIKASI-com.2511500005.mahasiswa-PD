package com._2511500005.mahasiswa

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com._2511500005.mahasiswa.api.HasilApi
import com._2511500005.mahasiswa.api.MahasiswaRepository
import com._2511500005.mahasiswa.databinding.ActivityMainBinding
import com._2511500005.mahasiswa.databinding.DialogFormMahasiswaBinding
import com._2511500005.mahasiswa.model.Mahasiswa
import com._2511500005.mahasiswa.model.MahasiswaRequest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MahasiswaAdapter

    private val warnaHijau = Color.parseColor("#2E7D32")
    private val warnaMerah = Color.parseColor("#C62828")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MahasiswaAdapter(
            onKlik = { tampilkanFormUbah(it) },
            onTahanLama = { konfirmasiHapus(it) }
        )
        binding.rvMahasiswa.layoutManager = LinearLayoutManager(this)
        binding.rvMahasiswa.adapter = adapter

        binding.btnMuat.setOnClickListener { muatData() }
        binding.btnCari.setOnClickListener { muatData() }
        binding.fabTambah.setOnClickListener { tampilkanFormTambah() }

        muatData()
    }

    private fun muatData() {
        val keyword = binding.etCari.text.toString().trim()

        lifecycleScope.launch {
            setLoading(true)
            setStatus("Status: Menghubungkan ke server...", Color.DKGRAY)

            val hasil = MahasiswaRepository.ambilSemua(keyword)

            setLoading(false)
            when (hasil) {
                is HasilApi.Sukses -> {
                    adapter.setData(hasil.data)
                    binding.tvKosong.visibility = if (hasil.data.isEmpty()) View.VISIBLE else View.GONE
                    setStatus("Status: Terhubung (${hasil.data.size} data ditemukan)", warnaHijau)
                }
                is HasilApi.Gagal -> setStatus("Status: ${hasil.pesan}", warnaMerah)
            }
        }
    }

    private fun setLoading(aktif: Boolean) {
        binding.progressBar.visibility = if (aktif) View.VISIBLE else View.GONE
        binding.btnMuat.isEnabled = !aktif
        binding.btnCari.isEnabled = !aktif
        binding.fabTambah.isEnabled = !aktif
    }

    private fun setStatus(teks: String, warna: Int) {
        binding.tvConnectionStatus.text = teks
        binding.tvConnectionStatus.setTextColor(warna)
    }

    private fun tampilkanFormTambah() {
        tampilkanForm("Tambah Mahasiswa", null) { req ->
            lifecycleScope.launch {
                prosesHasilTulis(MahasiswaRepository.tambah(req))
            }
        }
    }

    private fun tampilkanFormUbah(mhs: Mahasiswa) {
        tampilkanForm("Ubah Data Mahasiswa", mhs) { req ->
            lifecycleScope.launch {
                prosesHasilTulis(MahasiswaRepository.ubah(mhs.id, req))
            }
        }
    }

    private fun konfirmasiHapus(mhs: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle("Hapus data?")
            .setMessage("${mhs.nama} (${mhs.nim}) akan dihapus.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    prosesHasilTulis(MahasiswaRepository.hapus(mhs.id))
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun tampilkanForm(judul: String, awal: Mahasiswa?, onSimpan: (MahasiswaRequest) -> Unit) {
        val form = DialogFormMahasiswaBinding.inflate(layoutInflater)
        awal?.let {
            form.etNim.setText(it.nim)
            form.etNama.setText(it.nama)
            form.etJurusan.setText(it.jurusan)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(judul)
            .setView(form.root)
            .setPositiveButton("Simpan", null) // di-set ulang di bawah agar dialog tidak tertutup saat validasi gagal
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nim = form.etNim.text.toString().trim()
                val nama = form.etNama.text.toString().trim()
                val jurusan = form.etJurusan.text.toString().trim()

                if (nim.isEmpty() || nama.isEmpty() || jurusan.isEmpty()) {
                    Toast.makeText(this, "NIM, nama, dan jurusan wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dialog.dismiss()
                onSimpan(MahasiswaRequest(nim, nama, jurusan))
            }
        }
        dialog.show()
    }

    private fun prosesHasilTulis(hasil: HasilApi<Unit>) {
        when (hasil) {
            is HasilApi.Sukses -> {
                Toast.makeText(this, hasil.pesan, Toast.LENGTH_SHORT).show()
                muatData()
            }
            is HasilApi.Gagal -> {
                AlertDialog.Builder(this)
                    .setTitle("Gagal")
                    .setMessage(hasil.pesan)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
