package io.android.projectx.data

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.android.projectx.data.mapper.RecipeMapper
import io.android.projectx.data.model.RecipeEntity
import io.android.projectx.data.repository.RecipesCache
import io.android.projectx.data.repository.RecipesDataStore
import io.android.projectx.data.store.RecipesDataStoreFactory
import io.android.projectx.data.test.factory.DataFactory
import io.android.projectx.data.test.factory.RecipeFactory
import io.android.projectx.domain.model.Recipe
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class RecipesDataRepositoryTest {

    private val mapper = mock<RecipeMapper>()
    private val factory = mock<RecipesDataStoreFactory>()
    private val store = mock<RecipesDataStore>()
    private val cache = mock<RecipesCache>()
    private val repository = RecipesDataRepository(mapper, cache, factory)

    @Before
    fun setUp() {
        stubFactoryGetDataStore()
        stubFactoryGetCacheDataStore()
        stubIsCacheExpired(Single.just(false))
        stubAreRecipesCached(Single.just(false))
        stubSaveRecipes(Completable.complete())
    }

    @Test
    fun getRecipesCompletes() {
        stubGetRecipes(Observable.just(listOf(RecipeFactory.makeRecipeEntity())))
        stubMapper(RecipeFactory.makeRecipe(), any())
        val testObserver = repository.getRecipes().test()
        testObserver.assertComplete()
    }

    @Test
    fun getRecipesReturnsData() {
        val recipeEntity = RecipeFactory.makeRecipeEntity()
        val recipe = RecipeFactory.makeRecipe()
        stubGetRecipes(Observable.just(listOf(recipeEntity)))
        stubMapper(recipe, recipeEntity)
        val testObserver = repository.getRecipes().test()
        testObserver.assertValue(listOf(recipe))
    }

    @Test
    fun getRecipesBookmarkedCompletes() {
        stubGetBookmarkedRecipes(Observable.just(listOf(RecipeFactory.makeRecipeEntity())))
        stubMapper(RecipeFactory.makeRecipe(), any())
        val testObserver = repository.getBookmarkedRecipes().test()
        testObserver.assertComplete()
    }

    @Test
    fun getBookmarkedRecipesReturnsData() {
        val recipeEntity = RecipeFactory.makeRecipeEntity()
        val recipe = RecipeFactory.makeRecipe()
        stubGetBookmarkedRecipes(Observable.just(listOf(recipeEntity)))
        stubMapper(recipe, recipeEntity)
        val testObserver = repository.getBookmarkedRecipes().test()
        testObserver.assertValue(listOf(recipe))
    }

    @Test
    fun bookmarkRecipeCompletes() {
        stubBookmarkRecipe(Completable.complete())
        val testObserver = repository.bookmarkRecipe(DataFactory.uniqueId()).test()
        testObserver.assertComplete()
    }

    @Test
    fun unBookmarkRecipeCompletes() {
        stubUnBookmarkRecipe(Completable.complete())
        val testObserver = repository.unBookmarkRecipe(DataFactory.uniqueId()).test()
        testObserver.assertComplete()
    }

    fun stubBookmarkRecipe(completable: Completable) {
        whenever(store.setRecipeAsBookmarked(any()))
            .thenReturn(completable)
    }

    fun stubUnBookmarkRecipe(completable: Completable) {
        whenever(store.setRecipeAsNotBookmarked(any()))
            .thenReturn(completable)
    }

    private fun stubIsCacheExpired(single: Single<Boolean>) {
        whenever(cache.isRecipesCacheExpired())
            .thenReturn(single)
    }

    private fun stubAreRecipesCached(single: Single<Boolean>) {
        whenever(cache.areRecipesCached())
            .thenReturn(single)
    }

    private fun stubMapper(model: Recipe, entity: RecipeEntity) {
        whenever(mapper.mapFromEntity(entity))
            .thenReturn(model)
    }

    private fun stubGetRecipes(observable: Observable<List<RecipeEntity>>) {
        whenever(store.getRecipes())
            .thenReturn(observable)
    }

    private fun stubGetBookmarkedRecipes(observable: Observable<List<RecipeEntity>>) {
        whenever(store.getBookmarkedRecipes())
            .thenReturn(observable)
    }

    private fun stubFactoryGetDataStore() {
        whenever(factory.getDataStore(any(), any()))
            .thenReturn(store)
    }

    private fun stubFactoryGetCacheDataStore() {
        whenever(factory.getCacheDataStore())
            .thenReturn(store)
    }

    private fun stubSaveRecipes(completable: Completable) {
        whenever(store.saveRecipes(any()))
            .thenReturn(completable)
    }

}