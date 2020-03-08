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
import com.App.model.Coin;
import com.App.repository.CoinRepository;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/adm/coin")
public class CoinController {

	@Autowired
	CoinRepository coinRepository;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid Coin coin, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}

		if (!((coin.getNameCoin().equals("Ouro")) || (coin.getNameCoin().equals("Prata")))) {
			return new ResponseEntity<>(new GenericReturnMessage(3, "Nome deve ser Ouro ou Prata"), HttpStatus.BAD_REQUEST);
		}

		if ((coinRepository.findAll().size() > 1) && (coin.getId_coin() == 0)) {
			return new ResponseEntity<>(new GenericReturnMessage(2, "ID deve ser informado"), HttpStatus.BAD_REQUEST);
		} else if (coinRepository.findAll().size() == 0) {
			if (!(coin.getNameCoin().equals("Ouro"))) {
				return new ResponseEntity<>(new GenericReturnMessage(14, "Nome deve ser Ouro"), HttpStatus.BAD_REQUEST);
			}

		} else if (coinRepository.findAll().size() == 1) {
			if (!(coin.getNameCoin().equals("Prata"))) {
				return new ResponseEntity<>(new GenericReturnMessage(15, "Nome deve ser Prata"), HttpStatus.BAD_REQUEST);
			}
		}

		if (coin.getId_coin() == 1) {
			if (!(coin.getNameCoin().equals("Ouro"))) {
				return new ResponseEntity<>(
						new GenericReturnMessage(16, "Nome com ID:" + coin.getId_coin() + " deve ser Ouro"),
						HttpStatus.BAD_REQUEST);
			}
		} else if (coin.getId_coin() == 2) {
			if (!(coin.getNameCoin().equals("Prata"))) {
				return new ResponseEntity<>(
						new GenericReturnMessage(17, "Nome com ID: " + coin.getId_coin() + " deve ser Prata"),
						HttpStatus.BAD_REQUEST);
			}
		} else if (coin.getId_coin() > 2) {
			return new ResponseEntity<>(new GenericReturnMessage(18, "Não pode adicionar uma nova moeda"), HttpStatus.BAD_REQUEST);
		}

		Coin coinAdd = coinRepository.save(coin);
		return new ResponseEntity<>(coinAdd, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Collection<Coin>> list() {

		Collection<Coin> listCoin = coinRepository.findAll();
		return new ResponseEntity<>(listCoin, HttpStatus.OK);
	}
}
