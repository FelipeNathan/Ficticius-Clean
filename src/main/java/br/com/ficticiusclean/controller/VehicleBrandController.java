package br.com.ficticiusclean.controller;

import br.com.ficticiusclean.controller.core.ControllerBase;
import br.com.ficticiusclean.dto.VehicleBrandDTO;
import br.com.ficticiusclean.exception.InvalidEntityException;
import br.com.ficticiusclean.model.VehicleBrand;
import br.com.ficticiusclean.service.VehicleBrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicle-brand")
public class VehicleBrandController extends ControllerBase<VehicleBrand, VehicleBrandDTO, VehicleBrandService> {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleBrandDTO> create(@RequestBody VehicleBrandDTO brandDTO) throws InvalidEntityException {

        return new ResponseEntity<>(this.service.createBrand(brandDTO), HttpStatus.CREATED);
    }
}
