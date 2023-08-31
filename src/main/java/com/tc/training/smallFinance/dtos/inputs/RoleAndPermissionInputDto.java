package com.tc.training.smallFinance.dtos.inputs;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
public class RoleAndPermissionInputDto {

    private RequestMethod method;

    private String uri;

    private String roles;
}
