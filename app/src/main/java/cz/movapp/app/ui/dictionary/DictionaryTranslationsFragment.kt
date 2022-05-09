package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionaryAdapter
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.databinding.FragmentDictionaryTranslationsBinding

class DictionaryTranslationsFragment : Fragment() {

    private var _binding: FragmentDictionaryTranslationsBinding? = null
    private lateinit var translationIds: List<String>
    private var favoritesIds = mutableListOf<String>()

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private var adapterDataObservers = mutableListOf<RecyclerView.AdapterDataObserver>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        translationIds = listOf()

        arguments?.let {
            translationIds = it.getStringArray("translation_ids")?.toList() ?: listOf<String>()
        }

        if (translationIds.isNotEmpty())
            dictionarySharedViewModel.setSearchQuery("")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryTranslationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionaryTranslations
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.translations
        (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds
        }

        /**
         * This data observer is used to scroll up in case of data change.
         * It is necessary e.g. in searching. As the user writes, the recyclerview
         * would remain on previously found result but the more accurate result
         * is on top.
         */
        (recyclerView.adapter as DictionaryTranslationsAdapter).registerAdapterDataObserver(
            object: RecyclerView.AdapterDataObserver() {
                init {
                    adapterDataObservers.add(this)
                }

                override fun onChanged() {
                    if (_binding != null)
                        binding.recyclerViewDictionaryTranslations.scrollToPosition(0)
                }
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionaryTranslations.scrollToPosition(0)
                }
                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionaryTranslations.scrollToPosition(0)
                }
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionaryTranslations.scrollToPosition(0)
                }
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionaryTranslations.scrollToPosition(0)
                }
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    if (_binding != null)
                        binding.recyclerViewDictionaryTranslations.scrollToPosition(0)
                }
            }
        )

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { lang ->
            (binding.recyclerViewDictionaryTranslations.adapter as DictionaryTranslationsAdapter).langPair = lang
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (translationIds.isNotEmpty())
            dictionarySharedViewModel.selectedTranslations(translationIds)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (binding.recyclerViewDictionaryTranslations.adapter as DictionaryTranslationsAdapter).submitList(
            listOf()
        )

        for (observer in adapterDataObservers)
            (binding.recyclerViewDictionaryTranslations.adapter as DictionaryTranslationsAdapter).
                unregisterAdapterDataObserver(observer)
        adapterDataObservers.clear()

        binding.recyclerViewDictionaryTranslations.adapter = null
        _binding = null
    }
}