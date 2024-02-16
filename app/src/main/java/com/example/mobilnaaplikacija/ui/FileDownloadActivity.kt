package com.example.mobilnaaplikacija.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.example.mobilnaaplikacija.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileDownloadActivity: ComponentActivity() {
    private val PICK_FOLDER_REQUEST = 2

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        println("TTT ${uri.toString()}")
        Toast.makeText(this,"TTT ${uri.toString()}",Toast.LENGTH_SHORT).show()
        if (uri != null) {
            val destinationFile = File(uri.path!!) // Get the selected folder path
            val sourceFile = File(filesDir, "uploaded_file.txt") // Replace with the name of your file

            val inputStream = FileInputStream(sourceFile)
            val outputStream = FileOutputStream(File(destinationFile, "uploaded_file.txt")) // Create the file in the selected folder

            Toast.makeText(this,inputStream.toString(),Toast.LENGTH_SHORT).show()
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_download)
        println(filesDir.absolutePath.toString())
        val selectFolderButton: Button = findViewById(R.id.select_folder_button)
        selectFolderButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, PICK_FOLDER_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FOLDER_REQUEST && resultCode == Activity.RESULT_OK) {

            data?.data?.let { uri ->
                // The user selected a directory, and you have access to it through the URI
                // You can save files in this directory using ContentResolver or DocumentFile
                // Example:
                val contentResolver = contentResolver
                val documentFile = DocumentFile.fromTreeUri(this, uri)

                val sourceFile = File(filesDir, "uploaded_file.txt")
                // Now, you can create or save files in this directory
                // For example, create a new file:
                val newFile = documentFile?.createFile("text/plain", "example123.txt")
                val outputStream = contentResolver.openOutputStream(newFile!!.uri)
                val inputStream = FileInputStream(sourceFile)
                inputStream.copyTo(outputStream!!)
            // Write content to the file using the outputStream
            }

        //            println(data.toString())
//            Toast.makeText(this,data.toString(),Toast.LENGTH_SHORT).show()
//          //  getContent.launch(data?.data.toString()) // Launch the content selection for the chosen folder
//            val uri = data?.data
//            println("TTT ${uri.toString()}")
//            Toast.makeText(this,"TTT ${uri.toString()}",Toast.LENGTH_SHORT).show()
//            if (uri != null) {
//                try {
//
//
//                    val destinationFile = File(uri.path!!) // Get the selected folder path
//                    println("0 ${uri.path!!}")
//                    val exportFile = File(
//                        uri.toString()+"/"+
//                        "test.txt"
//                    )
//                    if (!exportFile.exists()) {
//                        println("4")
//                        exportFile.createNewFile()
//                    }else{
//                        "postoji"
//                    }
//                    println("1")
//                    val sourceFile =
//                        File(filesDir, "uploaded_file.txt") // Replace with the name of your file
//
//                    val inputStream = FileInputStream(sourceFile)
//                    println("2")
//                    val outputStream = FileOutputStream(
//                        File(
//                            destinationFile,
//                            "uploaded123_file.txt"
//                        )
//                    ) // Create the file in the selected folder
//                    println("3")
//                    Toast.makeText(this, inputStream.toString(), Toast.LENGTH_SHORT).show()
//                    inputStream.copyTo(outputStream)
//                    inputStream.close()
//                    outputStream.close()
//                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
//                }
//                catch (e:Exception){
//                    println(e.message)
//                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

}