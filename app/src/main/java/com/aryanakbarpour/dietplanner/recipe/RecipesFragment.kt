package com.aryanakbarpour.dietplanner.recipe

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.aryanakbarpour.dietplanner.R
import com.aryanakbarpour.dietplanner.databinding.FragmentRecipesBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tabs Customization
        binding.tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
        binding.tabLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.design_default_color_primary))
        binding.tabLayout.tabTextColors = ContextCompat.getColorStateList(this.requireContext(), android.R.color.white)

        // Set different Text Color for Tabs for when are selected or not
        //tab_layout.setTabTextColors(R.color.normalTabTextColor, R.color.selectedTabTextColor)

        // Number Of Tabs
        val numberOfTabs = 2

        // Set Tabs in the center
        //tab_layout.tabGravity = TabLayout.GRAVITY_CENTER

        // Show all Tabs in screen
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED

        // Scroll to see all Tabs
        //tab_layout.tabMode = TabLayout.MODE_SCROLLABLE

        // Set Tab icons next to the text, instead above the text
        binding.tabLayout.isInlineLabel = true

        // Set the ViewPager Adapter
        val adapter = TabsPagerAdapter(childFragmentManager, lifecycle, numberOfTabs)
        binding.tabsViewpager.adapter = adapter

        // Enable Swipe
        binding.tabsViewpager.isUserInputEnabled = true

        TabLayoutMediator(binding.tabLayout, binding.tabsViewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Discover"
                    tab.setIcon(R.drawable.ic_baseline_manage_search_24)
                }
                1 -> {
                    tab.text = "Saved"
                    tab.setIcon(R.drawable.ic_baseline_save_24)

                }
            }
            // Change color of the icons
            tab.icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    Color.WHITE,
                    BlendModeCompat.SRC_ATOP
                )
        }.attach()

        // setCustomTabTitles()

    }

    private fun setCustomTabTitles() {
        val vg = binding.tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount

        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup

            val tabChildCount = vgTab.childCount

            for (i in 0 until tabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {

                    // Change Font and Size
                    tabViewChild.typeface = Typeface.DEFAULT_BOLD
//                    val font = ResourcesCompat.getFont(this, R.font.myFont)
//                    tabViewChild.typeface = font
//                    tabViewChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
                }
            }
        }
    }

}