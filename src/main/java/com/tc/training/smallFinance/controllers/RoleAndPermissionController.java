package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.RoleAndPermissionInputDto;
import com.tc.training.smallFinance.dtos.outputs.RoleAndPermissionOutputDto;
import com.tc.training.smallFinance.service.RoleAndPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roleAndPermission")

public class RoleAndPermissionController {
    @Autowired
    private RoleAndPermissionService roleAndPermissionService;
    @PostMapping("/createPermission")
    public RoleAndPermissionOutputDto createRole(@RequestBody RoleAndPermissionInputDto roleAndPermissionInputDto){
        return roleAndPermissionService.createPermission(roleAndPermissionInputDto);
    }
}
