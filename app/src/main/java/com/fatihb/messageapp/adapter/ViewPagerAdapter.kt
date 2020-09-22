package com.fatihb.messageapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager,
                               behavior: Int
): FragmentPagerAdapter(supportFragmentManager,behavior) {
    private val fragments = ArrayList<Fragment>()
    private val fragmentTitle = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String){
        fragments.add(fragment)
        fragmentTitle.add(title)
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }

}