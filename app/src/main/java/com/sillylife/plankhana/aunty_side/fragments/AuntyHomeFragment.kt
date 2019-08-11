package com.sillylife.plankhana.aunty_side.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.views.BaseFragment
import com.sillylife.plankhana.views.adapter.UserListAdapter
import kotlinx.android.synthetic.main.bs_user_list.view.*
import kotlinx.android.synthetic.main.fragment_aunty_home.*

class AuntyHomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = AuntyHomeFragment()
        var TAG = AuntyHomeFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_aunty_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextBtn.setOnClickListener {
            showUserList(CommonUtil.userDummyData())
        }

    }

    private fun showUserList(users: ArrayList<User>) {
        val bottomSheet = BottomSheetDialog(context!!, R.style.BottomSheetDialog)
        val sheetView = layoutInflater.inflate(R.layout.bs_user_list, null)

        val adapter = UserListAdapter(context!!, users) {
            bottomSheet.dismiss()
        }

        val recyclerView = sheetView?.findViewById<RecyclerView>(R.id.rcv)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter
        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
        bottomSheet.setOnDismissListener {

        }

        sheetView.closeBtn.setOnClickListener {
            bottomSheet.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
