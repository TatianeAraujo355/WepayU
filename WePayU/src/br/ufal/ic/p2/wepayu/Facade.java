package br.ufal.ic.p2.wepayu;

import java.io.IOException;
import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoaDesfaze;
import br.ufal.ic.p2.wepayu.Exception.NaoPodeDarComandoDeposDeEncerrarSistemaException;
import br.ufal.ic.p2.wepayu.Exception.AgendaPagamento.DescricaoAgendaInvalida;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoDeveSerNaoNegativoException;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoDeveSerNumericoException;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoDeveSerPosivitoException;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.atributos.NaoPodeSerNuloException;
import br.ufal.ic.p2.wepayu.Exception.atributos.TipoException;
import br.ufal.ic.p2.wepayu.Exception.dataE.DataException;
import br.ufal.ic.p2.wepayu.Exception.dataE.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoEhDoTipoException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoRecebeEmBanco;
import br.ufal.ic.p2.wepayu.Exception.empregado.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.Sistema.AlterarEmpregado;
import br.ufal.ic.p2.wepayu.Sistema.Command;
import br.ufal.ic.p2.wepayu.Sistema.CriarEmpregado;
import br.ufal.ic.p2.wepayu.Sistema.Invoker;
import br.ufal.ic.p2.wepayu.Sistema.Sistema;

public class Facade {

    Sistema sistema = new Sistema();
    AlterarEmpregado alterarEmpregado = new AlterarEmpregado();
    CriarEmpregado criarempregado = new CriarEmpregado();
    Invoker invoker = new Invoker();

    public void zerarSistema() {
        try {
            sistema.encerrarSistema();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NaoPodeSerNuloException, AtributoDeveSerNumericoException, AtributoDeveSerNaoNegativoException, TipoException {
        return criarempregado.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NaoPodeSerNuloException, AtributoDeveSerNumericoException, AtributoDeveSerNaoNegativoException, TipoException {

        return criarempregado.criarEmpregado(nome, endereco, tipo, salario);
    }

    public void undo() throws NaoPodeDarComandoDeposDeEncerrarSistemaException, NaoHaComandoaDesfaze {
        invoker.undoLastCommand();
    }

    public void alteraEmpregado(String id, String atributo, String valor1) throws NaoPodeSerNuloException, EmpregadoNaoExisteException {
        if (id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        Empregado emp = sistema.buscarEmpregado(id);

        if (emp == null) {
            throw new EmpregadoNaoExisteException();
        }
        alterarEmpregado.alteraEmpregado(emp, atributo, valor1);
    }

    public void alteraEmpregado(String id, String atributo, boolean valor, String idSindicato, String taxaSindical)
            throws NaoPodeSerNuloException, EmpregadoNaoExisteException {
        if (id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        Empregado emp = sistema.buscarEmpregado(id);

        if (emp == null) {
            throw new EmpregadoNaoExisteException();
        }
        alterarEmpregado.alteraEmpregado(emp, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado(String id, String atributo, String tipo, String banco, String agencia,
            String contacorrente) throws NaoPodeSerNuloException, EmpregadoNaoExisteException {
        if (id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        Empregado emp = sistema.buscarEmpregado(id);

        if (emp == null) {
            throw new EmpregadoNaoExisteException();
        }
        alterarEmpregado.alteraEmpregado(emp, atributo, tipo, banco, agencia, contacorrente);
    }

    public void alteraEmpregado(String id, String tipo, String valor1, String comissao) throws NaoPodeSerNuloException, EmpregadoNaoExisteException {
        if (id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        Empregado emp = sistema.buscarEmpregado(id);

        if (emp == null) {
            throw new EmpregadoNaoExisteException();
        }
        alterarEmpregado.alteraEmpregado(emp, tipo, valor1, comissao);
    }

    public int getNumeroDeEmpregados() {
        return sistema.getEmpregados().size();
    }

    public String getAtributoEmpregado(String id, String atributo)
            throws NaoPodeSerNuloException, AtributoNaoExisteException, EmpregadoNaoEhDoTipoException, EmpregadoNaoRecebeEmBanco {
        return sistema.getAtributoEmpregado(id, atributo);
    }

    public void lancaTaxaServico(String idSindicato, String data, String valor) throws NaoPodeSerNuloException,
            AtributoNaoExisteException, AtributoDeveSerPosivitoException, DataInvalidaException {
        sistema.lancaTaxaServico(idSindicato, data, valor);
    }

    public void lancaVenda(String id, String data, String valor)
            throws NaoPodeSerNuloException, AtributoNaoExisteException, AtributoDeveSerPosivitoException,
            DataInvalidaException, EmpregadoNaoEhDoTipoException {
        sistema.lancaVenda(id, data, valor);
    }

    public void lancaCartao(String id, String data, String horas) throws NaoPodeSerNuloException,
            AtributoNaoExisteException, EmpregadoNaoEhDoTipoException, DataInvalidaException {
        sistema.lancaCartaoPonto(id, data, horas);
    }

    public String getVendasRealizadas(String id, String datainicial, String datafinal)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        return sistema.getVendasRealizadas(id, datainicial, datafinal);
    }

    public String getTaxasServico(String id, String dataInicial, String dataFinal)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        return sistema.getTaxasServico(id, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        return sistema.getHorasTrabalhadas(id, dataInicial, dataFinal, false);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        return sistema.getHorasTrabalhadas(id, dataInicial, dataFinal, true);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        return sistema.getEmpregadoPorNome(nome, indice);
    }

    public String totalFolha(String data) throws DataInvalidaException {
        return sistema.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) {
        invoker.executeCommand(new Command() {
            // @Override
            public void execute() {
                sistema.rodaFolha();
            }

            @Override
            public void undo() {
            }

            public void redo() {
            }
        });
    }

    public void criarAgendaDePagamentos(String tipo) throws DescricaoAgendaInvalida {
        sistema.criarAgendaPagamentos(tipo);
    }

    public void redo() throws NaoHaComandoaDesfaze {
        invoker.redoLastUndoneCommand();
    }

    public void removerEmpregado(String id) throws NaoPodeSerNuloException, AtributoNaoExisteException {
        sistema.removerEmpregado(id);
    }

    public void encerrarSistema() {
        try {
            sistema.encerrarSistema();
            invoker.encerrarSistema = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
