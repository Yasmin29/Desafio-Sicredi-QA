package testeSimulacao;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import services.SimulacaoService;

import java.util.Locale;

import static org.junit.Assert.*;

public class SimulacaoPUT {
    private Faker faker;
    private String cpfParaEdicoes;
    private String nome;
    private String cpf;
    private String email;
    private Double valor;
    private Integer parcelas;
    private Boolean seguro;
    private String emailInvalido;
    private SimulacaoService simulacao;
    private Response response;

    public SimulacaoPUT(){
        this.faker = new Faker(new Locale("pt-BR"));
        this.simulacao = new SimulacaoService();
        this.cpfParaEdicoes = faker.number().digits(11);
        this.nome = faker.name().firstName();
        this.cpf = faker.number().digits(11);
        this.email = nome.replaceAll("\\s+", "") + ".email@email.com";
        this.valor = 1500.00;
        this.parcelas = 4;
        this.seguro = true;
        this.emailInvalido = "email@invalido";
    }

    @Before
    public void criarCpfParaEdicoes() {
        response = simulacao.postSimulacao("Cpf Para Edição", cpfParaEdicoes, "email@email.com", 2000.00, 2, false);
    }

    @Test
    public void quandoEditoOCampoCpfNaSimulacaoEntaoRetornaStatus200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, parcelas, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals(cpf, response.jsonPath().getString("cpf"));
    }

    @Test
    public void quandoIsiroUmNomeValidoEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, parcelas, seguro);
        assertEquals(200, response.getStatusCode());
        assertEquals(nome, response.jsonPath().getString("nome"));
    }

    @Test
    public void quandoIsiroUmEmailValidoEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, parcelas, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals(email, response.jsonPath().getString("email"));
    }

    @Test
    public void quandoPassoFalseEmSeguroEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, parcelas, false);

        assertEquals(200, response.getStatusCode());
        assertFalse(response.jsonPath().getBoolean("seguro"));
    }

    @Test
    public void quandoPassoTrueEmSeguroEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, parcelas, true);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getBoolean("seguro"));
    }

    @Test
    public void quandoInsiroUmCpfParaEditarQueNaoExisteNaSimulacaoEntaoRetorna404() {
        response = simulacao.putSimulacao("0000000000", nome, cpf, email, valor, parcelas, seguro);

        assertEquals(404, response.getStatusCode());
        assertEquals("CPF 0000000000 não encontrado", response.jsonPath().getString("mensagem"));
    }

    //API com inconsistencia
    //Na regra eh pedido para retornar status 409 ao editar com um cpf jah cadastrado na simulacao, porem esta retornanado 400;
    @Test
    public void quandoPreenchoOCampoCpfComUmCpfJaCadastradoNaSimulcaoEntaoRetorna409EMensagemCPFJaExiste() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, "66414919004", email, valor, parcelas, seguro);

        assertEquals(409, response.getStatusCode());
        assertEquals("CPF duplicado", response.jsonPath().getString("mensagem"));
    }

    //API com inconsistencia
    //Ao editar o campo "valor" ele nao eh modificado, mantendo informacao ja existente;
    @Test
    public void quandoInsiroValoresValidosNosCamposEntaoRetornaStatus200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, parcelas, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals(nome, response.jsonPath().getString("nome"));
        assertEquals(cpf, response.jsonPath().getString("cpf"));
        assertEquals("4", String.valueOf(response.jsonPath().getInt("parcelas")));
        assertEquals(seguro, response.jsonPath().getBoolean("seguro"));
        assertEquals(email, response.jsonPath().getString("email"));
        assertEquals("1500.0", String.valueOf(response.jsonPath().getFloat("valor")));
    }

    //Quando eh deixado um campo em branco, ele mantem a informacao ja existente, nao retornando status 400;
    @Test
    public void quandoNaoPreenchoCamposObrigatoriosEntaoRetornaErro400() {
        response = simulacao.putSimulacao(cpfParaEdicoes, null, null, null, null, null, null);

        assertEquals(404, response.getStatusCode());
        assertEquals("CPF não pode ser vazio", response.jsonPath().getString("erros.cpf"));
        assertEquals("Parcelas não pode ser vazio", response.jsonPath().getString("erros.parcelas"));
        assertEquals("Valor não pode ser vazio", response.jsonPath().getString("erros.valor"));
        assertEquals("Nome não pode ser vazio", response.jsonPath().getString("erros.nome"));
        assertEquals("E-mail não deve ser vazio", response.jsonPath().getString("erros.email"));
    }

    //Quando eh deixado um campo em branco, ele mantem a informacao ja existente, nao retornando status 400;
    @Test
    public void quandoNaoPreenchoCampoCpfEntaoRetornaErro400() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, null, email, valor, parcelas, seguro);
        assertEquals(400, response.getStatusCode());
        assertEquals("CPF não pode ser vazio", response.jsonPath().getString("erros.cpf"));
    }

    //Quando eh deixado um campo em branco, se mantem a informacao ja existente, nao retornando erro 400;
    @Test
    public void quandoNaoPreenchoOCampoNomeEntaoDeveRetornar400() {
        response = simulacao.putSimulacao(cpfParaEdicoes, null, cpf, email, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Nome não pode ser vazio", response.jsonPath().getString("erros.cpf"));
    }

    //Quando eh deixado um campo em branco, se mantem a informacao ja existente, nao retornando status 400;
    @Test
    public void quandoNaoPreenchoOCampoValorEntaoRetorna400() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, null, parcelas, seguro);
        assertEquals(400, response.getStatusCode());
        assertEquals("Valor não pode ser vazio", response.jsonPath().getString("erros.valor"));
    }

    //Quando nao eh preenchido um campo, ele mantem as informacoes existentes, nao retornando status 400;
    @Test
    public void quandoNaoPreenchoOCampoParcelasEntaoRetorna400EMensagem() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, null, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Parcelas não pode ser vazio", response.jsonPath().getString("erros.parcelas"));
    }

    //Quando nao eh preenchido um campo, ele mantem as informacoes existentes, nao retornando status 400;
    @Test
    public void quandoNaoPreenchoOCampoEmailEntaoRetorna400EMensagem() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, null, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("E-mail não deve ser vazio", response.jsonPath().getString("erros.email"));
    }

    //A mensagem de erro muda para 'não é um endereço de e-mail' e 'E-mail deve ser um e-mail válido';
    @Test
    public void quandoPreenchoComUmEmailInvalidoEntaoRetorna400EMensagem() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, emailInvalido, valor, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("E-mail deve ser um e-mail válido", response.jsonPath().getString("erros.email"));
    }

    //API com inconsistencia
    //Ao editar o campo "valor" ele nao eh modificado, mantendo informacao ja existente, e nao retornando status 400;
    @Test
    public void quandoPreenchoOCampoValorComValorMenorQueMilEntaoRetorna400EMensagem() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, 900.00, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
    }

    //API com inconsistencia
    //Ao editar o campo "valor" ele nao eh modificado, mantendo informacao ja existente;
    @Test
    public void quandoPreenchoOCampoValorIgualAMilEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, 1000.00, parcelas, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals("1000.0", String.valueOf(response.jsonPath().getDouble("valor")));
    }

    //API com inconsistencia
    //Ao editar o campo "valor" ele nao eh modificado, mantendo informacao ja existente;
    @Test
    public void quandoPreenchoOCampoValorIgualAQuarentaMilEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, 40000.00, parcelas, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals("40000.0", String.valueOf(response.jsonPath().getDouble("valor")));
    }

    //API com inconsistencia
    //Ao editar o campo "valor" ele nao eh modificado, por isso nao retorna status 400, e mantem informacao existente;
    @Test
    public void quandoPreenchoOCampoValorComMaisQueQuarentaMilEntaoRetorna400EMensagem() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, 40001.00, parcelas, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Valor deve ser menor ou igual a R$ 40.000", response.jsonPath().getString("erros.valor"));
    }

    @Test
    public void quandoPreenchoOCampoParcelasComValorMenorQueDoisEntaoRetorna400EMensagem() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, 1, seguro);

        assertEquals(400, response.getStatusCode());
        assertEquals("Parcelas deve ser igual ou maior que 2", response.jsonPath().getString("erros.parcelas"));
    }

    @Test
    public void quandoPreenchoOCampoParcelasComValorIgualDoisEntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, 2, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals(2, response.jsonPath().getInt("parcelas"));
    }

    @Test
    public void quandoPreenchoOCampoParcelasComValorIgual48EntaoRetorna200() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, 48, seguro);

        assertEquals(200, response.getStatusCode());
        assertEquals(48, response.jsonPath().getInt("parcelas"));
    }

    //Inconsistencia na API
    //Nao deve ser permitido inserir um valor maior que 48 em parcelas;
    @Test
    public void quandoPreenchoOCampoParcelasComUmValorMaiorQue48EntaoRetorna400() {
        response = simulacao.putSimulacao(cpfParaEdicoes, nome, cpf, email, valor, 49, seguro);
        response.then().log().all();

        assertEquals(400, response.getStatusCode());
        assertEquals("Parcelas deve ser igual ou menor que 48", response.jsonPath().getString("erros.parcelas"));
    }
}
