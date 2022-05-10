package com.ramzi.gestiondestock.controller.api;

import com.ramzi.gestiondestock.dto.auth.AuthenticationRequest;
import com.ramzi.gestiondestock.dto.auth.AuthenticationResponse;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.ramzi.gestiondestock.utils.Constants.AUTHENTICATION_ENDPOINT;

@Api("authentication")
public interface AuthenticationApi {

  @PostMapping(AUTHENTICATION_ENDPOINT + "/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request);

}
