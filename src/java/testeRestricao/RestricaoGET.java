package testeRestricao;

import io.restassured.response.Response;
import org.junit.Test;
import services.RestricaoService;

import static org.junit.Assert.assertEquals;

public class RestricaoGET {
    RestricaoService restricaoService;
    Response response;

    public RestricaoGET(){
        this.restricaoService = new RestricaoService();
    }

    @Test
    public void quandoPesquisoUmCpfSemRestricaoEntaoRetornaStatus204() {
        response = restricaoService.getRestricaoCpf("12345678900");

        assertEquals(204,response.getStatusCode());
    }

    @Test
    public void quandoPesquisoUmCpfComRestricaoEntaoRetornaStatus200EMensagem(){
        response = restricaoService.getRestricaoCpf("58063164083");

        assertEquals(200, response.getStatusCode());
        assertEquals( "O CPF 58063164083 tem problema", response.getBody().jsonPath().getString("mensagem"));
    }
}
