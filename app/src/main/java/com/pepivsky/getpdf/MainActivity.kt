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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import br.com.onimur.handlepathoz.utils.extension.getListUri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import java.io.File
import java.io.FileInputStream
import java.lang.Exception


class MainActivity : AppCompatActivity(), HandlePathOzListener.MultipleUri {

    lateinit var btnChooseDoc: Button



    private lateinit var handlePathOz: HandlePathOz

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
            val listUri = data.getListUri()
            handlePathOz.getListRealPath(listUri)

            Log.d(TAG, "lista de uri: $listUri")
            /*val uri = data?.data
            val path = uri?.path
            val fileName = path?.substring(path.lastIndexOf("/") + 1)
            Log.d("MainActivity", "onActivityResult ")*/

           // if (uri != null) {
            //    Log.d(TAG, "uri: $uri ")
            //    Log.d(TAG, "path: $path ")
             //   Log.d(TAG, "fileName: $fileName ")

                // obteniendo el filepath completo

               /* data?.data?.also { it ->
                    //set uri to handle
                    handlePathOz.getRealPath(it)
                    //show Progress Loading
                }*/
        }
    }

    private fun setup() {
        PDFBoxResourceLoader.init(applicationContext)
        handlePathOz = HandlePathOz(this, this)
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

   /* override fun onRequestHandlePathOz(listPathOz: List<PathOz>, tr: Throwable?) {
        //Hide Progress
        //Update the recyclerview with the list
        val list = listPathOz.map { uri -> Uri.parse(uri.path) }



        //Handle any Exception (Optional)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onRequestHandlePathOz(listPathOz: List<PathOz>, tr: Throwable?) {
        Log.d(TAG, "recuperados")
        val list = listPathOz.map { uri -> Uri.parse(uri.path) }
        Log.d(TAG, "lista $list")

        if (list != null && list.isNotEmpty()) {
            navigate(list)
        }
    }

    private fun navigate(list: List<Uri>) {
        Log.d(TAG, "navegando")
        Log.d(TAG, "lista: $list")
        val uriArrayList = ArrayList<Uri>()
        uriArrayList.addAll(list)

        val intent = Intent(this, SecondActivity::class.java)
        //val bundle = bundleOf("lista" to lista)
        //intent.putExtras(bundle)
        intent.putParcelableArrayListExtra("lista", uriArrayList)
        startActivity(intent)
    }

    /*override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        val list = listPathOz.map { uri -> Uri.parse(uri.path) }
        Log.d(TAG, "lista $list")
    }*/

    // uyn item
    /*override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        Toast.makeText(this, "The real path is: ${pathOz.path} \n The type is: ", Toast.LENGTH_SHORT).show()

        val file: File = File(pathOz.path) // error

        val myDocument = PDDocument.load(file)
        val numPages = myDocument.numberOfPages
        Log.d(TAG, "pages: $numPages ")

        Toast.makeText(this, "Este documento tiene $numPages paginas", Toast.LENGTH_SHORT).show()

        //Handle any Exception (Optional)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }*/
}