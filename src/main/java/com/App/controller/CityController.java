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
import com.App.model.State;
import com.App.repository.CityRepository;
import com.App.repository.StateRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/city")
public class CityController {

	@Autowired
	CityRepository cityRepository;

	@Autowired
	StateRepository stateRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid City city, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (cityRepository.findByName(city.getNameCity()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(4, "Cidade já registrada"), HttpStatus.BAD_REQUEST);
		}

		City cityAdd = cityRepository.save(city);
		return new ResponseEntity<>(cityAdd, HttpStatus.OK);
	}

	@RequestMapping(value = "/list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@PathVariable long id) {

		if (id == 0) {
			return new ResponseEntity<>(new GenericReturnMessage(1, "ID deve ser maior que 0"), HttpStatus.BAD_REQUEST);
		}

		State state = stateRepository.findById(id).orElse(null);
		if (state == null) {
			return new ResponseEntity<>(new GenericReturnMessage(6, "Estado não encontrado"), HttpStatus.BAD_REQUEST);
		}

		Collection<City> listCity = cityRepository.findAllByState(state);
		return new ResponseEntity<>(listCity, HttpStatus.OK);
	}

}
