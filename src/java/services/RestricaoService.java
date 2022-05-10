package services;

import io.restassured.response.Response;

public class RestricaoService extends BaseService {
    private static final String SIMULACAO_ENDPOINT = "http://localhost:8080/api/v1/restricoes/";

    public Response getRestricaoCpf(String cpf){
        return doGetRequest(SIMULACAO_ENDPOINT + cpf);
    }
}
