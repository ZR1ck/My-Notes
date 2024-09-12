package com.example.mynotes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mynotes.adapter.NoteItemAdapter
import com.example.mynotes.databinding.FragmentHomeBinding
import com.example.mynotes.model.Note
import com.example.mynotes.viewmodel.NoteViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteItemAdapter: NoteItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val currentState = sharedPreferences.getInt("state", -1)
        if (currentState != -1) {
            binding.constraintLayout.jumpToState(currentState)
        }

        // Navigation event handler
        binding.btnAdd.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_updateNoteFragment)
        }
        binding.btnSearch.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_findNote)
        }

        // ViewModel
        noteViewModel = (activity as MainActivity).noteViewModel

        // Setup recyclerview
//        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val layoutManager = GridLayoutManager(context, 3)

        binding.recyclerViewHome.layoutManager = layoutManager
//        binding.recyclerViewHome.setHasFixedSize(true)

//        binding.recyclerViewHome.itemAnimator = null
        noteItemAdapter = NoteItemAdapter(object : OnItemClickListener {
            // Item click handler - Navigate
            override fun onItemClick(view: View, note: Note) {
                val action = HomeFragmentDirections.actionHomeFragmentToUpdateNoteFragment(note)
                view.findNavController().navigate(action)
            }
        })
        binding.recyclerViewHome.adapter = noteItemAdapter


        // Setup menu
        val popupMenu = PopupMenu(context, binding.dropdownMenu)
        popupMenu.menuInflater.inflate(R.menu.sort_menu, popupMenu.menu)
        // Handle item click event
        binding.dropdownMenu.setOnClickListener {
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.lastUpdate -> {
                        noteViewModel.type.value = 0
                    }
                    R.id.createDate -> {
                        noteViewModel.type.value = 1
                    }
                    R.id.title -> {
                        noteViewModel.type.value = 2
                    }
                }
                activity?.let {
                    noteViewModel.get(noteViewModel.type.value!!, noteViewModel.asc.value!!).observe(viewLifecycleOwner) { noteList ->
                        noteItemAdapter.differ.submitList(noteList) {
                            binding.recyclerViewHome.scrollToPosition(0)
                        }
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        //let is a scope function that allows you to execute a block of code on the object it is called on.
        //It's useful for avoiding null checks and working with nullable objects in a clean and concise way.
        //It returns the result of the lambda expression.
        activity?.let { // let block WILL ONLY BE executed if ACTIVITY is not null
            // Load data into recyclerview
            noteViewModel.get().observe(viewLifecycleOwner) { noteList ->
                noteItemAdapter.differ.submitList(noteList)
                binding.tvNumNote.text = buildString {
                    append(noteList.size.toString())
                    append(" note")
                }
            }
        }

        // Handle sort events
        // Order button
        binding.btnSort.setOnClickListener {
            noteViewModel.asc.value  = !noteViewModel.asc.value!!
            activity?.let {
                noteViewModel.get(noteViewModel.type.value!!, noteViewModel.asc.value!!).observe(viewLifecycleOwner) { noteList ->
                    noteItemAdapter.differ.submitList(noteList) {
                        binding.recyclerViewHome.scrollToPosition(0)
                    }
                }
            }
        }

        // Observe viewmodel attribute
        noteViewModel.asc.observe(viewLifecycleOwner) { isAsc ->
            val drawableId =
                if (isAsc) R.drawable.baseline_arrow_upward_24 else R.drawable.baseline_arrow_downward_24
            binding.btnSort.setImageResource(drawableId)
        }

        noteViewModel.type.observe(viewLifecycleOwner) { type ->
            binding.tvType.text = popupMenu.menu.getItem(type).title
            popupMenu.menu.getItem(type).isChecked = true
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("state", binding.constraintLayout.currentState)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}