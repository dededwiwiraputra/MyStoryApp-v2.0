package com.mawumbo.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.liveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.mawumbo.storyapp.data.repository.StoryRepository
import com.mawumbo.storyapp.ui.MainTestDispatcher
import com.mawumbo.storyapp.ui.fake.FakeStory.fakeStories
import com.mawumbo.storyapp.adapter.StoryDiffCallback
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcher = MainTestDispatcher()

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    private val storyRepository = Mockito.mock(StoryRepository::class.java)
    private lateinit var homeViewModel: HomeViewModel

    private val data = fakeStories

    @Before
    fun setUp() {

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `allStory PagingData test success`() = runTest {
        val pagingData = PagingData.from(data)

        Mockito.`when`(storyRepository.getAllStory()).then { liveData { emit(pagingData) } }

        homeViewModel = HomeViewModel(storyRepository)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcher.mainTestDispatcher
        )

        homeViewModel.allStory.getOrAwaitValue().also {
            differ.submitData(it)
        }

        val testResult = differ.snapshot().items

        // Memastikan data tidak null.
        Assert.assertNotNull(testResult)

        // Memastikan jumlah data sesuai dengan yang diharapkan.
        Assert.assertEquals(data.size, testResult.size)

        // Memastikan data pertama yang dikembalikan sesuai.
        Assert.assertEquals(data.first(), testResult.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `allStory PagingData test failed`() = runTest {
        // Mengembalikan data kosong
        val emptyPagingData = PagingData.from(emptyList())

        Mockito.`when`(storyRepository.getAllStory())
            .then { liveData { emit(emptyPagingData) } }

        homeViewModel = HomeViewModel(storyRepository)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcher.mainTestDispatcher
        )

        homeViewModel.allStory.getOrAwaitValue().also {
            differ.submitData(it)
        }

        val testResult = differ.snapshot().items

        // Memastikan jumlah data yang dikembalikan nol.
        Assert.assertEquals(0, testResult.size)
    }

}