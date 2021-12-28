package `in`.angryvijay.vjuploader

import `in`.angryvijay.vjuploader.databinding.ActivityMainBinding
import `in`.angryvijay.vjuploader.utils.Controller
import `in`.angryvijay.vjuploader.utils.CustomProgressDialog
import `in`.angryvijay.vjuploader.utils.SessionManager
import `in`.angryvijay.vjuploader.viewmodel.LoginViewModel
import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
     val viewModel: LoginViewModel by viewModels()
     var customProgressDialog: CustomProgressDialog? = null
    @Inject
    lateinit var sessionmanager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (sessionmanager.Isloggedin()){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        binding.login.setOnClickListener {

            Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        // permission is granted, open the camera
                        if (binding.username.text.toString().isEmpty() && binding.password.text.toString().isEmpty()){
                            Toast.makeText(applicationContext, "Please Fill the All Fields", Toast.LENGTH_SHORT).show()
                        }else{
                            viewModel.login(binding.username.text.toString(),binding.password.text.toString())
                        }
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied) {
                            // navigate user to app settings
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()

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
                Controller.Status.SUCCESS -> {
                    customProgressDialog?.dismiss()
                    Log.e("TAG", "onCreate: ${it.data.toString()}" )
                    if (it.data?.status.equals("0")){
                        sessionmanager.setlogin(true)
                        sessionmanager.savevalue("user_id",it.data?.msg.toString())
                        startActivity(Intent(this,HomeActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                }
                Controller.Status.ERROR -> {
                    customProgressDialog!!.dismiss()
                    if (it.data?.status.equals("0")){
                       sessionmanager.setlogin(true)
                       sessionmanager.savevalue("user_id",it.data?.msg.toString())
                       startActivity(Intent(this,HomeActivity::class.java))
                       finish()
                   }
                }
            }
        })
    }
}