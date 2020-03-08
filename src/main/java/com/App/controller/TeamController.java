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
import com.App.model.Team;
import com.App.repository.TeamRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MODERATOR')")
@RequestMapping("/adm/team")
public class TeamController {

	@Autowired
	TeamRepository teamRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> add(@RequestBody @Valid Team team, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldError(), HttpStatus.BAD_REQUEST);
		}

		Team findByName = teamRepository.findByName(team.getNameTeam());
		if ((findByName != null)) {
			return new ResponseEntity<>(new GenericReturnMessage(12, "Time com nome: " + findByName.getNameTeam() + " já registado"), HttpStatus.BAD_REQUEST);
		}

		Team teamAdd = teamRepository.save(team);
		return new ResponseEntity<>(teamAdd, HttpStatus.OK);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Team>> listAll() {

		Collection<Team> listTeams = teamRepository.findAll();
		return new ResponseEntity<>(listTeams, HttpStatus.OK);
	}

	@RequestMapping(value = "/findAll/{nameTeam}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> findAll(@PathVariable String nameTeam) {

		if (nameTeam == null || nameTeam.length() <= 0) {
			return new ResponseEntity<>(new GenericReturnMessage(71, "Nome Inválido"), HttpStatus.BAD_REQUEST);
		}

		Collection<Team> foundTeams = teamRepository.findAllByName(nameTeam);
		return new ResponseEntity<>(foundTeams, HttpStatus.OK);
	}

}
