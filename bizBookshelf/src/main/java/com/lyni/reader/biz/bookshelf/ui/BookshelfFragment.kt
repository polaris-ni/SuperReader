package com.lyni.reader.biz.bookshelf.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.lyni.reader.biz.bookshelf.databinding.BsFragmentMainBinding
import com.lyni.reader.lib.common.base.ui.BaseFragment
import com.lyni.reader.lib.common.base.viewmodel.BaseViewModel
import com.lyni.reader.lib.common.router.RouterTable

/**
 * @date 2022/5/24
 * @author Liangyong Ni
 * description BookshelfFragment
 */
@Route(path = RouterTable.Fragment.BOOKSHELF_MAIN)
class BookshelfFragment : BaseFragment<BsFragmentMainBinding, BaseViewModel>() {
}