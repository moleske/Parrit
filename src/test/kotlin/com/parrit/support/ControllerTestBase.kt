package com.parrit.support

import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebAppConfiguration
open class ControllerTestBase : SpringTestBase() {

    lateinit var mvc: MockMvc

    @Autowired
    private val context: WebApplicationContext? = null

    @Before
    fun setUpControllerBase() {
        mvc = MockMvcBuilders.webAppContextSetup(context!!).build()
    }
}
