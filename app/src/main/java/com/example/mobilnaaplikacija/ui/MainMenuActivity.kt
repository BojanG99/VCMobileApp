package com.example.mobilnaaplikacija.ui


import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.databinding.ActivityMenuBinding
import com.example.mobilnaaplikacija.fragments.CustomizeFragment
import com.example.mobilnaaplikacija.fragments.HomeFragment
import com.example.mobilnaaplikacija.fragments.SettingsFragment
import com.example.mobilnaaplikacija.fragments.VCFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainMenuActivity : FragmentActivity() {
    private lateinit var button: FloatingActionButton
    private lateinit var binding: ActivityMenuBinding
    private lateinit var bnv : BottomNavigationView
    private lateinit var homeFragment: HomeFragment
    private lateinit var vcFragment: VCFragment
    private lateinit var customizeFragment: CustomizeFragment
    private lateinit var settingsFragment: SettingsFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        button = findViewById<FloatingActionButton>(R.id.fab)
        button.setOnClickListener{
            Toast.makeText(this, "Scann QR code", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, ResolveQRCodeScanActivity::class.java)
            val intent = Intent(this, QRCodeActivity::class.java)
            startActivity(intent)

            //finish();
        }
        homeFragment = HomeFragment()
        vcFragment = VCFragment()
        customizeFragment = CustomizeFragment()
        settingsFragment = SettingsFragment()
        replaceFragment(HomeFragment())

        bnv = findViewById(R.id.bottomNavigationView)
        bnv.setOnItemSelectedListener {
            println("samo tebi pevamo ${it.itemId}")
            when(it.itemId){
                R.id.home -> replaceFragment(homeFragment)
                R.id.verifiablecerdentials -> replaceFragment(vcFragment)
                R.id.customize -> replaceFragment(CustomizeFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
            }
            true
        }



    }
    private fun replaceFragment(fragment:Fragment){
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}