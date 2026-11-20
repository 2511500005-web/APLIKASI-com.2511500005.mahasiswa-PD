package com._2511500005.mahasiswa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com._2511500005.mahasiswa.databinding.ItemMahasiswaBinding
import com._2511500005.mahasiswa.model.Mahasiswa

class MahasiswaAdapter(
    private val onKlik: (Mahasiswa) -> Unit,
    private val onTahanLama: (Mahasiswa) -> Unit
) : RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    private val items = mutableListOf<Mahasiswa>()

    fun setData(baru: List<Mahasiswa>) {
        items.clear()
        items.addAll(baru)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemMahasiswaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMahasiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mhs = items[position]
        holder.binding.tvNama.text = mhs.nama
        holder.binding.tvNim.text = "NIM: ${mhs.nim}"
        holder.binding.tvJurusan.text = mhs.jurusan
        holder.binding.root.setOnClickListener { onKlik(mhs) }
        holder.binding.root.setOnLongClickListener {
            onTahanLama(mhs)
            true
        }
    }
}
