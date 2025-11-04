package io.github.isaevisa05.bank;

import org.springframework.boot.SpringApplication;

public class TestBankApplication {

	public static void main(String[] args) {
		SpringApplication.from(BankApplication::main).run(args);
	}

}
