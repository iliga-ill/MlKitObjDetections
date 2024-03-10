package com.example.ejisho.pages

import com.example.mlkit_detection.AppSettings
import com.example.mlkit_detection.pages.pages.Page_FaceRecognition
import com.example.mlkit_objdetections.MainActivity

//список всех страниц
private var openedPagesStack: ArrayList<Page> = arrayListOf()

class PageController(
    var appSettings: AppSettings,
){
    private lateinit var main: MainActivity

    private lateinit var lastActionPage: Page
    private var lastActionIndex: Int = 0

    //actions
    private var onCloseAction: () -> Unit = {}
    private var onCloseCancelAction: () -> Unit = {}

    fun connect(main: MainActivity){
        this.main = main

        //первое открытие вкладки при создании контроллера
        if (getOpenedPagesStack().size == 0) openNewPageLayout(main, Page_FaceRecognition(appSettings.page_face_recognition_id, appSettings, this))
        else openLastPageLayout(main)

    }

    private fun openLayout(main: MainActivity, page: Page){
//        val pageBody = page.openLayout(main)
//        println("Controller_Page finished creating page id: ${page.pageId} at ${main.sec}")
//        addViewInDynamicBody(main, pageBody)

//        println(page.pageId)

        main.getDynamicBody().removeAllViews()
        page.appSettings = appSettings
        page.pageController = this
        page.openLayout(main)
        //page.openLayout(main)
        addToPageArray(page)
    }

//    fun openPagesInViewPager(main: MainActivity, pagesArr: ArrayList<PageInterface>, currentPageIndex: Int){
//        val viewPagerFragment = main.layoutInflater.inflate(R.layout.fragment_viewpager_container, null)
//        val viewpagerContainer = viewPagerFragment.findViewById<ViewPager>(R.id.viewpager_container)
//        val adapter: PagerAdapter = ViewPagerAdapter(main, viewpagerContainer, pagesArr)
//        viewpagerContainer.adapter = adapter
//        viewpagerContainer.currentItem = currentPageIndex
//        addViewInDynamicBody(main, viewPagerFragment)
//    }

    fun openNewPageLayout(main: MainActivity, page: Page, confirmPageClosing: Boolean = false) {
        if (openedPagesStack.size == 0 || getLastItemFromPagesStack().pageId != page.pageId) {
            if (openedPagesStack.size == 0 || getLastItemFromPagesStack().onPageClose(main, confirmPageClosing)) {
                openLayout(main, page)
            } else {
                lastActionIndex = 0
                lastActionPage = page
            }
        }
    }

    fun openLastPageLayout(main: MainActivity) {
        val lastItem = getLastItemFromPagesStack()
        removeLastItemFromPageArray()
        openLayout(main, lastItem)
    }

    fun openPreviousPageLayout(main: MainActivity, confirmPageClosing: Boolean = false) {
        if (getOpenedPagesStackSize()>1) {
            val lastItem = getLastItemFromPagesStack()
            if (lastItem.onPageClose(main, confirmPageClosing)) {
                removeLastItemFromPageArray()
                val previousItem = getLastItemFromPagesStack()
                removeLastItemFromPageArray()
                openLayout(main, previousItem)
            } else lastActionIndex = 2
        } else {
            main.finishAffinity()
        }
    }

    fun closePageWithAction(onCloseAction: () -> Unit, onCloseCancelAction: () -> Unit, confirmPageClosing: Boolean = false) {
        if (getLastItemFromPagesStack().onPageClose(main, confirmPageClosing)) {
            onCloseAction.invoke()
        } else {
            this.onCloseAction = onCloseAction
            this.onCloseCancelAction = onCloseCancelAction
            lastActionIndex = 3
        }
    }

    //отменяет действие при закрытии страницы (например при переключении темы на несохраненной страницы если пользователь нажимает отмена action откатывает обратно ползунок темы приложения)
    fun onCloseCancelAction(){
        onCloseCancelAction.invoke()
        onCloseCancelAction = {}
    }

    fun repeatLastAction(confirmed: Boolean){
        when (lastActionIndex) {
            0 -> openNewPageLayout(main, lastActionPage, confirmed)
            1 -> openLastPageLayout(main)
            2 -> openPreviousPageLayout(main, confirmed)
            3 -> {onCloseAction.invoke(); onCloseAction = {} }
        }
    }

    // функции работы с PagesStack
    fun getOpenedPagesStack(): ArrayList<Page>{ return openedPagesStack }
    fun getOpenedPagesStackSize(): Int { return openedPagesStack.size }
    fun getLastItemFromPagesStack(): Page { return openedPagesStack[openedPagesStack.lastIndex]}
    fun addToPageArray(pageInstance: Page){ openedPagesStack.add(pageInstance) }
    fun clearPageArray(){ openedPagesStack = arrayListOf() }
    fun removeLastItemFromPageArray(){ openedPagesStack.removeLast() }
}