package `in`.angryvijay.vjuploader

import `in`.angryvijay.vjuploader.databinding.ActivityHomeBinding
import `in`.angryvijay.vjuploader.utils.Controller
import `in`.angryvijay.vjuploader.utils.CustomProgressDialog
import `in`.angryvijay.vjuploader.utils.FileMyutils
import `in`.angryvijay.vjuploader.utils.SessionManager
import `in`.angryvijay.vjuploader.viewmodel.UploadViewModel
import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

import android.graphics.Bitmap

import android.media.MediaMetadataRetriever
import android.view.View
import android.widget.AdapterView
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import kotlin.collections.ArrayList


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding : ActivityHomeBinding
    lateinit var byteArray: ByteArray
    var addressfile: File? = null
    var videofile: File? = null
    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var dateTime: String
    lateinit  var calendar: Calendar
    lateinit var date: SimpleDateFormat
    lateinit var time: SimpleDateFormat
    var catlist = ArrayList<String>()
    var catidlist = ArrayList<String>()
    var selectcat = ""
    var customProgressDialog: CustomProgressDialog? = null

    val viewModel: UploadViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = ArrayAdapter<String>(applicationContext, R.layout.simple_list_item_1,catlist)

        binding.spinner.adapter = adapter

        viewModel.getcat()


        viewModel.catmutablelivedata.observe(this,{
            when(it.status){
                Controller.Status.LOADING -> {
                    customProgressDialog = CustomProgressDialog.show(
                        this,
                        indeterminate = true,
                        cancelable = false
                    )
                }
                Controller.Status.ERROR -> {
                    customProgressDialog!!.dismiss()

                    Log.e("TAG", "onCreateViewerror: ${it.message}" )
                }
                Controller.Status.SUCCESS -> {
                   customProgressDialog?.dismiss()
                    catlist.clear()
                    catidlist.clear()
                    catlist.add("Select Category")
                    catidlist.add("-1")

                    if (it.data?.status.equals("0")){
                        it.data?.list?.forEach { it ->
                            it.name?.let { it1 -> catlist.add(it1) }
                            it.id?.let { it1 -> catidlist.add(it1) }

                        }

                    }
                    adapter.notifyDataSetChanged()

                }
            }
        })


        binding.spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectcat = catidlist[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.uploadvideo.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_PICK
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            videolauncher.launch(Intent.createChooser(intent, "Select Video"))
        }



        binding.uploadserver.setOnClickListener {

            if (binding.spinner.selectedItem.toString().equals("Select Category")){
                Toast.makeText(applicationContext, "Please Choose Category", Toast.LENGTH_SHORT).show()
            }
            else if (binding.songname.text.toString().isEmpty() && binding.moviename.text.toString().isEmpty()){
                Toast.makeText(this, "Please Enter Song and Movie name", Toast.LENGTH_SHORT).show()
            } else
                if (videofile == null){
                    Toast.makeText(this, "Please Upload  Video", Toast.LENGTH_SHORT).show()
                }else {

                    val user_id =
                        sessionManager.getvalue("user_id")
                            ?.let { it1 -> RequestBody.create("text/plain".toMediaTypeOrNull(), it1) }
                    val songname =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), binding.songname.text.toString())
                    val moviename = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.moviename.text.toString())
                    val category = RequestBody.create("text/plain".toMediaTypeOrNull(), selectcat)

                    val requestBody1 = RequestBody.create("/*".toMediaTypeOrNull(), byteArray)
                    val fileToUpload1 =
                        requestBody1.let { it1 ->
                            MultipartBody.Part.createFormData("imgname", "anything",
                                it1
                            )
                        }


                    val requestBody2 =
                        videofile?.let { it1 -> RequestBody.create("/*".toMediaTypeOrNull(), it1) }
                    val fileToUpload2 =
                        requestBody2?.let { it1 ->
                            MultipartBody.Part.createFormData("vidname", videofile?.name,
                                it1
                            )
                        }
                    if (fileToUpload1 != null) {
                        if (fileToUpload2 != null) {
                            viewModel.upload(user_id!!,songname, moviename,category, fileToUpload1, fileToUpload2)
                        }
                    }
                }
        }

        viewModel.uploadmutablelivedata.observe(this,{
            when(it.status){
                Controller.Status.LOADING -> {
                    customProgressDialog = CustomProgressDialog.show(
                        this,
                        indeterminate = true,
                        cancelable = false
                    )
                }
                Controller.Status.ERROR -> {
                    customProgressDialog!!.dismiss()

                    Log.e("TAG", "onCreateViewerror: ${it.message}" )
                }
                Controller.Status.SUCCESS -> {
                    customProgressDialog!!.dismiss()
                    binding.videotext.setText("")
                    videofile = null
                    binding.imageview.setImageDrawable(null)
                    binding.moviename.setText("")
                    binding.songname.setText("")
                    if (it.data?.status.equals("0")){
                        Toast.makeText(applicationContext, "Video Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("TAG", "onCreateView: ${it.data.toString()}" )
                }
            }
        })

    }
    private val singleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    val path = getPathFromURI(selectedImageUri)
                    addressfile = File(
                        FileMyutils.getPath(
                            applicationContext, selectedImageUri
                        )
                    )

                }
            }
        }

    private val videolauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    val path = getPathFromURI(selectedImageUri)
                    videofile = File(
                        FileMyutils.getPath(
                            applicationContext, selectedImageUri
                        )
                    )
//                    val mmr = FFmpegMediaMetadataRetriever()
//                    mmr.setDataSource(videofile.toString())
//                    mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM)
//                    mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST)
//                    val b: Bitmap = mmr.getFrameAtTime(
//                        2000000,
//                        FFmpegMediaMetadataRetriever.OPTION_CLOSEST
//                    ) // frame at 2 seconds
//
//
//                    mmr.release()
                        //  binding.imageview.setImageBitmap(b)
                    val retriever = MediaMetadataRetriever()
                    try {


                        retriever.setDataSource(applicationContext,selectedImageUri)
                       val bitmap = retriever.getFrameAtTime(-1, MediaMetadataRetriever.OPTION_CLOSEST)
                        Log.d(
                            "bitmappp",
                            " " + bitmap?.getWidth().toString() + " " + bitmap?.getHeight()
                        )
                        val stream = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                         byteArray = stream.toByteArray()
                        binding.imageview.setImageBitmap(bitmap)
                    } catch (ex: IllegalArgumentException) {
// Assume this is a corrupt video file
                        Toast.makeText(applicationContext, "This videos format is not Supported", Toast.LENGTH_SHORT).show()
                        Log.d("IllegalArgumentException", "" + ex.message)
                    } catch (ex: RuntimeException) {
// Assume this is a corrupt video file.
                        Toast.makeText(applicationContext, "This videos format is not Supported", Toast.LENGTH_SHORT).show()

                        Log.d("RuntimeException", "" + ex.message)
                    } finally {
                        try {
                            retriever.release()
                        } catch (ex: RuntimeException) {
// Ignore failures while cleaning up.
                            Toast.makeText(applicationContext, "This videos format is not Supported", Toast.LENGTH_SHORT).show()

                            Log.d("RuntimeException", "" + ex.message)
                        }
                    }

                }
            }
        }

    private fun getPathFromURI(uri: Uri?): String {

        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver!!.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }
}