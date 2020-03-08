package com.App.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.App.model.PackageCoin;
import com.App.repository.CoinRepository;
import com.App.repository.PackageCoinRepository;

@RestController
public class PackageCoinController {
	
	private final BigDecimal taxPercentage = new BigDecimal(9.58);
	private final BigDecimal taxFixPaypal = new BigDecimal(0.90);

	@Autowired
	PackageCoinRepository packageCoinRepository;
	
	@Autowired
	CoinRepository coinRepository;

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/adm/packagecoin/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> add(@RequestBody @Valid PackageCoin packageCoin, BindingResult bResult) {

		if (bResult.hasErrors()) {
			return new ResponseEntity<>(bResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
		}		
		
		Coin coin = coinRepository.findById(packageCoin.getCoin().getId_coin()).orElse(null);;
		if(coin == null) {
			return new ResponseEntity<>(new GenericReturnMessage(29, "Tipo de moeda inválida"),
					HttpStatus.BAD_REQUEST);
		}
		
		packageCoin.setValuePackage(new BigDecimal(packageCoin.getAmountCoin() * coin.getRealUnitvalue()).setScale(2, RoundingMode.HALF_EVEN));
		
		packageCoin.setTax(packageCoin.getValuePackage()
			    .multiply(taxPercentage)
			    .divide(new BigDecimal(100))
			    .add(taxFixPaypal)
				.setScale(2, RoundingMode.HALF_EVEN));
		
		packageCoin.setTotalToPay(packageCoin.getValuePackage()
				.add(packageCoin.getValuePackage()
			    .multiply(taxPercentage)
			    .divide(new BigDecimal(100))
			    .add(taxFixPaypal))
				.setScale(2, RoundingMode.HALF_EVEN));

		PackageCoin packageCoinAdd = packageCoinRepository.save(packageCoin);

		return new ResponseEntity<>(packageCoinAdd, HttpStatus.OK);
	}

	@RequestMapping(value = "/packagecoin/list", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Collection<PackageCoin>> list() {

		Collection<PackageCoin> listPackageCoin = packageCoinRepository.findAll();
		return new ResponseEntity<>(listPackageCoin, HttpStatus.OK);
	}

}
