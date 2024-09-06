package com.example.mynotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.databinding.FragmentHomeBinding
import com.example.mynotes.model.Note
import com.example.mynotes.viewmodel.NoteViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var customAdapter: CustomAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        binding.btnAdd.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_updateNoteFragment)
        }
        binding.btnSearch.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_findNote)
        }

        // ViewModel
        noteViewModel = (activity as MainActivity).noteViewModel

        // Setup recyclerview
        binding.recyclerViewHome.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewHome.setHasFixedSize(true)

        val data : List<Note> = listOf()
        customAdapter = CustomAdapter(data)

        //let is a scope function that allows you to execute a block of code on the object it is called on.
        //It's useful for avoiding null checks and working with nullable objects in a clean and concise way.
        //It returns the result of the lambda expression.
        activity?.let { // let block will only be executed if "activity" is not null
            noteViewModel.getAllNotes().observe(viewLifecycleOwner) { noteList ->
                customAdapter = CustomAdapter(noteList)
            }
        }

        binding.recyclerViewHome.adapter = customAdapter
    }
}