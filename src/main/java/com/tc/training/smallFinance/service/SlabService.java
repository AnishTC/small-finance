package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.SlabInputDto;
import com.tc.training.smallFinance.dtos.outputs.SlabOutputDto;

import java.util.List;

public interface SlabService {
    SlabOutputDto addSlab(SlabInputDto slabInputDto);

    List<SlabOutputDto> getAllSlabs();
}
