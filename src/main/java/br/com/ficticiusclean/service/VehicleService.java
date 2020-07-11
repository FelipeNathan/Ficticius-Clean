package br.com.ficticiusclean.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.ficticiusclean.dto.VehicleDTO;
import br.com.ficticiusclean.exception.EntityNotFoundException;
import br.com.ficticiusclean.exception.InvalidEntityException;
import br.com.ficticiusclean.model.Vehicle;
import br.com.ficticiusclean.model.VehicleBrand;
import br.com.ficticiusclean.service.core.ServiceBaseMapperImpl;

@Service
@Transactional
public class VehicleService extends ServiceBaseMapperImpl<Vehicle, VehicleDTO> {

	@Inject
	private VehicleBrandService brandService;

	public VehicleDTO createVehicle(VehicleDTO dto) {

		validateDTO(dto);

		Vehicle vehicle = this.mapper.toEntity(dto);

		VehicleBrand brand = brandService.findOneByName(dto.getBrand());

		if (brand == null)
			throw new EntityNotFoundException(dto.getBrand());

		vehicle.setBrand(brand);

		vehicle = this.repositoryBase.save(vehicle);

		dto.setId(vehicle.getId());
		return dto;
	}

	public List<VehicleDTO> calculateConsumption(BigDecimal gasPrice, BigDecimal kmInCity, BigDecimal kmInRoad) {

		List<VehicleDTO> vehicles = findAllDTO();

		vehicles.forEach(vehicle -> calculateConsumptionByVehicle(vehicle, gasPrice, kmInCity, kmInRoad));
		vehicles.sort(Comparator.comparing(VehicleDTO::getPriceConsumptionTotal).reversed());

		return vehicles;
	}

	public void calculateConsumptionByVehicle(VehicleDTO vehicle, BigDecimal gasPrice, BigDecimal kmInCity, BigDecimal kmInRoad) {

		BigDecimal totalGasConsumptionInCity = kmInCity.divide(vehicle.getGasConsumptionCity(), 2, RoundingMode.HALF_EVEN);
		BigDecimal totalGasConsumptionInRoad = kmInRoad.divide(vehicle.getGasConsumptionRoad(), 2, RoundingMode.HALF_EVEN);

		BigDecimal totalGasConsumption = totalGasConsumptionInCity.add(totalGasConsumptionInRoad);
		vehicle.setGasConsumptionTotal(totalGasConsumption.setScale(2, RoundingMode.HALF_EVEN));
		vehicle.setPriceConsumptionTotal(totalGasConsumption.multiply(gasPrice).setScale(2, RoundingMode.HALF_EVEN));
	}

	public void validateDTO(VehicleDTO dto) {

		if (StringUtils.isEmpty(dto.getBrand()))
			throw new InvalidEntityException("Brand is required");

		if (StringUtils.isEmpty(dto.getModel()))
			throw new InvalidEntityException("Model is required");

		if (StringUtils.isEmpty(dto.getName()))
			throw new InvalidEntityException("Name is required");
	}
}
