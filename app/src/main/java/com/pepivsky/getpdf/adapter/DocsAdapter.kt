package com.pepivsky.getpdf.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pepivsky.getpdf.R
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import java.io.File

class DocsAdapter(private val docs: MutableList<File>, private val context: Context): RecyclerView.Adapter<DocsAdapter.DocsHolder>() {

    val TAG = "Adapter"
    init {

        Log.d(TAG, "init, docs: $docs")
        setup()
    }

    class DocsHolder(val view: View): RecyclerView.ViewHolder(view) {
        lateinit var tvName: TextView
        lateinit var tvNumPages: TextView
        lateinit var ibDelete: ImageButton

        fun render(doc: File) {
            tvName = view.findViewById(R.id.tvNameDoc)
            tvNumPages = view.findViewById(R.id.tvNumPages)
            ibDelete = view.findViewById(R.id.ibDelete)
            //ibDelete = view.findViewById()
            val myDocument = PDDocument.load(doc)
            val numPages = myDocument.numberOfPages


            tvName.text = doc.name
            val textNumPages = "Paginas: $numPages"
            tvNumPages.text = textNumPages
            myDocument.close() // importante cerrar el documento


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DocsHolder(
            layoutInflater.inflate(
                R.layout.item_file,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocsHolder, position: Int) {
       val doc = holder.render(docs[position])

        holder.ibDelete.setOnClickListener {
            docs.remove(docs[position])
            Log.d(TAG, "onClic ib delete")
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = docs.size

    private fun setup() {
        PDFBoxResourceLoader.init(context);
    }

}