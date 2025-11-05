package com.mikuexpress.mikuexpress.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mikuexpress.mikuexpress.dto.ViaCepResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViaCepService {

	private final RestTemplate restTemplate;
	
	/**
	 * Busca o endereço completo através da API ViaCEP usando o CEP informado.
	 * 
	 * Este método faz uma requisição GET para a API ViaCEP (https://viacep.com.br)
	 * e retorna os dados completos do endereço incluindo logradouro, bairro, cidade e estado.
	 * 
	 * @param cep CEP a ser consultado (apenas números, ex: "01310100")
	 * @return ViaCepResponseDTO com os dados do endereço encontrado
	 * @throws RuntimeException se houver erro na consulta à API ViaCEP ou se o CEP for inválido
	 */
	public ViaCepResponseDTO getAddressByCep(String cep) {
		String url = "https://viacep.com.br/ws/" + cep + "/json/";
		
		try {
			ViaCepResponseDTO response = restTemplate.getForObject(url, ViaCepResponseDTO.class);
			return response;
		}catch(Exception e) {
			throw new RuntimeException("Erro ao consultar ViaCEP: " + e.getMessage());
		}
	}
}
