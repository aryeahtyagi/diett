package com.aryanakbarpour.dietplanner.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Discover Recipes")
                val fragment = RecipeDiscoverFragment()
                fragment.arguments = bundle
                return fragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putString("fragmentName", "Saved Recipes")
                val recipesListFragment = RecipesListFragment()
                recipesListFragment.arguments = bundle
                return recipesListFragment
            }

            else -> return Fragment()
        }

    }

}