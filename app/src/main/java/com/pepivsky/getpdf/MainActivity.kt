package com.pepivsky.getpdf

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import java.io.File
import java.io.FileInputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    lateinit var btnChooseDoc: Button

    lateinit var root: File
    lateinit var assetManager: AssetManager

    val TAG = "MainActivity"
    //val pdfBox = PDf.init(getApplicationContext());


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChooseDoc = findViewById(R.id.btnChooseDocument)

        // inicializando libreria del pdf
        setup()



        btnChooseDoc.setOnClickListener {
            checkSPermissions()

        }
    }

    private fun checkSPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No hay permiso")
            requestReadStoragePermission()
        } else {
            Log.d(TAG, "Hay permiso")
            launchFileChooser()
        }


    }

    private fun requestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // permiso  rechazado por el usuario
            Log.d(TAG, "Permiso denegado")
        } else {
            // pedir permiso
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
           /* val uri = data?.data
            val path = uri?.path
            val fileName = path?.substring(path.lastIndexOf("/") + 1)
            Log.d("MainActivity", "onActivityResult ")*/

            if (data != null) {
                if (null !=data.clipData) { //multiples archivos
                    Log.d(TAG, "multiples archivos")
                    val lista = data.clipData
                    for (i in 0 until lista!!.itemCount) {
                        val uri = data.clipData!!.getItemAt(i).uri

                        val path = uri?.path
                        val fileName = path?.substring(path.lastIndexOf("/") + 1)
                        Log.d(TAG, "i: $fileName")
                    }
                } else { // un solo archivo
                    Log.d(TAG, "un solo archivo")
                    val uri = data.data
                    Log.d(TAG, "uri $uri")
                }

               /* Log.d(TAG, "uri: $uri ")
                Log.d(TAG, "path: $path ")
                Log.d(TAG, "fileName: $fileName ")

                // obteniendo el filepath completo

                val realPath = uri.getFilePath(applicationContext)

                Log.d(TAG, "realPath: $realPath ")
                //Log.d("MainActivity", "nuevo real path: $truePath ")


                try {
                    val file: File = File(realPath) // error

                    //val newFile = FileInputStream(file)

                    Log.d(TAG, "file creado: $file ")
                    //Log.d("MainActivity", "new file creado: $newFile ")

                    val myDocument = PDDocument.load(file)
                    val numPages = myDocument.numberOfPages
                    Log.d(TAG, "pages: $numPages ")

                    Toast.makeText(this, "Este documento $fileName tiene $numPages paginas", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    println("No se pudo convertir ${e.message}")
                }*/


            }
        }
    }

    private fun setup() {
        PDFBoxResourceLoader.init(applicationContext);

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // comprbado que nuestros permisos estan otorgdos
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchFileChooser()
            } else {
                Log.d(TAG, "Permiso denegado pior primera vez")
            }
        }
    }

    private fun launchFileChooser() {
        //funciona para versiones anteriores a android 10
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        // intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        //val pdfDocument = PDFBoxResourceLoader
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select Your .pdf File"),
                    10
            )

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                    this@MainActivity,
                    "Please Install a File Mana ger",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }
}