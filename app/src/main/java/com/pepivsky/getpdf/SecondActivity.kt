package com.pepivsky.getpdf

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pepivsky.getpdf.adapter.DocsAdapter
import com.pepivsky.getpdf.service.ApiService
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class SecondActivity : AppCompatActivity(), HandlePathOzListener.SingleUri{

    companion object {
        fun getRetrofit(): Retrofit {
            val baseURL = "https://backclient.notariapp.online/"
            //http://3.219.19.170:3000/

            //objeto logger
            val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            //HTTP client
            val client = OkHttpClient.Builder().addInterceptor(logger)

            //objeto builder
            val builder = Retrofit.Builder().baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                //agregando el cliente http
                .client(client.build())
            val retrofit = builder.build()
            //Log.i("companion", "Aqui ocurre")

            //objeto retrofit
            return retrofit
        }
    }

    val TAG = "SecondActivity"

    lateinit var rvDocs: RecyclerView
    lateinit var adapter: DocsAdapter

    private lateinit var handlePathOz: HandlePathOz

    lateinit var btnAdd: FloatingActionButton
    lateinit var btnEnviar: Button

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
        btnEnviar = findViewById(R.id.btnEnviar)
        btnEnviar.setOnClickListener {
            uploadFile(listaFiles.first())
        }
        btnAdd.setOnClickListener {
            launchFileChooser()
        }
    }

    private fun uploadFile(file: File) {
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2MjMxOTk4MjMsImV4cCI6MTYyMzIwMzQyM30.5TfvURkaV8mXxSZ2jprIwubnivpF9XtlioolikAQExo"
        //id y archivo
        val requestId = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "253")
        var requestDoc: MultipartBody.Part? = null



        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)

        requestDoc = MultipartBody.Part.createFormData("file", file.name, requestFile)
        Log.d("MainActivity", "$requestDoc")


        // bueno

       /* val requestBody =  RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
        val fileToUpload = MultipartBody.Part.createFormData("files", file.name, requestBody)
        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), "253")*/
        //val uri = Uri.fromFile(file)

        val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val fileToUpload = MultipartBody.Part.createFormData("files", file.name, requestBody)
        val id = "253".toRequestBody("text/plain".toMediaTypeOrNull())


        val retrofit = getRetrofit()
        val call = retrofit.create(ApiService::class.java)

        call.uploadFile(fileToUpload, token, id).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Exito!")
                    Log.d(TAG, "response${response.body()}")
                    Toast.makeText(this@SecondActivity, "Subido ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.d(TAG, "Algo salio mal :( ${call.toString()}")
                Toast.makeText(this@SecondActivity, "Algo falla", Toast.LENGTH_SHORT).show()
            }

        })

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