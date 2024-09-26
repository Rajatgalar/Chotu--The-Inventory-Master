package com.itechnowizard.chotu.utils

import android.view.View
import com.itechnowizard.chotu.databinding.ToolbarBinding

object ToolbarUtils {

    fun setToolbar(toolbarLayout: ToolbarBinding, isbBackBtn : Boolean,
                   title: String, menuText : String){

        if(isbBackBtn)
            toolbarLayout.toolbarBack.visibility = View.VISIBLE
        if(!title.contentEquals(Constants.TOOLBAR_NO_TITLE))    {
            toolbarLayout.toolbarTitle.text = title
            toolbarLayout.toolbarTitle.visibility = View.VISIBLE
        }
        if(!menuText.contentEquals(Constants.TOOLBAR_NO_MENU_TEXT)){
            toolbarLayout.toolbarMenuText.text = menuText
            toolbarLayout.toolbarMenuText.visibility = View.VISIBLE
        }
    }
}