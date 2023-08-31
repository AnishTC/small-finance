package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.SlabInputDto;
import com.tc.training.smallFinance.dtos.outputs.SlabOutputDto;
import com.tc.training.smallFinance.model.Slabs;
import com.tc.training.smallFinance.repository.SlabRepository;
import com.tc.training.smallFinance.service.SlabService;
import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlabServiceImpl implements SlabService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SlabRepository slabRepository;

    @Override
    public SlabOutputDto addSlab(SlabInputDto slabInputDto) {
        Slabs slab = modelMapper.map(slabInputDto, Slabs.class);
        slab.setTenures(Tenures.valueOf(slabInputDto.getTenures()));
        slab.setTypeOfTransaction(TypeOfTransaction.valueOf(slabInputDto.getTypeOfTransaction()));
        slabRepository.save(slab);
        return  modelMapper.map(slab,SlabOutputDto.class);
    }

    @Override
    public List<SlabOutputDto> getAllSlabs() {
       return slabRepository.findAll().stream().map(slab->modelMapper.map(slab,SlabOutputDto.class)).collect(Collectors.toList());
    }
}
