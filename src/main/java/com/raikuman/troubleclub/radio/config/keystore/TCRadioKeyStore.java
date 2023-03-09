package com.raikuman.troubleclub.radio.config.keystore;

import com.raikuman.botutilities.crypto.keystore.KeyStoreInterface;

import java.util.List;

public class TCRadioKeyStore implements KeyStoreInterface {

	@Override
	public List<String> keyStoreAliases() {
		return List.of(
			"songs-encryption-secret"
		);
	}
}
