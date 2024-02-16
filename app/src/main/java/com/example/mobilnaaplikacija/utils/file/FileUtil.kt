package com.example.mobilnaaplikacija.utils.file

import android.content.Context
import android.widget.Toast
import java.io.File

class FileUtil(){
    fun exists(context: Context, fileName: String): Boolean {
        // Create File object with the file path
        val file = File(context.filesDir.absolutePath + "/" + fileName)
        // Check if file exists
        if (file.exists()) {
            Toast.makeText(context,"The file '$fileName' exists.",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context,"The file '$fileName' doesn't exists.",Toast.LENGTH_SHORT).show()
        }

        return file.exists();
    }
}