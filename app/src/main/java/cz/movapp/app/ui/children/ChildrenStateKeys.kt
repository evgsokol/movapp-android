package cz.movapp.app.ui.children

import com.google.gson.reflect.TypeToken
import cz.movapp.android.StateStore

class ChildrenStateKeys {
    companion object{
        val SCROLL_POSITIONS =
            StateStore.Key<Int>("Children.scrollPositions", object : TypeToken<Int>() {}.type)
    }
}