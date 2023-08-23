package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.FdInputDto;
import com.tc.training.smallFinance.dtos.outputs.FdOutputDto;
import org.springframework.stereotype.Service;


public interface FdService {
    public FdOutputDto createFd(FdInputDto fdInputDto);
}
