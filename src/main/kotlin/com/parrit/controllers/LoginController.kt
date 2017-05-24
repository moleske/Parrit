package com.parrit.controllers

import com.parrit.DTOs.UsernameAndPasswordDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Collections

@Controller
class LoginController {

    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    //*********************//
    //******  Views  ******//
    //*********************//

    /*
     *  Login page for a project. This method expects that this page was reached
     *  by a redirect due to not being authenticated.  Therefore, we can see which
     *  project was attempted to be logged into by looking at the Session Request Cache
     *
     *  @returns: project-login page with the project name as a model attribute
     */
    @RequestMapping(path = arrayOf("/login/project"), method = arrayOf(RequestMethod.GET))
    fun loginProject(request: HttpServletRequest, response: HttpServletResponse, model: Model): String {
        val savedRequest = HttpSessionRequestCache().getRequest(request, response)

        //TODO: Check to make sure this isn't null -- maybe redirect to homepage if it is
        val originalRequestUrl = savedRequest.redirectUrl
        var projectName = originalRequestUrl.substring(originalRequestUrl.lastIndexOf('/') + 1)
        projectName = projectName.replace("%20", " ")

        model.addAttribute("projectName", projectName)
        return "project-login"
    }

    @RequestMapping(path = arrayOf("/error"), method = arrayOf(RequestMethod.GET))
    fun error(): String = "error"

    //********************//
    //******  APIs  ******//
    //********************//

    /*
     *  Attempts to log the user in and returns a href to the project that was logged into
     *
     *  @returns: href string for the project that was logged into
     *  @throws: InternalAuthenticationServiceException if somehow the user does not get authenticated and nothing else throws an exception
     */
    @RequestMapping(path = arrayOf("/login"), method = arrayOf(RequestMethod.POST))
    @ResponseBody
    @Throws(InternalAuthenticationServiceException::class)
    fun login(@RequestBody loginDetails: UsernameAndPasswordDTO): ResponseEntity<String> {
        val username = loginDetails.name
        val password = loginDetails.password

        val authentication = authenticationManager!!.authenticate(UsernamePasswordAuthenticationToken(username, password, emptyList()))

        if (authentication.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = authentication
            return ResponseEntity("/" + authentication.name, HttpStatus.OK)
        }

        throw InternalAuthenticationServiceException("Unknown authentication problem.")
    }
}
