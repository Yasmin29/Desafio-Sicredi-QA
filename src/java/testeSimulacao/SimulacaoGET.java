package testeSimulacao;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.Test;
import services.SimulacaoService;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimulacaoGET {
    private SimulacaoService simulacaoService;
    private Faker faker;
    private Response response;
    private String cpf;
    private String nome;
    private String email;
    private Double valor;
    private Integer parcelas;
    private Boolean seguro;

    public SimulacaoGET(){
        this.simulacaoService = new SimulacaoService();
        this.faker = new Faker(new Locale("pt-BR"));
        this.cpf = faker.number().digits(11);
        this.nome = "Amanda";
        this.email = "email@email.com";
        this.valor = 1500.00;
        this.parcelas = 5;
        this.seguro = true;
    }

    @Test
    public void deveRetornarAsSimulacoesExistentes(){
        Response response = simulacaoService.getSimulacao();

        assertEquals(200,response.getStatusCode());
    }

    @Test
    public void quandoFacoABuscaPorUmCpfValidoEntaoRetornaASimulacaoCadastrada(){
        simulacaoService.postSimulacao(nome, cpf, email, valor, parcelas, seguro);
        response = simulacaoService.getSimulcaoCPF(cpf);

        assertEquals(nome, response.getBody().jsonPath().getString("nome"));
        assertEquals(cpf, response.getBody().jsonPath().getString("cpf"));
        assertEquals(email, response.getBody().jsonPath().getString("email"));
        assertEquals("2000.0", String.valueOf(response.jsonPath().getDouble("valor")));
        assertEquals("5", String.valueOf(response.jsonPath().getInt("parcelas")));
        assertTrue(response.jsonPath().getBoolean("seguro"));
    }

    @Test
    public void quandoFacoABuscaPorUmCpfQueNaoPossuiSimulacaoEntaoRetorna404(){
        response = simulacaoService.getSimulcaoCPF("84809766080");

        assertEquals(404, response.getStatusCode());
        assertEquals("CPF 84809766080 não encontrado", response.getBody().jsonPath().getString("mensagem") );
    }


}
