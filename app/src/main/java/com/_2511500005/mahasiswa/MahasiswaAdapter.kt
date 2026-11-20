package com._2511500005.mahasiswa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com._2511500005.mahasiswa.databinding.ItemMahasiswaBinding
import com._2511500005.mahasiswa.model.Mahasiswa

class MahasiswaAdapter(
    private val onItemClick: (Mahasiswa) -> Unit,
    private val onItemLongClick: (Mahasiswa) -> Unit
) : RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    private var items: List<Mahasiswa> = emptyList()

    fun updateData(newData: List<Mahasiswa>) {
        items = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemMahasiswaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(m: Mahasiswa) {
            binding.tvNama.text = m.nama
            binding.tvNim.text = "NIM: ${m.nim}"
            binding.tvJurusan.text = m.jurusan
            binding.root.setOnClickListener { onItemClick(m) }
            binding.root.setOnLongClickListener {
                onItemLongClick(m)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMahasiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}