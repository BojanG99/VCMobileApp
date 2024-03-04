package com.example.mobilnaaplikacija.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.mobilnaaplikacija.R
import com.example.mobilnaaplikacija.ui.VerifiableCredentialHistoryActivity
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VCActivityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VCActivityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment

        val bundle: Bundle? = arguments
        val data = bundle!!.getString("vc", "default_value")
        // Now 'data' contains the value sent from the activity
        // Inflate the layout for this fragment
        val jsonObject = JSONObject(data)

        jsonObject.getString("issuer").split(":")[3]

        val vcDataList = listOf(
            VCActivity("23-2-2024 20:47","did:ethhipfs:0x1234:EXAMPLE_PRESENTATION", "To test look of presentaion succes", "success"),
            VCActivity("23-2-2024 20:48","did:ethhipfs:0x1234:EXAMPLE_PRESENTATION", "To test look of presentaion fail", "fail"),
            VCActivity(jsonObject.getString("issuanceDate"),jsonObject.getString("issuer"), "", "created")
        )

        val arrayAdapter = VCActivityArrayAddapter(requireContext(), vcDataList)

        val view = inflater.inflate(R.layout.fragment_v_c_activity, container, false)
        view.findViewById<ListView>(R.id.listView).adapter = arrayAdapter
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VCActivityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VCActivityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class VCActivity(val date:String, val verifier:String, val purpose: String, val status:String)

class VCActivityArrayAddapter(context: Context, vcDataList: List<VCActivity>):
    ArrayAdapter<VCActivity>(context,0,vcDataList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = convertView ?: LayoutInflater.from(context).inflate(R.layout.vc_activity_layout, parent, false)
        val currentContact = getItem(position)
//
        val parts = currentContact!!.verifier.split(":")
        val issuer = parts[parts.size - 1]
        //rootView.contactImage.setImageResource(currentContact.imageResource)
        rootView.findViewById<TextView>(R.id.date).text = currentContact!!.date
        rootView.findViewById<TextView>(R.id.issuer).text = issuer
        rootView.findViewById<TextView>(R.id.status).text = currentContact.status
        rootView.setOnClickListener{
            val intent = Intent(context, VerifiableCredentialHistoryActivity::class.java)
            intent.putExtra("date",currentContact.date)
            intent.putExtra("verifier",currentContact.verifier)
            intent.putExtra("purpose",currentContact.purpose)
            intent.putExtra("status",currentContact.status)
            context.startActivity(intent)
        }

        val color = when (currentContact.status){
            "success"-> Color.parseColor("#00ff00")
            "fail" -> Color.parseColor("#ff0000")
            "created" -> Color.parseColor("#0000ff")
            else -> Color.YELLOW
        }
        val rid = when(currentContact.status){
            "created" -> R.drawable.baseline_download_24
            else -> R.drawable.baseline_upload_24
        }

        rootView.findViewById<ImageView>(R.id.logoImage).setImageResource(rid)
        rootView.findViewById<ImageView>(R.id.logoImage).setColorFilter(Color.parseColor("#2D0244"))

        rootView.findViewById<ImageView>(R.id.infoImage).setColorFilter(color)
        return rootView
    }
}
