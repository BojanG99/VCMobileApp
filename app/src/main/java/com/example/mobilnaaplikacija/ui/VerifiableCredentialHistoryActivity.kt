package com.example.mobilnaaplikacija.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.mobilnaaplikacija.R
import java.io.File

class VerifiableCredentialHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifiable_credential_history)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Toolbar"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val date = intent.getStringExtra("date")?:"no time"
        val verifier = intent.getStringExtra("verifier")?:"no verifier"
        val purpose = intent.getStringExtra("purpose")?:"no purpose"
        val status = intent.getStringExtra("status")?:"no status"


        val list = mutableListOf<KeyValue>()


        if(status=="created"){
            list.add(KeyValue("date of issuing",date!!))
            list.add(KeyValue("creator",verifier!!))
        }
        else{
            list.add(KeyValue("date of presentation",date!!))
            list.add(KeyValue("verifier",verifier.split(":")[3]))
            list.add(KeyValue("purpose",purpose!!))
            list.add(KeyValue("Status",status!!))
        }


        findViewById<ListView>(R.id.listView).adapter = KeyValueArrayAddapter(this,list)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

class KeyValue(val key:String, val value:String)

class KeyValueArrayAddapter(context: Context, vcDataList: List<KeyValue>):
    ArrayAdapter<KeyValue>(context,0,vcDataList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = convertView ?: LayoutInflater.from(context).inflate(R.layout.vc_data, parent, false)
        val currentContact = getItem(position)

        //  rootView.contactImage.setImageResource(currentContact.imageResource)
        rootView.findViewById<TextView>(R.id.contactName).text = currentContact!!.key
        rootView.findViewById<TextView>(R.id.contactDescription).text = currentContact!!.value

        return rootView
    }
}
