package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.SlabInputDto;
import com.tc.training.smallFinance.dtos.outputs.SlabOutputDto;
import com.tc.training.smallFinance.service.SlabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slab")
public class SlabController {
    @Autowired
    private SlabService slabService;

    @PostMapping("/add")
    public SlabOutputDto addSlab(@RequestBody SlabInputDto slabInputDto){
        return slabService.addSlab(slabInputDto);
    }

    @GetMapping("/getAll")
    public List<SlabOutputDto> getAllSlabs(){
        return slabService.getAllSlabs();
    }
}
