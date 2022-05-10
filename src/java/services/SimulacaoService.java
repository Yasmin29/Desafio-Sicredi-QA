package services;

import dados.Body;
import io.restassured.response.Response;

public class SimulacaoService extends BaseService {

    private static final String SIMULACAO_ENDPOINT = "http://localhost:8080/api/v1/simulacoes";
    private static final String BARRA = "/";

    public Response postSimulacao(String nome, String cpf, String email, Double valor, Integer parcelas, Boolean seguro) {
        Body body = new Body(nome, cpf, email, valor, parcelas, seguro);
        return doPostRequest(body, SIMULACAO_ENDPOINT);
    }

    public Response putSimulacao(String cpfParaEditar, String nome, String cpf, String email, Double valor, Integer parcelas, Boolean seguro) {
        Body body = new Body(nome,cpf,email,valor,parcelas,seguro);
        return doPutRequest(body, SIMULACAO_ENDPOINT + BARRA + cpfParaEditar);
    }

    public Response deleteSimulacao(Integer id) {
        return doDeleteRequest(SIMULACAO_ENDPOINT + BARRA + id);
    }

    public Response getSimulacao(){
        return doGetRequest(SIMULACAO_ENDPOINT);
    }

    public Response getSimulcaoCPF(String cpf){
        return doGetRequest(SIMULACAO_ENDPOINT + BARRA + cpf);
    }

}
