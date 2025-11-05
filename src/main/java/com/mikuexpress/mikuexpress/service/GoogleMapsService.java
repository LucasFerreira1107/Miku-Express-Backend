package com.mikuexpress.mikuexpress.service;

import org.springframework.stereotype.Service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsService {

	private final GeoApiContext context;
	
	
	/**
	 * Calcula a distância rodoviária em quilômetros entre dois endereços usando a API Google Maps Distance Matrix.
	 * 
	 * Este método:
	 * 1. Faz uma requisição para a API Google Maps Distance Matrix
	 * 2. Usa modo de viagem DRIVING (veículo)
	 * 3. Retorna a distância em quilômetros (converte de metros)
	 * 
	 * Os endereços devem estar no formato: "Cidade, Estado, BR" (ex: "São Paulo, SP, BR")
	 * 
	 * @param source Endereço de origem no formato "Cidade, Estado, BR"
	 * @param destination Endereço de destino no formato "Cidade, Estado, BR"
	 * @return Distância em quilômetros (Double) entre os dois endereços
	 * @throws RuntimeException se houver erro na chamada à API ou se não for possível calcular a distância
	 */
	public Double getDistanceInKM(String source, String destination) {
		try {
			log.info("Consultando Google Distance Matrix API: {} -> {}", source, destination);
			
			DistanceMatrix matrix = DistanceMatrixApi.newRequest(context)
					.origins(source)
					.destinations(destination)
					.mode(TravelMode.DRIVING)
					.units(Unit.METRIC)
					.await();
			if(matrix.rows.length > 0 && matrix.rows[0].elements.length > 0) {
				long distanceInMeters = matrix.rows[0].elements[0].distance.inMeters;
				double distanceInKms = distanceInMeters /1000;
				
				log.info("Distancia calculada: {} km", distanceInKms);
				return distanceInKms;
			}else {
				log.warn("Não foi possível calcular a distância para: {} -> {}", source, destination);
                throw new RuntimeException("Não foi possível calcular a distância. Verifique os endereços.");
			}
		}catch(Exception e) {
			log.error("Erro ao chamar API do Google Maps", e);
			throw new RuntimeException("Erro ao chamar API do Google Maps: " + e.getMessage());
		}
	}
}
