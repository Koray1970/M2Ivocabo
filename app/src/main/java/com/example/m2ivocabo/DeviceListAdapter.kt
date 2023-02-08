package com.example.m2ivocabo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.NonDisposableHandle.parent
import org.w3c.dom.Text

class DeviceListAdapter(val context: Context, val devicelist: ArrayList<DeviceItem>) :
    BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return devicelist.size
    }

    override fun getItem(p0: Int): Any {
        return devicelist.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.cardview_devicelist, p2, false)
        var deviceItem=getItem(p0) as DeviceItem
        var txtmacaddress=rowView.findViewById<TextView>(R.id.devicecode)
        var txtdevicename=rowView.findViewById<TextView>(R.id.devicename)
        txtmacaddress.setText(deviceItem.code)
        txtdevicename.setText(deviceItem.name)
        return rowView
    }
}