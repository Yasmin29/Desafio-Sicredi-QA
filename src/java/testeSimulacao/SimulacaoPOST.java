package testeSimulacao;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.Test;
import services.SimulacaoService;

import java.util.Locale;

import static org.junit.Assert.*;

public class SimulacaoPOST {

    private Faker faker;
    private String nome;
    private String cpf;
    private String email;
    private Double valor;
    private Integer parcelas;
    private Boolean seguro;
    private String cpfExistente;
    private String emailInvalido;
    private SimulacaoService simulacao;
    private Response response;

    public SimulacaoPOST() {
        this.faker = new Faker(new Locale("pt-BR"));
        this.nome = faker.name().firstName();
        this.cpf = faker.number().digits(11);
        this.email = nome.replaceAll("\\s+", "") + ".email@email.com";
        this.valor = 1500.00;
        this.parcelas = 4;
        this.seguro = true;
        this.cpfExistente = "66414919004";
        this.emailInvalido = "email@invalido";
        this.simulacao = new SimulacaoService();
    }

    @Test
    public void quandoInsiroValoresValidosNosCamposEntaoRetornaStatus201() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, parcelas, seguro);

        assertEquals(201, response.getStatusCode());
        assertEquals(nome, response.jsonPath().getString("nome"));
        assertEquals(cpf, response.jsonPath().getString("cpf"));
        assertEquals(email, response.jsonPath().getString("email"));
        assertEquals("1500.0", String.valueOf(response.jsonPath().getDouble("valor")));
        assertEquals(4, response.jsonPath().getInt("parcelas"));
        assertEquals(seguro, response.jsonPath().getBoolean("seguro"));
    }

    @Test
    public void quandoEnvioUmCpfJaCadastradoNaSimulcaoEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpfExistente, email, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("CPF duplicado", response.jsonPath().getString("mensagem"));
    }

    @Test
    public void quandoNaoInsiroValoresNosCamposObrigatoriosEntaoRetornaStatus400EMensagens() {
        response = simulacao.postSimulacao(null, null, null, null, null, null);

        assertEquals(400, response.getStatusCode());
        assertEquals("CPF não pode ser vazio", response.jsonPath().getString("erros.cpf"));
        assertEquals("Parcelas não pode ser vazio", response.jsonPath().getString("erros.parcelas"));
        assertEquals("Valor não pode ser vazio", response.jsonPath().getString("erros.valor"));
        assertEquals("Nome não pode ser vazio", response.jsonPath().getString("erros.nome"));
        assertEquals("E-mail não deve ser vazio", response.jsonPath().getString("erros.email"));
    }

    @Test
    public void quandoNaoInsiroCpfEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, null, email, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("CPF não pode ser vazio", response.jsonPath().getString("erros.cpf"));
    }

    @Test
    public void quandoNaoIsiroNomeEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(null, cpf, email, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Nome não pode ser vazio", response.jsonPath().getString("erros.nome"));
    }

    @Test
    public void quandoNaoPreenchoOCampoValorEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, email, null, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Valor não pode ser vazio", response.jsonPath().getString("erros.valor"));
    }

    //Inconsistencia na API
    //Nao deve ser aceito valores menores que mil, no campo "valor"
    @Test
    public void quandoPreenchoOCampoValorComUmValorMenorQueMilEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, email, 900.00, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void quandoPreenchoOCampoValorComMilEntaoRetornaStatus201() {
        response = simulacao.postSimulacao(nome, cpf, email, 1000.00, parcelas, seguro);

        assertEquals(201, response.getStatusCode());
        assertEquals("1000.0", String.valueOf(response.jsonPath().getDouble("valor")));
    }

    @Test
    public void quandoPreenchoOCampoValorIgualAQuarentaMilEntaoRetornaStatus201() {
        response = simulacao.postSimulacao(nome, cpf, email, 40000.00, parcelas, seguro);

        assertEquals(201, response.getStatusCode());
        assertEquals("40000.0", String.valueOf(response.jsonPath().getFloat("valor")));
    }

    @Test
    public void quandoPreenchoCampoValorComValorMaiorQueQuarentaMilEntaoRetorna400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, email, 40000.01, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Valor deve ser menor ou igual a R$ 40.000", response.jsonPath().getString("erros.valor"));
    }

    @Test
    public void quandoNaoPreenchoOCampoEmailEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, null, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("E-mail não deve ser vazio", response.jsonPath().getString("erros.email"));
    }

    @Test
    public void quandoInsiroUmEmailInvalidoEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, emailInvalido, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("E-mail deve ser um e-mail válido", response.jsonPath().getString("erros.email"));
    }

    @Test
    public void quandoNaoPreenchoOCampoParcelasEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, null, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Parcelas não pode ser vazio", response.jsonPath().getString("erros.parcelas"));
    }

    @Test
    public void quandoPreenchoOCampoParcelasComValorMenorQueDoisEntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, 1, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Parcelas deve ser igual ou maior que 2", response.jsonPath().getString("erros.parcelas"));
    }

    @Test
    public void quandoPreenchoOCampoParcelasComValorIgualADoisEntaoRetornaStatus201() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, 2, seguro);

        assertEquals(201, response.getStatusCode());
        assertEquals(2, response.jsonPath().getInt("parcelas"));
    }

    @Test
    public void quandoPreenchoOCampoParcelasComValorIgual48EntaoRetornaStatus201() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, 48, seguro);

        assertEquals(201, response.getStatusCode());
        assertEquals(48, response.jsonPath().getInt("parcelas"));

    }

    //Inconsistencia na API
    //Nao deve ser aceito valores maiores que 48 no campo de parcelas
    @Test
    public void quandoPreenchoOCampoParcelasComValorMaiorQue48EntaoRetornaStatus400EMensagem() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, 49, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Parcelas deve ser igual ou menor que 48", response.jsonPath().getString("erros.parcelas"));
    }

    @Test
    public void quandoPreenchoOCampoSeguroEntaoOCampoRetornaComoFalse() {
        response = simulacao.postSimulacao(nome, cpf, email, valor, parcelas, null);

        assertEquals(201, response.getStatusCode());
        assertFalse(response.jsonPath().getBoolean("seguro"));
    }
}
