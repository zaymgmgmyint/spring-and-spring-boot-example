package com.supernap.multipleds.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.supernap.multipleds.dto.PersonnelDto;
import com.supernap.multipleds.dto.PersonnelResponse;

@Service
public class PersonnelService {

	private static final Logger LOG = LoggerFactory.getLogger(PersonnelService.class);

	private final RestTemplate restTemplate;

	private final String deviceIp = "http://192.168.1.50:8090";

	private final String pass = "admin$2024";

	public PersonnelService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	// create personnel
	public PersonnelDto registerFaceScannerTerminal(PersonnelDto p) {

		try {

			String url = "" + deviceIp + "/person/create";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			if (p != null && p.getUserId() != null && !p.getUserId().isEmpty()) {

				MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
				map.add("pass", pass);
				map.add("person", "{\"id\":\"" + p.getUserId() + "\",\"name\":\"" + p.getName() + "\",\"idcardNum\":\""
						+ p.getCardNoId() + "\",\"facePermission\":" + (p.getType().equals(1) ? 2 : 1)
						+ ",\"idCardPermission\":" + (p.getType().equals(2) ? 2 : 1) + ","
						+ "\"faceAndCardPermission\":" + (p.getType().equals(3) ? 2 : 1)
						+ ",\"faceAndQrCodePermission\":1,\"iDNumberPermission\":1,\"qrCode\":\"\",\"tag\":\""
						+ p.getTag() + "\"," + "\"phone\":\"" + p.getPhoneNo()
						+ "\",\"password\":\"\",\"passwordPermission\":1,\"fingerPermission\":1,\"qrCodePermission\":1,"
						+ "\"cardAndPasswordPermission\":1}");

				if (p.getType().equals(1) || p.getType().equals(3)) {
					map.add("face1", "{\"faceId\":\"" + p.getUserId() + "\",\"base64\":\"" + p.getFaceId() + "\"}");
				}

				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
				PersonnelResponse response = restTemplate.postForObject(url, request, PersonnelResponse.class);
				LOG.info("Create response: " + response);

				if(response != null && response.getSuccess()) {
					p.setDataSynced(1);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}

	// fetch personnel
	public List<String> fetch(List<PersonnelDto> personnelList) {

		List<String> responses = new ArrayList<>();
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			personnelList.forEach(personnel -> {
				String url = "" + deviceIp + "/person/find?pass=" + pass + "&ID=" + personnel.getUserId();

				ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
				String response = responseEntity.getBody();

				responses.add(response);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responses;

	}

	// delete personnel
	public List<String> delete(List<PersonnelDto> personnelList) {

		List<String> responses = new ArrayList<>();

		try {
			String url = "" + deviceIp + "/person/delete";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			personnelList.forEach(personnel -> {

				MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
				map.add("pass", pass);
				map.add("ID", personnel.getUserId());

				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
				String response = restTemplate.postForObject(url, request, String.class);

				responses.add(response);
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responses;

	}

}