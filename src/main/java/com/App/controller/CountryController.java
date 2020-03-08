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
import com.App.model.Country;
import com.App.repository.CountryRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/country")
public class CountryController {

	@Autowired
	CountryRepository countryRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Country country, BindingResult bResult) {
		
		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}
		
		if (country.getInitials().length() > 2 || country.getInitials().length() < 2) {
			return new ResponseEntity<>(
					new GenericReturnMessage(21, "As iniciais devem conter exatamente 2 caracteres"),
					HttpStatus.BAD_REQUEST);
		}
		
		if (countryRepository.findByName(country.getNameCountry()) !=  null || countryRepository.findByInitials(country.getInitials()) !=  null) {
			return new ResponseEntity<>(new GenericReturnMessage(10, "País ou Iniciais já registrados"),
					HttpStatus.BAD_REQUEST);
		}

		Country countryAdd = countryRepository.save(country);
		return new ResponseEntity<>(countryAdd, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Country>> list() {

		Collection<Country> listCountry = countryRepository.findAll();
		return new ResponseEntity<>(listCountry, HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Country> remove(@PathVariable long id) {
		Country countryRemove = countryRepository.getOne(id);
		if (countryRemove == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		countryRepository.delete(countryRemove);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
