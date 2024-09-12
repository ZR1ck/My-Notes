package com.example.mynotes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.databinding.FragmentUpdateNoteBinding
import com.example.mynotes.model.Note
import com.example.mynotes.viewmodel.NoteViewModel
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UpdateNoteFragment : Fragment() {

    private lateinit var binding: FragmentUpdateNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private var currentNote: Note? = null
    private var deleted = false
    private lateinit var optionsBtn : MenuItem
    private lateinit var favouriteBtn : MenuItem
    private var isFavourite = false

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
            binding.edtTitle.setText(currentNote!!.title)
            binding.edtNote.setText(currentNote!!.body)
        }
        else { // Setup add note page
            binding.edtNote.requestFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtNote, InputMethodManager.SHOW_IMPLICIT)
        }


        // Return home
        binding.btnReturn.setOnClickListener {
            it.findNavController().popBackStack()
        }

        // Setup toolbar
        val toolbar : MaterialToolbar = binding.toolbar
        (activity as MainActivity).setSupportActionBar(toolbar)

        // Setup menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.action_menu, menu)
                optionsBtn = menu.findItem(R.id.options)
                favouriteBtn = menu.findItem(R.id.favourite)
                if (currentNote == null) {
                    optionsBtn.setVisible(false)
                }
                else if (currentNote!!.favourite) {
                    // Setup favourite note
                    val favourite = menu.findItem(R.id.favourite)
                    isFavourite = currentNote!!.favourite
                    favourite.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.read -> {
                        if (!menuItem.isChecked) {
                            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            binding.edtNote.clearFocus()
                            binding.edtNote.isFocusable = false
                            binding.edtNote.isFocusableInTouchMode = false
                            menuItem.isChecked = true
                            Toast.makeText(context, "Read only: On", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            binding.edtNote.isFocusable = true
                            binding.edtNote.isFocusableInTouchMode = true
                            menuItem.isChecked = false
                            Toast.makeText(context, "Read only: Off", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.search -> {
                        binding.layoutTitle.visibility = View.GONE
                        binding.layoutSearch.visibility = View.VISIBLE
                        binding.edtNote.isFocusable = false
                        binding.edtNote.isFocusableInTouchMode = false
                        binding.edtSearch.requestFocus()
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)
                        true
                    }
                    R.id.delete -> {
                        currentNote?.let { noteViewModel.deleteNote(it) }
                        deleted = true
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        view.findNavController().popBackStack()
                        true
                    }
                    R.id.favourite -> {
                        if (!isFavourite) {
                            menuItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_24)
                            isFavourite = true
                            currentNote?.let { currentNote!!.favourite = true }
                        }
                        else {
                            menuItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
                            isFavourite = false
                            currentNote?.let { currentNote!!.favourite = false }
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Show options button if edittext has value
        binding.edtNote.doAfterTextChanged { text ->
            text?.let {
                if (it.isNotEmpty()) {
                    optionsBtn.isVisible = true
                }
                else if (it.isEmpty() && binding.edtTitle.text.isEmpty()) {
                    optionsBtn.isVisible = false
                    isFavourite = false
                    favouriteBtn.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
                }
            }
        }
        binding.edtTitle.doAfterTextChanged { text ->
            text?.let {
                if (it.isNotEmpty()) {
                    optionsBtn.isVisible = true
                }
                else if (it.isEmpty() && binding.edtNote.text.isEmpty()) {
                    favouriteBtn.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_star_outline_24)
                    optionsBtn.isVisible = false
                    isFavourite = false
                }
            }
        }
        
        // Handle search events
        // Exit button
        binding.btnExit.setOnClickListener {
            binding.layoutTitle.visibility = View.VISIBLE
            binding.layoutSearch.visibility = View.GONE
            binding.edtSearch.text.clear()
            binding.edtNote.isFocusable = true
            binding.edtNote.isFocusableInTouchMode = true
            // Reassign note body
            val content = binding.edtNote.text.toString()
            binding.edtNote.setText(content)
        }
        
        // Search query
        binding.edtSearch.doAfterTextChanged { text ->  
            text?.let {
                val context = binding.edtNote.text.toString()
                if (text.isNotEmpty()) {
                    val query = text.toString()
                    val spannable = SpannableString(context)
                    var startIndex = spannable.indexOf(query)
                    while (startIndex >= 0) {
                        spannable.setSpan(
                            BackgroundColorSpan(Color.YELLOW),
                            startIndex,
                            startIndex + query.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        startIndex = context.indexOf(query, startIndex + query.length)
                    }
                    binding.edtNote.setText(spannable)
                }
                else {
                    val content = binding.edtNote.text.toString()
                    binding.edtNote.setText(content)
                }
            }
        }
    }

    private fun saveNote() {
        var title = binding.edtTitle.text.toString().trim()
        val body = binding.edtNote.text.toString().trim()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        if (title.isNotEmpty() || body.isNotEmpty()) {
            // New note
            title = title.ifEmpty { "New note $currentDate" }

            if (currentNote == null) {
                noteViewModel.addNote(Note(0, title, body, currentDate, currentDate, isFavourite))
                Toast.makeText(context, "Saved new note", Toast.LENGTH_SHORT).show()
            }
            // Update Note
            else {
                noteViewModel.updateNote(Note(currentNote!!.id, title, body, currentNote!!.createDate, currentDate, currentNote!!.favourite))
                Toast.makeText(context, "Updated note", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(context, "Unsaved note", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        if (!deleted) {
            saveNote()
        }
        super.onStop()
    }
}