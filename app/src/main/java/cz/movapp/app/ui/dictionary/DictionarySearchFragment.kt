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
import cz.movapp.app.adapter.DictionarySearchAdapter
import cz.movapp.app.databinding.FragmentDictionarySearchBinding

class DictionarySearchFragment : Fragment() {

    private var _binding: FragmentDictionarySearchBinding? = null
    private var favoritesIds = mutableListOf<String>()

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private var adapterDataObservers = mutableListOf<RecyclerView.AdapterDataObserver>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionarySearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionarySearch
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.searches.value
        (recyclerView.adapter as DictionarySearchAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            (recyclerView.adapter as DictionarySearchAdapter).favoritesIds = favoritesIds
        }

        /**
         * This data observer is used to scroll up in case of data change.
         * It is necessary in searching. As the user writes, the recyclerview
         * would remain on previously found result but the more accurate result
         * is on top.
         */
        (recyclerView.adapter as DictionarySearchAdapter).registerAdapterDataObserver(
            object: RecyclerView.AdapterDataObserver() {
                init {
                    adapterDataObservers.add(this)
                }

                override fun onChanged() {
                    if (_binding != null)
                        binding.recyclerViewDictionarySearch.scrollToPosition(0)
                }
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionarySearch.scrollToPosition(0)
                }
                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionarySearch.scrollToPosition(0)
                }
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionarySearch.scrollToPosition(0)
                }
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionarySearch.scrollToPosition(0)
                }
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    if (_binding != null)
                        binding.recyclerViewDictionarySearch.scrollToPosition(0)
                }
            }
        )

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { lang ->
            (binding.recyclerViewDictionarySearch.adapter as DictionarySearchAdapter).langPair = lang
            (binding.recyclerViewDictionarySearch.adapter as DictionarySearchAdapter).notifyDataSetChanged()
        })

        dictionarySharedViewModel.translationsIds.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                dictionarySharedViewModel.translations.value?.selectTranslations(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (binding.recyclerViewDictionarySearch.adapter as DictionarySearchAdapter).submitList(
            listOf()
        )

        for (observer in adapterDataObservers)
            (binding.recyclerViewDictionarySearch.adapter as DictionarySearchAdapter).
                unregisterAdapterDataObserver(observer)
        adapterDataObservers.clear()

        binding.recyclerViewDictionarySearch.adapter = null
        _binding = null
    }
}