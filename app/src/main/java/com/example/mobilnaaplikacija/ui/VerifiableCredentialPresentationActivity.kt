package com.example.mobilnaaplikacija.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.fragments.SettingsFragment
import com.example.mobilnaaplikacija.fragments.VCActivityFragment
import com.example.mobilnaaplikacija.fragments.VCDataFragment
import com.google.android.material.tabs.TabLayout


class VerifiableCredentialPresentationActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var addapter: FragmentPageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.vc_data)
        setContentView(R.layout.activity_verifiable_credential_presentation)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager2)

        addapter = FragmentPageAdapter(supportFragmentManager, lifecycle,intent.getStringExtra("vc")!!)

        tabLayout.addTab(tabLayout.newTab().setText("Info"))
        tabLayout.addTab(tabLayout.newTab().setText("Activity"))

        viewPager2.adapter = addapter

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Toolbar"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


    }

    fun onDeleteIconClick(view: View?) {
        Toast.makeText(this,"Obrisano",Toast.LENGTH_SHORT).show()
        // Handle delete icon click here
        // For example, show a confirmation dialog or perform the delete action
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

class FragmentPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val vcData:String
) : FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
       return 2;
    }

    override fun createFragment(position: Int): Fragment {
      return   if(position == 0){
          val bundle = Bundle()
          bundle.putString("vc", vcData)
          val fragment = VCDataFragment()
          fragment.arguments = bundle
          fragment
        }
        else{
          val bundle = Bundle()
          bundle.putString("vc", vcData)
          val fragment = VCActivityFragment()
          fragment.arguments = bundle
          fragment
        }
    }

}