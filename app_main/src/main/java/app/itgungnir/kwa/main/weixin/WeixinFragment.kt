package app.itgungnir.kwa.main.weixin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import app.itgungnir.kwa.common.hideSoftInput
import app.itgungnir.kwa.common.html
import app.itgungnir.kwa.common.popToast
import app.itgungnir.kwa.main.R
import app.itgungnir.kwa.main.weixin.child.WeixinChildFragment
import kotlinx.android.synthetic.main.fragment_weixin.*
import my.itgungnir.rxmvvm.core.mvvm.buildFragmentViewModel

class WeixinFragment : Fragment() {

    private val viewModel by lazy {
        buildFragmentViewModel(
            fragment = this,
            viewModelClass = WeixinViewModel::class.java
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_weixin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponent()
        observeVM()
    }

    private fun initComponent() {

        headBar.title("公众号")

        tabLayout.setupWithViewPager(viewPager)

        searchBar.doOnSearch {
            searchBar.hideSoftInput()
            viewModel.setState {
                copy(
                    k = it
                )
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                searchBar.getInput().setText("")
                searchBar.hideSoftInput()
                viewModel.setState {
                    copy(
                        currTab = tabs[position],
                        k = ""
                    )
                }
            }
        })

        // Init Data
        viewModel.getWeixinTabs()
    }

    private fun observeVM() {

        viewModel.pick(WeixinState::tabs)
            .observe(this, Observer { tabs ->
                tabs?.a?.let {
                    viewPager.adapter = object :
                        FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                    override fun getItem(position: Int): Fragment =
                        WeixinChildFragment.newInstance(it[position].id)

                    override fun getCount(): Int =
                        it.size

                    override fun getPageTitle(position: Int) =
                        html(it[position].name)
                }
                }
            })

        viewModel.pick(WeixinState::currTab)
            .observe(this, Observer { currTab ->
                currTab?.a?.let {
                    searchBar.getInput().hint = "在${it.name}的公众号中搜索"
                }
            })

        viewModel.pick(WeixinState::error)
            .observe(this, Observer { error ->
                error?.a?.message?.let {
                    popToast(it)
                }
            })
    }
}