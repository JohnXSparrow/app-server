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
import com.App.model.State;
import com.App.repository.CountryRepository;
import com.App.repository.StateRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/state")
public class StateController {

	@Autowired
	StateRepository stateRepository;

	@Autowired
	CountryRepository countryRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid State state, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (stateRepository.findByName(state.getNameState()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(8, "Estado ou Iniciais já registrados"),
					HttpStatus.BAD_REQUEST);
		}

		State stateAdd = stateRepository.save(state);
		return new ResponseEntity<>(stateAdd, HttpStatus.OK);
	}

	@RequestMapping(value = "/list/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@PathVariable long id) {

		if (id == 0) {
			return new ResponseEntity<>(new GenericReturnMessage(1, "ID deve ser maior que 0"), HttpStatus.BAD_REQUEST);
		}

		Country country = countryRepository.findById(id).orElse(null);;
		if (country == null) {
			return new ResponseEntity<>(new GenericReturnMessage(11, "País não encontrado"), HttpStatus.BAD_REQUEST);
		}

		Collection<State> listState = stateRepository.findAllByCountry(country);

		return new ResponseEntity<>(listState, HttpStatus.OK);
	}

	@RequestMapping(value = "/find/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<State> find(@PathVariable long id) {

		State stateList = stateRepository.findById(id).orElse(null);;
		return new ResponseEntity<>(stateList, HttpStatus.OK);
	}

}
