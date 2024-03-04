package com.example.mobilnaaplikacija.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.did.DIDParser
import com.example.mobilnaaplikacija.gradientcolor.GradientColor
import com.example.mobilnaaplikacija.ui.VerifiableCredentialPresentationActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VCSelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VCSelectFragment(val button: Button?=null) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private  var linearLayoutContainer:LinearLayout?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("Created")
        val types = arguments?.getStringArrayList("acceptableTypes")
        val dids = arguments?.getStringArrayList("acceptableDIDs")

        val view = inflater.inflate(R.layout.fragment_v_c, container, false)
        // Inflate the layout for this fragment
        linearLayoutContainer = view.findViewById(R.id.linear_layout)
        //  addNewCardView()
        VCSelector.selected = null
        println(types)
        println(dids)
        getAllVCs(types,dids)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VCSelectFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VCSelectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun addNewCardView(file: File, types:ArrayList<String>?,dids:ArrayList<String>?) {
        val fileContent =  file.readText()

        val gson = Gson()

        // Convert JSON string to JsonObject
        val jsonObject: JsonObject = gson.fromJson(fileContent, JsonObject::class.java)

        if(types!=null && types.size>0){
            val a = jsonObject.getAsJsonArray("type").filter {
                types.contains(it.asString)
            }
            if(a==null || a.size==0)return
        }

        if(dids!=null && dids.size>0){
            if(!dids.contains(jsonObject.get("issuer").asString))return
        }

        var indexString = "-1"
        try {
            val styleFile = File(requireContext().filesDir,"vc_"+jsonObject.get("id").asString+".style")
            indexString = styleFile.readText()
        }
        catch (e: Exception){

        }
        //println("asdasd $linearLayoutContainer")
        if(linearLayoutContainer==null)return
        val newCardView = layoutInflater.inflate(R.layout.card_layout, null) as CardView
        val issuer = jsonObject["issuer"]

        newCardView.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.baseline_home_work_24)
        val did = DIDParser(issuer.asString).parse()
        newCardView.findViewById<TextView>(R.id.textView5).text = did.idStrings[1]

        if(jsonObject.get("description")!=null){
            newCardView.findViewById<TextView>(R.id.textView4).text =jsonObject.get("description").asString
        }else{
            newCardView.findViewById<TextView>(R.id.textView4).text = "No title"
        }

        val density = resources.displayMetrics.density
        val pixels = (180 * density).toInt()
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            pixels
        )
        layoutParams.topMargin = (20 * density).toInt()
        layoutParams.marginStart = (20 * density).toInt()
        layoutParams.marginEnd = (20 * density).toInt()
        //  newCardView.radius = 50f

        newCardView.background = GradientColor.getGradient(indexString.toInt())
        newCardView.layoutParams = layoutParams

        val ovo:VCSelectFragment = this
        newCardView.findViewById<CardView>(R.id.card_view4).setOnClickListener(object: View.OnClickListener{
            var isSelected = false
            val fileContent = fileContent
            val prevBg = newCardView.background
            override fun onClick(p0: View?) {
                isSelected = !isSelected

                VCSelector.fileContent = fileContent
                VCSelector.select(p0 as CardView,ovo)

                if (isSelected) {

                    val borderDrawable = GradientDrawable()
                    borderDrawable.shape = GradientDrawable.RECTANGLE
                    borderDrawable.setStroke(10,Color.BLUE)
                    borderDrawable.cornerRadius = 100f

                    val layers = arrayOf(prevBg, borderDrawable)
                    val layerDrawable = LayerDrawable(layers)

                    newCardView.background = layerDrawable

                } else {
                    newCardView.background = prevBg

                }
            }

        })

        // Customize the newCardView if needed
        linearLayoutContainer?.addView(newCardView)
    }

    private fun getAllVCs(types:ArrayList<String>?,dids:ArrayList<String>?){
        Toast.makeText(requireContext(),"alloo", Toast.LENGTH_SHORT).show()
        val allFiles = requireContext().filesDir.listFiles()
        val allVCFiles = allFiles?.filter { it.name.startsWith("vc_") && it.name.endsWith(".json") }?: emptyList()

        for(file in allVCFiles){
            addNewCardView(file,types,dids)
        }

    }


}

object VCSelector{
    var selected: CardView? = null
    var fileContent: String? = null
    fun select(card: CardView,fragment: VCSelectFragment){
        if(card == selected){
            selected = null;
            fileContent = null
            fragment.button?.isEnabled = false;
            return
        }
        selected?.callOnClick()

        selected = card
        fragment.button?.isEnabled = true;
        fragment.button?.visibility = View.VISIBLE
    }

}