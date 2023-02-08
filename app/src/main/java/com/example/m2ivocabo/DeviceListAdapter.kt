package com.example.m2ivocabo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.coroutines.NonDisposableHandle.parent

class DeviceListAdapter(val context: Context, val devicelist: ArrayList<DeviceItem>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return devicelist.size
    }

    override fun getItem(p0: Int): Any {
        return devicelist.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.cardview_devicelist, parent, false)
        return rowView
    }
}