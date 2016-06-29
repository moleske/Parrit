package com.parrit.support

import com.parrit.ParritApplication
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(ParritApplication::class))
@ActiveProfiles(profiles = arrayOf("test"))
@Transactional
open class SpringTestBase : MockitoTestBase()
