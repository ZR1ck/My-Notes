package com.example.mynotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.mynotes.databinding.FragmentFindNoteBinding
import com.example.mynotes.viewmodel.NoteViewModel

class FindNote : Fragment() {

    private lateinit var binding: FragmentFindNoteBinding
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentFindNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).noteViewModel

        // Return home
        binding.btnReturn.setOnClickListener {
            it.findNavController().popBackStack()
        }
    }
}