package com.lyni.reader

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.lyni.reader.databinding.ActivityMainBinding
import com.lyni.reader.lib.common.base.ui.BaseActivity
import com.lyni.reader.lib.common.base.viewmodel.BaseViewModel
import com.lyni.reader.lib.common.router.RouterTable

class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>() {

    private lateinit var fragments: Array<Fragment>

    private var currentIndex = 0

    override fun initData() {
        super.initData()
        doInit()
    }

    override fun initView() {
        super.initView()
        hideToolbar()
    }

    override fun initListener() {
        super.initListener()
        binding.bnvMain.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemBookshelf -> change(0)
                R.id.itemDiscover -> change(1)
                R.id.itemMe -> change(2)
            }
            true
        }
    }

    private fun doInit() {
        fragments = arrayOf(
            ARouter.getInstance().build(RouterTable.Fragment.BOOKSHELF_MAIN)
                .navigation() as Fragment,
            ARouter.getInstance().build(RouterTable.Fragment.BOOKSHELF_MAIN)
                .navigation() as Fragment,
            ARouter.getInstance().build(RouterTable.Fragment.BOOKSHELF_MAIN)
                .navigation() as Fragment
        )

        supportFragmentManager.beginTransaction().apply {
            fragments.forEach {
                add(R.id.fragment, it)
                hide(it)
            }
            show(fragments[0])
            commit()
        }
    }

    private fun change(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragments[currentIndex])
        transaction.show(fragments[index])
        currentIndex = index
        transaction.commit()
    }
}