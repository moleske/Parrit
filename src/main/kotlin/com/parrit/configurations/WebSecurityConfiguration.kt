package com.parrit.configurations

import com.parrit.configurations.security.ProjectDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils
import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("cloud")
open class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var projectDetailsService: ProjectDetailsService

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .formLogin()
                    .loginPage("/login/project")
                    .failureUrl("/error")
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/logout/project")
                    .logoutSuccessUrl("/")
                    .and()
                .authorizeRequests()
                    .antMatchers("/favicon.ico").permitAll()
                    .antMatchers("/built/**").permitAll()
                    .antMatchers("/svg/**").permitAll()
                    .antMatchers("/img/**").permitAll()
                    .antMatchers("/").permitAll()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/api/project/new").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .csrf().csrfTokenRepository(csrfTokenRepository()).and()
                .addFilterAfter(csrfHeaderFilter(), CsrfFilter::class.java)
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        val customAuthenticationProvider = DaoAuthenticationProvider()
        customAuthenticationProvider.setUserDetailsService(projectDetailsService)
        customAuthenticationProvider.setPasswordEncoder(ShaPasswordEncoder(256))
        customAuthenticationProvider.isHideUserNotFoundExceptions = false

        auth.authenticationProvider(customAuthenticationProvider)
    }

    private fun csrfHeaderFilter(): Filter {
        return object : OncePerRequestFilter() {
            @Throws(ServletException::class, IOException::class)
            override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
                val csrf = request.getAttribute(CsrfToken::class.java.name) as CsrfToken?
                if (csrf != null) {
                    var cookie: Cookie? = WebUtils.getCookie(request, "XSRF-TOKEN")
                    val token = csrf.token
                    if (cookie == null || token != null && token != cookie.value) {
                        cookie = Cookie("XSRF-TOKEN", token)
                        cookie.path = "/"
                        response.addCookie(cookie)
                    }
                }
                filterChain.doFilter(request, response)
            }
        }
    }

    private fun csrfTokenRepository(): CsrfTokenRepository {
        val repository = HttpSessionCsrfTokenRepository()
        repository.setHeaderName("X-XSRF-TOKEN")
        return repository
    }
}
