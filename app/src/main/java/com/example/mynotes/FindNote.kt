package com.example.mynotes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotes.adapter.NoteItemAdapter
import com.example.mynotes.adapter.RecentItemAdapter
import com.example.mynotes.databinding.FragmentFindNoteBinding
import com.example.mynotes.model.Note
import com.example.mynotes.viewmodel.NoteViewModel
import kotlinx.coroutines.Job
import java.util.Locale

class FindNote : Fragment() {

    private lateinit var binding: FragmentFindNoteBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteItemAdapter: NoteItemAdapter
    private lateinit var recentItemAdapter: RecentItemAdapter
//    private var searchJob : Job? = null

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

        // Setup recyclerview
        // for searched note
        binding.recyclerViewSearchedNote.layoutManager = GridLayoutManager(context, 3)
        noteItemAdapter = NoteItemAdapter(object : OnItemClickListener{
            override fun onItemClick(view: View, note: Note) {
                val action = FindNoteDirections.actionFindNoteToUpdateNoteFragment(note)
                view.findNavController().navigate(action)
            }

        })
        binding.recyclerViewSearchedNote.adapter = noteItemAdapter
        // for recent search
        binding.recyclerViewRecent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recentItemAdapter = RecentItemAdapter(recentItemClick)
        binding.recyclerViewRecent.adapter = recentItemAdapter


        // Load recent search data
        activity?.let {
            recentItemAdapter.differ.submitList(getSearchHistory(requireContext()))
        }

        // Handle edittext
        binding.edtSearch.text.clear()
        binding.edtSearch.doAfterTextChanged { text ->
            // Handle layout visibility
            text?.let {
                if (text.isEmpty()) {
                    noteItemAdapter.differ.submitList(emptyList()) {
                        binding.recyclerViewSearchedNote.scrollToPosition(0)
                        binding.layoutRecent.visibility = View.VISIBLE
                    }
                }
            }

            // Search on text changed
//            searchJob?.cancel() // Cancel previous job
//            searchJob = lifecycle.coroutineScope.launch {
//                delay(500) // Delay before query
//                text?.let {
//                    if (text.isNotEmpty()) {
//                        binding.layoutRecent.visibility = View.GONE
//                        activity?.let {
//                            noteViewModel.search(text.toString()).observe(viewLifecycleOwner) { dataList ->
//                                customAdapter.differ.submitList(dataList) {
//                                    binding.recyclerViewSearchedNote.scrollToPosition(0)
//                                }
//                            }
//                        }
//                    }
//                    else {
//                        binding.layoutRecent.visibility = View.VISIBLE
//                        customAdapter.differ.submitList(emptyList())
//                    }
//                }
//            }
        }

        // Search when click enter button
        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && binding.edtSearch.text.isNotEmpty()) {
                // Hide recent layout and keyboard
                binding.layoutRecent.visibility = View.GONE
                binding.edtSearch.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                val searchText = binding.edtSearch.text.toString()
                if (searchText.isNotEmpty()) {
                    // Save input value and reload adapter
                    recentItemAdapter.differ.submitList(saveSearchHistory(requireContext(), searchText)) {
                        recentItemAdapter.notifyDataSetChanged()
                        binding.recyclerViewRecent.scrollToPosition(0)
                    }
                    // Get data from database
                    noteViewModel.search(searchText).observe(viewLifecycleOwner) { dataList ->
                            noteItemAdapter.differ.submitList(dataList) {
                                binding.recyclerViewSearchedNote.scrollToPosition(0)
                            }
                        }
                }
                true
            }
            else {
                false
            }
        }

        // Voice input
        binding.btnMic.setOnClickListener {
            startVoiceInput()
        }
    }

    private val recentItemClick = object : RecentItemAdapter.OnItemClickListener {
        override fun onItemClick(view: View, value: String) {
            binding.edtSearch.setText(value)
            // Hide recent layout and keyboard
            binding.layoutRecent.visibility = View.GONE
            binding.edtSearch.clearFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            // Save input value and reload adapter
            recentItemAdapter.differ.submitList(saveSearchHistory(requireContext(), value)) {
                recentItemAdapter.notifyDataSetChanged()
                binding.recyclerViewRecent.scrollToPosition(0)
            }
            // Get data from database
            noteViewModel.search(value).observe(viewLifecycleOwner) { dataList ->
                noteItemAdapter.differ.submitList(dataList) {
                    binding.recyclerViewSearchedNote.scrollToPosition(0)
                }
            }
        }

        override fun onRemoveClick(view: View, value: String) {
            recentItemAdapter.differ.submitList(removeSearchHistory(requireContext(), value)) {
                recentItemAdapter.notifyDataSetChanged()
                binding.recyclerViewRecent.scrollToPosition(0)
            }
        }
    }

    // Voice input
    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        try {
            resultLauncher.launch(intent)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val resultText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!resultText.isNullOrEmpty()) {
                binding.edtSearch.setText(resultText[0])
                binding.edtSearch.requestFocus()
                binding.edtSearch.setSelection(binding.edtSearch.text.length)

                binding.edtSearch.postDelayed({
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)
                }, 300)
            }
        }
    }

    // Save search history
    private fun saveSearchHistory(context: Context, search: String) : List<String> {
        val sharedPreferences = context.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Get old search string
        val history = getSearchHistory(context).toMutableList()
        // Insert new value, remove if existed
        history.remove(search)
        history.add(0, search)

        if (history.size > 10) {
            history.removeLast()
        }
        // Save
        if (history.isNotEmpty()) {
            editor.putString("searchHistory", history.joinToString(separator = ","))
        }
        else {
            editor.remove("searchHistory")
        }
        editor.apply()
        return history
    }

    // Get saved search list history
    private fun getSearchHistory(context: Context) : List<String> {
        val sharedPreferences = context.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)
        val historyString = sharedPreferences.getString("searchHistory", null)
        return historyString?.split(",") ?: emptyList()
    }

    private fun removeSearchHistory(context: Context, value: String) : List<String> {
        val sharedPreferences = context.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Get old search string
        val history = getSearchHistory(context).toMutableList()
        // Remove value
        history.remove(value)
        // Save
        // Save
        if (history.isNotEmpty()) {
            editor.putString("searchHistory", history.joinToString(separator = ","))
        }
        else {
            editor.remove("searchHistory")
        }
        editor.apply()
        return history
    }

    fun clearSearchHistory(context: Context) {
        val sharedPreferences = context.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("searchHistory")
        editor.apply()
    }
}