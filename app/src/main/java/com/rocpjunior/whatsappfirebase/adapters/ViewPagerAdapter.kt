package com.rocpjunior.whatsappfirebase.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rocpjunior.whatsappfirebase.fragments.ContatosFragment
import com.rocpjunior.whatsappfirebase.fragments.ConversasFragment

class ViewPagerAdapter(
    val abas: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(positon: Int): Fragment {
        when(positon){
            1 -> return ContatosFragment()
        }
        return ConversasFragment()
    }

    override fun getItemCount(): Int {
        return abas.size
    }
}