package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.RoleAndPermissionInputDto;
import com.tc.training.smallFinance.dtos.outputs.RoleAndPermissionOutputDto;
import com.tc.training.smallFinance.exception.ElementNotFound;
import com.tc.training.smallFinance.model.RoleAndPermission;
import com.tc.training.smallFinance.repository.RoleAndPermissionRepo;
import com.tc.training.smallFinance.service.RoleAndPermissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Service
public class RoleAndPermissionServiceImpl implements RoleAndPermissionService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleAndPermissionRepo roleAndPermissionRepo;
    @Override
    public RoleAndPermissionOutputDto createPermission(RoleAndPermissionInputDto roleAndPermissionInputDto) {
        RoleAndPermission roleAndPermission = modelMapper.map(roleAndPermissionInputDto, RoleAndPermission.class);
        roleAndPermission = roleAndPermissionRepo.save(roleAndPermission);
        return modelMapper.map(roleAndPermission, RoleAndPermissionOutputDto.class);
    }

    @Override
    public List<RoleAndPermissionOutputDto> getAllPermission() {
        List<RoleAndPermission> roleAndPermission = roleAndPermissionRepo.findAll();
        return roleAndPermission.stream().map(role -> modelMapper.map(role, RoleAndPermissionOutputDto.class)).toList();
    }

    @Override
    public RoleAndPermissionOutputDto getAllPermissionByMethodAndUrl(RequestMethod method, String uri) {
//        RoleAndPermission roleAndPermission=roleAndPermissionRepo.findByHttpMethodTypeAndUri(httpMethod,uri ).orElseThrow(() -> new ElementNotFound("no such permisiions with this methodand uri"));
        RoleAndPermission roleAndPermission = roleAndPermissionRepo.findByMethodAndUri(method, uri).orElseThrow(() -> new ElementNotFound("no such permissions with this method and uri"));
        return modelMapper.map(roleAndPermission, RoleAndPermissionOutputDto.class);
    }
}
