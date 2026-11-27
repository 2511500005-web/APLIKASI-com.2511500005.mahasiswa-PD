package com._2511500005.mahasiswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com._2511500005.mahasiswa.api.HasilApi
import com._2511500005.mahasiswa.api.MahasiswaRepository
import com._2511500005.mahasiswa.databinding.ActivityTambahMahasiswaBinding
import com._2511500005.mahasiswa.model.MahasiswaRequest
import kotlinx.coroutines.launch

class TambahMahasiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahMahasiswaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpan.setOnClickListener { simpan() }
        binding.btnBatal.setOnClickListener { finish() }
    }

    private fun simpan() {
        val nama = binding.etNama.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val jurusan = binding.etJurusan.text.toString().trim()

        // 1. Validasi client-side (sebelum request dikirim)
        binding.tilNama.error = null
        binding.tilNim.error = null
        binding.tilJurusan.error = null

        if (nama.isEmpty() || nim.isEmpty() || jurusan.isEmpty()) {
            Toast.makeText(this, "Lengkapi nama, NIM, dan jurusan", Toast.LENGTH_SHORT).show()
            return
        }
        if (nim.length < 8) {
            binding.tilNim.error = "NIM minimal 8 karakter"
            return
        }

        // 2. Kunci tombol agar tidak terkirim dua kali
        setLoading(true)

        // 3. Kirim POST ke server
        lifecycleScope.launch {
            when (val hasil = MahasiswaRepository.tambah(MahasiswaRequest(nim, nama, jurusan))) {
                is HasilApi.Sukses -> {
                    Toast.makeText(this@TambahMahasiswaActivity, "Tersimpan", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // pemicu refresh di MainActivity
                    finish()
                }
                is HasilApi.Gagal -> {
                    setLoading(false)
                    Toast.makeText(this@TambahMahasiswaActivity, hasil.pesan, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(aktif: Boolean) {
        binding.btnSimpan.isEnabled = !aktif
        binding.btnSimpan.text = if (aktif) "Menyimpan..." else "SIMPAN"
        binding.progressBar.visibility = if (aktif) View.VISIBLE else View.GONE
    }
}