package com.non.abztest.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.non.abztest.model.UserGet
import com.non.abztest.network.ApiService
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource(
    private val api: ApiService
) : PagingSource<Int, UserGet>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserGet> {
        val page = params.key ?: 1
        return try {
            val response = api.getUsers(page = page, count = params.loadSize)

            println(response)
            val nextKey = if (response.users.isEmpty()) {
                null
            } else {
                page + 1
            }

            LoadResult.Page(
                data = response.users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserGet>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}