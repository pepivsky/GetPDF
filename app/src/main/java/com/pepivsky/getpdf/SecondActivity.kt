package com.pepivsky.getpdf

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pepivsky.getpdf.adapter.DocsAdapter
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import java.io.File

class SecondActivity : AppCompatActivity(), HandlePathOzListener.SingleUri{

    val TAG = "SecondActivity"

    lateinit var rvDocs: RecyclerView
    lateinit var adapter: DocsAdapter

    private lateinit var handlePathOz: HandlePathOz

    lateinit var btnAdd: FloatingActionButton

   // private lateinit var handlePathOz: HandlePathOz
    val listaFiles = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        handlePathOz = HandlePathOz(this, this)

        val TAG = "SecondActivity"

        val extras = intent.extras
        val lista = extras?.getParcelableArrayList<Uri>("lista")

        Log.d(TAG, "lista recibida: $lista")



        lista?.forEach {
            Log.d(TAG, "item: ${it.path}")
            val file = File(it.path)
            listaFiles.add(file)

        }

        Log.d(TAG, "lista archivos: $listaFiles")

        // inicializar el recyclerView
        rvDocs = findViewById(R.id.rvFiles)

        initRecycler()
        setup()



    }

    private fun initRecycler() {
        rvDocs.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        adapter = DocsAdapter(listaFiles, baseContext)
        rvDocs.adapter = adapter
    }

    private fun setup() {
        PDFBoxResourceLoader.init(applicationContext);
        btnAdd = findViewById(R.id.fabAdd)
        btnAdd.setOnClickListener {
            launchFileChooser()
        }
    }

    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        val file: File = File(pathOz.path)
        file?.let {
            listaFiles.add(file)
            // notificar que se agrego un item
            adapter.notifyDataSetChanged()
        }
    }

    private fun launchFileChooser() {
        //funciona para versiones anteriores a android 10
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        // intent.action = Intent.ACTION_GET_CONTENT
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        //val pdfDocument = PDFBoxResourceLoader
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select Your .pdf File"),
                    100
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                    this,
                    "Please Install a File Mana ger",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            data?.data?.also { it ->
                Log.i(TAG, "")
                //set uri to handle
                handlePathOz.getRealPath(it)
                //show Progress Loading
            }
        }



    }

    /*override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        Log.d(TAG, "onRequest")
        Log.d(TAG, "The real path is: ${pathOz.path}")
        val file: File = File(pathOz.path)
        Log.d(TAG, "file $file")
        listaFiles.add(file)

        Log.d(TAG, "lista de archivos $listaFiles")



    }*/
}