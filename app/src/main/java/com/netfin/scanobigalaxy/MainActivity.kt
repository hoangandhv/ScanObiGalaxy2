package com.netfin.scanobigalaxy

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_QRCODE: Int = 1
    private val REQUEST_IMAGE_CAPTURE: Int = 2
    private val PERMISSION_REQUEST_CAMERA_CODE: Int = 101
    private var mCurrentPhotoPath: String? = null
    private var Open_Scan_Qrcode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_main)
        tvresult = findViewById<TextView>(R.id.txt_show)
        imresult = findViewById<ImageView>(R.id.imageView)
        if (!Open_Scan_Qrcode) {
            scanQrcode()
            Open_Scan_Qrcode =true
        }
    }
    fun scanQrcode(){
        val intent = Intent(this@MainActivity, ScanActivity::class.java)
        startActivityForResult(intent, REQUEST_QRCODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_QRCODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
                txt_show.text = result
                val timer = object : CountDownTimer(4000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        txt_show.text = ""+(millisUntilFinished/1000)
                    }
                    override fun onFinish() {
                        txt_show.text = "Done!"
                        if (checkPersmission()) {
                            takePicture()
                        }else requestPermission()
                    }
                }
                timer.start()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else  if (requestCode == REQUEST_IMAGE_CAPTURE ) {
            if (resultCode == Activity.RESULT_OK){
                //To get the File for further usage
                val auxFile = File(mCurrentPhotoPath)
                var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
                imageView.setImageBitmap(bitmap)
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    takePicture()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CAMERA_CODE)
    }
    @Throws(IOException::class)
    private fun createFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }


    companion object {
        var tvresult: TextView? = null
        var imresult: ImageView? = null
    }
}
