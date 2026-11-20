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

        setupRecyclerView()
        setupActions()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = MahasiswaAdapter(
            onItemClick = { showDetail(it.id) },      // klik item -> GET by ID
            onItemLongClick = { konfirmasiHapus(it) } // tekan lama -> hapus
        )
        binding.rvMahasiswa.layoutManager = LinearLayoutManager(this)
        binding.rvMahasiswa.adapter = adapter
    }

    private fun setupActions() {
        binding.btnTambah.setOnClickListener {
            tampilkanForm("Tambah Mahasiswa", null) { req ->
                lifecycleScope.launch { prosesHasilTulis(MahasiswaRepository.tambah(req)) }
            }
        }
        binding.btnCari.setOnClickListener {
            loadData(binding.etSearch.text.toString().trim())
        }
        binding.btnReset.setOnClickListener {
            binding.etSearch.text?.clear()
            loadData()
        }
    }

    // GET semua data, atau GET ?search= jika keyword diisi
    private fun loadData(keyword: String = "") {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE

            when (val hasil = MahasiswaRepository.ambilSemua(keyword)) {
                is HasilApi.Sukses -> {
                    adapter.updateData(hasil.data)
                    binding.tvJudul.text = "Data Mahasiswa (${hasil.data.size})"

                    if (hasil.data.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = if (keyword.isEmpty()) {
                            "Belum ada data mahasiswa"
                        } else {
                            "Tidak ditemukan untuk \"$keyword\""
                        }
                    }
                    setStatus("Status: Terhubung (${hasil.data.size} data)", warnaHijau)
                }
                is HasilApi.Gagal -> {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "Gagal memuat data"
                    setStatus("Status: ${hasil.pesan}", warnaMerah)
                }
            }

            binding.progressBar.visibility = View.GONE
        }
    }

    // GET by ID: dipanggil saat item diklik
    private fun showDetail(id: Int) {
        lifecycleScope.launch {
            when (val hasil = MahasiswaRepository.ambilDetail(id)) {
                is HasilApi.Sukses -> tampilkanDialogDetail(hasil.data)
                is HasilApi.Gagal -> {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Gagal memuat detail")
                        .setMessage(hasil.pesan)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }

    private fun tampilkanDialogDetail(m: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle(m.nama)
            .setMessage("ID: ${m.id}\nNIM: ${m.nim}\nJurusan: ${m.jurusan}")
            .setPositiveButton("Ubah") { _, _ -> tampilkanFormUbah(m) }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun tampilkanFormUbah(m: Mahasiswa) {
        tampilkanForm("Ubah Data Mahasiswa", m) { req ->
            lifecycleScope.launch { prosesHasilTulis(MahasiswaRepository.ubah(m.id, req)) }
        }
    }

    private fun konfirmasiHapus(m: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle("Hapus data?")
            .setMessage("${m.nama} (${m.nim}) akan dihapus.")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch { prosesHasilTulis(MahasiswaRepository.hapus(m.id)) }
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
                loadData(binding.etSearch.text.toString().trim())
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

    private fun setStatus(teks: String, warna: Int) {
        binding.tvConnectionStatus.text = teks
        binding.tvConnectionStatus.setTextColor(warna)
    }
}