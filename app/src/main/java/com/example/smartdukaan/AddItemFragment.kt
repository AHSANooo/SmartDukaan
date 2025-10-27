package com.example.smartdukaan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartdukaan.ui.home.HomeFragment

class AddItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)
        setupClickListeners(view)
        return view
    }

    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(HomeFragment())
        }

        // Save button
        view.findViewById<Button>(R.id.btnSaveItem)?.setOnClickListener {
            saveItem(view)
        }
    }

    private fun saveItem(view: View) {
        val nameUrdu = view.findViewById<EditText>(R.id.etNameUrdu)?.text.toString()
        val nameEnglish = view.findViewById<EditText>(R.id.etName)?.text.toString()

        if (nameUrdu.isEmpty() || nameEnglish.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Item added successfully!", Toast.LENGTH_SHORT).show()
        (activity as? MainActivity)?.replaceFragment(HomeFragment())
    }
}
