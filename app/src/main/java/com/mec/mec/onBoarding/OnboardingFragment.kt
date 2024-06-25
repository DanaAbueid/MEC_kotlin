package com.mec.mec.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mec.mec.R
import com.mec.mec.generic.BaseFragment

class OnboardingFragment : BaseFragment() {
    override fun isLoggedin(): Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val images = listOf(
            R.drawable.pic2,
            R.drawable.pic6,
            R.drawable.pic4
        )

        val headlines = listOf(
            "Welcome to Our App",
            "Discover New Features",
            "Get Started Now"
        )

        val descriptions = listOf(
            "This is the first description.",
            "This is the second description.",
            "This is the third description."
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val adapter = OnboardingAdapter(images, headlines, descriptions)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == images.size - 1) {
                    view.findViewById<Button>(R.id.btnGetStarted).apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            // Navigate to the login screen
                            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
                        }
                    }
                } else {
                    view.findViewById<Button>(R.id.btnGetStarted).visibility = View.GONE
                }
            }
        })
    }
}
