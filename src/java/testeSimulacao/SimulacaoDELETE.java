package testeSimulacao;

import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import services.SimulacaoService;
import com.github.javafaker.Faker;
import java.util.Locale;
import static org.junit.Assert.assertEquals;

public class SimulacaoDELETE {

    private SimulacaoService simulacaoService;
    private Faker faker;
    private Response response;
    private Integer id;
    private String cpf;

    public SimulacaoDELETE(){
        this.simulacaoService = new SimulacaoService();
        this.faker = new Faker(new Locale("pt-BR"));
        this.cpf = faker.number().digits(11);
    }

    @Before
    public void criarNovoID(){
        response = simulacaoService.postSimulacao("Nome Para Excluir", cpf, "email@email.com", 1000.00, 4, false);
        this.id = response.jsonPath().getInt("id");
    }

    //Na regra de negocio eh pedido que retorne 204 ao ser alterado com sucesso
    //porem na documentacao do swagger esta retornando 200
    @Test
    public void quandoDeletoUmIdExistenteNaSimulacaoEntaoRetornaStatusCode200() {
        response = simulacaoService.deleteSimulacao(id);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void quandoExcluoUmIdEntaoSeuRespectivoCpfMaisConstarNaListaDeSimulacao(){
        simulacaoService.deleteSimulacao(id);
        response = simulacaoService.getSimulcaoCPF(cpf);
        assertEquals(404, response.getStatusCode());
        assertEquals("CPF " +cpf+ " não encontrado", response.jsonPath().getString("mensagem"));
    }

    //Inconsistencia na API
    //Na regra de negocio eh pedido que retorne 404 com a mensagem "Simulação não encontrada", se nao existir a simulacao pelo ID informado;
    @Test
    public void quandoDeletoUmIdInexistenteEntaoDeveRetornar404(){
        response = simulacaoService.deleteSimulacao(0000000000);
        assertEquals(404, response.getStatusCode());
    }


}
