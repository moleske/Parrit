package com.parrit.controllers;

import com.parrit.DTOs.UsernameAndPasswordDTO;
import com.parrit.DTOs.WorkspaceDTO;
import com.parrit.entities.Workspace;
import com.parrit.repositories.WorkspaceRepository;
import com.parrit.transformers.WorkspaceTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;

@Controller
public class WorkspaceController {

    private WorkspaceRepository workspaceRepository;

	@Autowired
	public WorkspaceController(WorkspaceRepository workspaceRepository) {
		this.workspaceRepository = workspaceRepository;
	}

    //*********************//
    //******  Views  ******//
    //*********************//

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getDashboard() {
        return "dashboard";
    }

    @PreAuthorize("@authorizationService.canAccessWorkspace(principal, #workspaceName)")
    @RequestMapping(path = "/{workspaceName:.+}", method = RequestMethod.GET)
    public String getWorkspace(@PathVariable String workspaceName, Model model) {
        Workspace workspace = workspaceRepository.findByName(workspaceName);
        model.addAttribute("workspace", WorkspaceTransformer.transform(workspace));
        return "workspace";
    }

    //********************//
    //******  APIs  ******//
    //********************//

    @RequestMapping(path = "/api/workspace/new", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseBody
    public void createWorkspace(@RequestBody UsernameAndPasswordDTO usernameAndPasswordDTO) throws NestedServletException {
        if(usernameAndPasswordDTO.getName().isEmpty() || usernameAndPasswordDTO.getPassword().isEmpty()) {
            throw new NestedServletException("Workspace Name and/or Password is empty!");
        }

        ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
        String hashedPassword = encoder.encodePassword(usernameAndPasswordDTO.getPassword(), null);

        Workspace workspace = new Workspace(usernameAndPasswordDTO.getName(), hashedPassword, new ArrayList<>(), new ArrayList<>());
        workspaceRepository.save(workspace);
    }

    //TODO: This authorization will not work if the workspace name is being changed.....
    @PreAuthorize("@authorizationService.canAccessWorkspace(principal, #workspaceDTO)")
    @RequestMapping(path = "/api/workspace", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseBody
    public ResponseEntity<WorkspaceDTO> saveWorkspace(@RequestBody WorkspaceDTO workspaceDTO) {
        Workspace savedWorkspace = workspaceRepository.findOne(workspaceDTO.getId());
        Workspace updatedWorkspace = WorkspaceTransformer.merge(savedWorkspace, workspaceDTO);
        updatedWorkspace = workspaceRepository.save(updatedWorkspace);
        return new ResponseEntity<>(WorkspaceTransformer.transform(updatedWorkspace), HttpStatus.OK);
    }
}
