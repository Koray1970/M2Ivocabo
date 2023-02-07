package com.example.m2ivocabo

import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MACADDRESS = "macaddress"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddDeviceWithScanResultForm.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddDeviceWithScanResultForm : Fragment() {
    // TODO: Rename and change types of parameters
    private var macaddress: String? = null
    private var param2: String? = null
    private var TAG: String = AddDeviceWithScanResultForm.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            macaddress = it.getString(ARG_MACADDRESS)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_add_device_with_scan_result_form, container)
        var txtmacaddress = view.findViewById<EditText>(R.id.txtmacaddress)
        txtmacaddress.isClickable=false
        txtmacaddress.isEnabled=false
        txtmacaddress?.setText(macaddress)

        var txtdevicename=view.findViewById<EditText>(R.id.txtdevicename)
        var btnsubmit = view.findViewById<Button>(R.id.btnsubmit)
        btnsubmit.setOnClickListener {
            if(txtmacaddress.text.isNullOrEmpty()){
                txtmacaddress.setError("no text")
            }
            if(txtdevicename.text.isNullOrEmpty()){
                var errormessage=R.string.form_pleaseinputdevicename.toString();
                txtdevicename.setError(errormessage)
            }
        }

        return inflater.inflate(
            R.layout.fragment_add_device_with_scan_result_form,
            container,
            false
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddDeviceWithScanResultForm.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddDeviceWithScanResultForm().apply {
                arguments = Bundle().apply {
                    putString(ARG_MACADDRESS, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}