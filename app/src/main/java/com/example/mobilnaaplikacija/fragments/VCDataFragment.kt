package com.example.mobilnaaplikacija.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobilnaaplikacija.R
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VCDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VCDataFragment : Fragment() {
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

        val bundle: Bundle? = arguments
        val data = bundle!!.getString("vc", "default_value")
            // Now 'data' contains the value sent from the activity
        // Inflate the layout for this fragment

        val jsonObject = JSONObject(data)

        jsonObject.getString("issuer")
        val vcDataList = listOf(
            VCData("Issuer",jsonObject.getString("issuer")),
            VCData("Subject", jsonObject.getString("subject")),
            VCData("Type",jsonObject.getString("type")),
            VCData("IssuanceDate",jsonObject.getString("issuanceDate")),
            VCData("Claims",jsonObject.getString("claims"))
        )

        val arrayAdapter = VCDataArrayAddapter(requireContext(), vcDataList)
        val view = inflater.inflate(R.layout.fragment_v_c_data, container, false)

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
         * @return A new instance of fragment VCDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VCDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


class VCData(val key:String, val value:String)

class VCDataArrayAddapter(context: Context, vcDataList: List<VCData>):
    ArrayAdapter<VCData>(context,0,vcDataList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = convertView ?: LayoutInflater.from(context).inflate(R.layout.vc_data, parent, false)
        val currentContact = getItem(position)

        //  rootView.contactImage.setImageResource(currentContact.imageResource)
        rootView.findViewById<TextView>(R.id.contactName).text = currentContact!!.key
        rootView.findViewById<TextView>(R.id.contactDescription).text = currentContact!!.value

        return rootView
    }
}

