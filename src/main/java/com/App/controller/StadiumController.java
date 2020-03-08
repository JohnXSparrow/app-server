package com.App.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.exception.GenericReturnMessage;
import com.App.model.City;
import com.App.model.Stadium;
import com.App.repository.CityRepository;
import com.App.repository.StadiumRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/stadium")
public class StadiumController {

	@Autowired
	StadiumRepository stadiumRepository;

	@Autowired
	CityRepository cityRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Stadium stadium, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (stadiumRepository.findByName(stadium.getNameStadium()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(7, "Estádio já registrado"),
					HttpStatus.BAD_REQUEST);
		}

		Stadium stadiumAdd = stadiumRepository.save(stadium);
		return new ResponseEntity<>(stadiumAdd, HttpStatus.OK);
	}

	@RequestMapping(value = "/list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@PathVariable long id) {

		if (id == 0) {
			return new ResponseEntity<>(new GenericReturnMessage(1, "ID deve ser maior que 0"),
					HttpStatus.BAD_REQUEST);
		}

		City city = cityRepository.findById(id).orElse(null);;
		if (city == null) {
			return new ResponseEntity<>(new GenericReturnMessage(5, "Cidade não encontrada"), HttpStatus.BAD_REQUEST);
		}

		Collection<Stadium> listStadium = stadiumRepository.findAllByCity(city);
		return new ResponseEntity<>(listStadium, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/findAll/{nameStadium}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> findAll(@PathVariable String nameStadium) {
		
		if(nameStadium == null|| nameStadium.length() <= 0) {
			return new ResponseEntity<>(new GenericReturnMessage(71, "Nome Inválido"),	HttpStatus.BAD_REQUEST);
		}

		Collection<Stadium> foundStadium = stadiumRepository.findAllByName(nameStadium);
		return new ResponseEntity<>(foundStadium, HttpStatus.OK);
	}

}
