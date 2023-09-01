package com.tc.training.smallFinance.dtos.outputs;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
public class RoleAndPermissionOutputDto {

    private RequestMethod method;

    private String uri;

    private String roles;
}
