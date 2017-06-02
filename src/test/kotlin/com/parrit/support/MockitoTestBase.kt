package com.parrit.support

import org.junit.Before
import org.mockito.MockitoAnnotations

open class MockitoTestBase {

    @Before
    fun setupMockitoBase() {
        MockitoAnnotations.initMocks(this)
    }

}
