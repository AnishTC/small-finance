package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.RoleAndPermissionInputDto;
import com.tc.training.smallFinance.dtos.outputs.RoleAndPermissionOutputDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Service
public interface RoleAndPermissionService {
    RoleAndPermissionOutputDto createPermission(RoleAndPermissionInputDto roleAndPermissionInputDto);

    List<RoleAndPermissionOutputDto> getAllPermission();

    List<RoleAndPermissionOutputDto> getAllPermissionByMethodAndUrl(RequestMethod httpMethod, String uri);

}
