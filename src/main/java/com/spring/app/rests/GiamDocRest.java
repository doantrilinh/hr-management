package com.spring.app.rests;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "giamdoc", produces = MediaType.APPLICATION_JSON_VALUE)
public class GiamDocRest {

}
