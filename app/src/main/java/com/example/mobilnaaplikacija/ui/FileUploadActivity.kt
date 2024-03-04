package com.example.mobilnaaplikacija.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.did.DID
import com.example.mobilnaaplikacija.did.DIDParser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.base.MoreObjects.ToStringHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class FileUploadActivity : ComponentActivity() {
    private val PICK_FILE_REQUEST = 10
    private val STORAGE_PERMISSION_CODE = 1

    private lateinit var didText: EditText
    private lateinit var fileNameTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_upload)
        if(!isStoragePermissionGranted()){
            requestStoragePermission()
        }
        didText = findViewById(R.id.editText1);
        fileNameTextView = findViewById(R.id.fileNameView)
        val selectFileButton: Button = findViewById(R.id.select_file_button)
        selectFileButton.setOnClickListener {
            if(checkDID()){
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/octet-stream"))
                startActivityForResult(intent, PICK_FILE_REQUEST)
            }
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                checkDID()
                try{
                    fileNameTextView.text = uri.toString()
                    //println(data.)
//                    val mData = "content://com.android.externalstorage.documents/document/primary%3ADownload%2Ftest.enc"
//                    println("AEAEAE ${uri.toString()}")
//                    Toast.makeText(this, mData, Toast.LENGTH_SHORT).show()
//                    val inputStream = contentResolver.openInputStream(uri)
//                    val file = File(filesDir, "uploaded_file.enc")
//                    val outputStream = FileOutputStream(file)
//                    inputStream?.copyTo(outputStream)
//                    inputStream?.close()
//                    outputStream.close()
                    val contentResolver = contentResolver
                    println("0")
                 //   val documentFile = DocumentFile.fromTreeUri(this, uri)

                   // val sourceFile = File(filesDir, "uploaded_file.txt")
                    println("1")
                    val file = File(filesDir, "privateKeys.enc")
                    println("2")
                    val outputStream = FileOutputStream(file)
                    // Now, you can create or save files in this directory
                    // For example, create a new file:
                  //  val newFile = documentFile?.createFile("text/plain", "example123.txt")
                    println("3")
                    val inputStream = contentResolver.openInputStream(uri)
                    println("4")
                    inputStream!!.copyTo(outputStream!!)
                    saveDID()
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    println(e.message)
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkDID():Boolean{
        try {
            val did: DID = DIDParser(didText.text.toString()).parse()
        }
        catch (e:Exception){
            fileNameTextView.text = "Error ${e.message}"
            return false;
        }
        return true
    }

    private fun saveDID(){
        val fileName = "did.txt"
        val file = File(filesDir, fileName)

// Use FileOutputStream and OutputStreamWriter to write the string to the file
        try {
            FileOutputStream(file).use { fileOutputStream ->
                OutputStreamWriter(fileOutputStream).use { outputStreamWriter ->
                    outputStreamWriter.write(didText.text.toString())
                }
            }
            println("String saved to file: ${file.absolutePath}")
        } catch (e: Exception) {
            // Handle any exceptions that may occur during the file write operation
            println("Error saving string to file: ${e.message}")
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_VIDEO),
            STORAGE_PERMISSION_CODE
        )
    }

    private fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Handle the permission request response
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Yes permission",Toast.LENGTH_SHORT).show()
                // Permission granted, you can now access external storage
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this,"No permission",Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Example usage in your activity

}