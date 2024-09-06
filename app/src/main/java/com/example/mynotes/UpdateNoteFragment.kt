package com.example.mynotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.databinding.FragmentUpdateNoteBinding
import com.example.mynotes.model.Note
import com.example.mynotes.viewmodel.NoteViewModel


class UpdateNoteFragment : Fragment() {

    private lateinit var binding: FragmentUpdateNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private var currentNote: Note? = null

    private val args : UpdateNoteFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).noteViewModel

        currentNote = args.note

        if (currentNote != null) { // Setup update note page
            binding.tvTitle.visibility = View.GONE
            binding.edtTitle.visibility = View.VISIBLE
            binding.layoutOptions.visibility = View.GONE
            binding.edtNote.requestFocus()
        }
        else { // Setup add note page

        }


        // Return home
        binding.btnReturn.setOnClickListener {
            it.findNavController().popBackStack()
        }
    }
}