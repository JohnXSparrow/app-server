package com.App.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.App.exception.GenericReturnMessage;
import com.App.model.Tourn;
import com.App.repository.TournRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/tourn")
public class TournController {

	@Autowired
	TournRepository tournRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Tourn tourn, BindingResult bResult) {
		
		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}
		
		if (tournRepository.findByName(tourn.getNameTourn()) != null) {
			return new ResponseEntity<>(new GenericReturnMessage(9, "Torneio já registrado"),
					HttpStatus.BAD_REQUEST);
		}

		Tourn tournAdd = tournRepository.save(tourn);
		return new ResponseEntity<>(tournAdd, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Collection<Tourn>> list() {

		Collection<Tourn> listTourn = tournRepository.findAll();
		return new ResponseEntity<>(listTourn, HttpStatus.OK);
	}

}
