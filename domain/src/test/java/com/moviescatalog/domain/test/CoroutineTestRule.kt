package com.moviescatalog.domain.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit rule for testing coroutines with TestCoroutineScheduler.
 * This provides a test dispatcher for coroutine testing and automatically
 * sets and resets the Main dispatcher.
 *
 * Usage:
 * ```
 * @get:Rule
 * val coroutineRule = CoroutineTestRule()
 *
 * @Test
 * fun testSomething() = coroutineRule.runTest {
 *     // Your test code here
 * }
 * ```
 */
@ExperimentalCoroutinesApi
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

/**
 * Extension function to run a test with the test dispatcher
 */
@ExperimentalCoroutinesApi
fun CoroutineTestRule.runTest(block: suspend TestScope.() -> Unit) {
    kotlinx.coroutines.test.runTest(testDispatcher.scheduler) {
        block()
    }
}
