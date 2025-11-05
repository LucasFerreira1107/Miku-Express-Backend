package com.mikuexpress.mikuexpress.dto;

public record ViaCepResponseDTO(
		String cep,
		String logradouro,
		String complemento,
		String bairro,
		String localidade,
		String uf,
		String erro) {

	public String getAddressForGoogle() {
		return localidade + ", " + uf + ", BR";
	}
	
	public String getAddressComplete() {
		if (hasError()) {
			return "CEP não encontrado";
		}
		return String.format("%s, %s - %s, %s - %s", 
                logradouro != null ? logradouro : "", 
                complemento != null ? complemento : "", 
                bairro != null ? bairro : "", 
                localidade != null ? localidade : "", 
                uf != null ? uf : "");
	}
	
	public boolean hasError() {
		// ViaCEP retorna um campo "erro": true quando o CEP não é encontrado
		// ou podemos verificar se campos essenciais estão nulos
		return (erro != null && "true".equalsIgnoreCase(erro)) 
				|| cep == null 
				|| logradouro == null 
				|| localidade == null;
	}
	
	public boolean isValid() {
		return !hasError() && cep != null && logradouro != null && localidade != null;
	}
}
