package com.netfin.scanobigalaxy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.io.File
import java.io.IOException
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import java.util.*

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    val REQUEST_IMAGE_CAPTURE = 1
    private var mScannerView: ZXingScannerView? = null
    private var mCurrentPhotoPath: String? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

    }
    override fun handleResult(p0: Result?) {
        MainActivity.tvresult?.text = p0?.text
        takePicture()
        //onBackPressed()
    }
    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }
    private fun takePicture() {

        //val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.netfin.scanobigalaxy",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            //To get the File for further usage
            val auxFile = File(mCurrentPhotoPath)


            var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            MainActivity.imresult?.setImageBitmap(bitmap)

        }
    }
    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
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

}
