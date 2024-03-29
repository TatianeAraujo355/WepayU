package br.ufal.ic.p2.wepayu.Sistema;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Empregados.Horista;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Comissionado;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Venda;
import br.ufal.ic.p2.wepayu.Exception.AgendaPagamento.DescricaoAgendaInvalida;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoDeveSerPosivitoException;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.atributos.NaoPodeSerNuloException;
import br.ufal.ic.p2.wepayu.Exception.dataE.DataException;
import br.ufal.ic.p2.wepayu.Exception.dataE.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoEhDoTipoException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoRecebeEmBanco;
import br.ufal.ic.p2.wepayu.Exception.empregado.NaoHaEmpregadoComEsseNomeException;
import br.ufal.ic.p2.wepayu.Persistencia.LerDadosDoArquivo;
import br.ufal.ic.p2.wepayu.Persistencia.SalvarDadosDoArquivo;
import br.ufal.ic.p2.wepayu.folhaPagamento.AgendaPagamentos;
import br.ufal.ic.p2.wepayu.folhaPagamento.GerarFolhaPagamento;

public class Sistema {
    // lista de todos os empregados do sistema
    private static LinkedHashMap<String, Empregado> empregados = new LinkedHashMap<>();
    //lista de todos os membros do sindicato
    private static Map<String, Empregado> membrosSindicato = new HashMap<>();
    // amazena a agenda de pagamento
    private static AgendaPagamentos ag = new AgendaPagamentos();
    // lista para facilitar a recuperacao dos empregados
    private LinkedHashMap<String, Empregado> estadoEmpregados;

    LerDadosDoArquivo l = new LerDadosDoArquivo();
    Invoker invoker = new Invoker();

    // carrega os dados do arquivo para os empregados
    public Sistema() {
        try {
            empregados = l.carregarEmpregadosDoArquivo("./database/Empregados.txt");
            empregados = l.lerHoras(empregados);
            empregados = l.lerVendas(empregados);
            empregados = l.lerTaxas(empregados);
            ag.listaTiposdePagamentos = l.lerListaAgendasDoArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void adicionarEmpregado(Empregado emp, String id) {
        if (empregados == null) {
            empregados = new LinkedHashMap<>();
        }
        empregados.put(id, emp);
    }

    public void setMembroSindicato(String id, Empregado emp) {
        if (emp == null) {
            membrosSindicato.remove(id);
        } else {
            membrosSindicato.put(id, emp);
        }
    }

    public void setAgendaPagamento(Empregado emp, String valor) {
        ag.agendaEmp(emp, valor);
    }

/////////////////////////////////////funcoes lancar///////////////////////////////////////////////

    // adicionar taxa de servico ao membro do sindicato//
    public void lancaTaxaServico(String idSindicato, String data, String valor) throws NaoPodeSerNuloException,
            AtributoNaoExisteException, AtributoDeveSerPosivitoException, DataInvalidaException {
        if (idSindicato.trim().isEmpty()) {
            throw new NaoPodeSerNuloException("Identificacao do membro", true);
        }
        Empregado emp = membrosSindicato.get(idSindicato);
        if (emp == null) {
            throw new AtributoNaoExisteException("Membro");
        }
        double valorDouble = Double.parseDouble(valor.replace(",", "."));
        if (valorDouble <= 0) {
            throw new AtributoDeveSerPosivitoException("Valor");
        }
        LocalDate dt = converterData(data, 0);
        if (dt != null) {
            invoker.executeCommand(new Command() {
                // @Override
                public void execute() {
                    emp.membroSindicato.setTaxaServico(dt, valor);
                }
                @Override
                public void undo() {
                    emp.getMembroSindicato().removeTaxaServico(dt);
                }
                public void redo() {
                }
            });
        }
    }

    // adicionar venda ao empregado comissionado//
    public void lancaVenda(String id, String data, String valor) throws NaoPodeSerNuloException, AtributoNaoExisteException, AtributoDeveSerPosivitoException,  DataInvalidaException,EmpregadoNaoEhDoTipoException {
        if (id == null || id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        LocalDate Data = converterData(data, 0);
        Empregado emp = empregados.get(id);
        if (emp == null) {
            throw new AtributoNaoExisteException("Empregado");
        }
        if (!(emp.getTipo().equals("comissionado"))) {
            throw new EmpregadoNaoEhDoTipoException("comissionado.");
        }
        double valorDouble = Double.parseDouble(valor.replace(",", "."));
        if (valorDouble <= 0) {
            throw new AtributoDeveSerPosivitoException("Valor");
        }
        Venda venda = new Venda(id, Data, valor);
        Comissionado emp1 = (Comissionado) emp;
        estadoEmpregados = new LinkedHashMap<>(empregados);
        invoker.executeCommand(new Command() {
            // @Override
            public void execute() {
                emp1.adicionarVenda(venda);
            }
            @Override
            public void undo() {
                emp1.removervenda();
            }
            public void redo() {
            }
        });
    }

    // lanca horas trabalhadas por data ao empregado horista//
    public void lancaCartaoPonto(String id, String data, String horas) throws NaoPodeSerNuloException,
            AtributoNaoExisteException, EmpregadoNaoEhDoTipoException, DataInvalidaException {
        if (id == null || id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        Empregado emp = empregados.get(id);
        if (emp == null) {
            throw new AtributoNaoExisteException("Empregado");
        }
        if (!(emp.getTipo().equals("horista"))) {
            throw new EmpregadoNaoEhDoTipoException("horista.");
        }
        LocalDate dt = converterData(data, 0);
        Horista emp1 = (Horista) emp;
        invoker.executeCommand(new Command() {
            // @Override
            public void execute() {
                emp1.setHorasNormaisTrabalhadas(dt, horas);
            }
            @Override
            public void undo() {
                emp1.removeHorasTrabalhadas(dt);
            }
            public void redo() {
            }
        });
    }

//////////////////////////////////////////funcoes buscar//////////////////////////////////////
    // retorna vendas do empregado comissionado //
    public String getVendasRealizadas(String id, String datainicial, String datafinal)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        LocalDate dataIni = converterData(datainicial, 1);
        LocalDate dataFim = converterData(datafinal, 2);
        if (dataFim.isBefore(dataIni)) {
            throw new DataException();
        }
        Empregado emp = empregados.get(id);

        if (!(emp.getTipo().equals("comissionado"))) {
            throw new EmpregadoNaoEhDoTipoException("comissionado.");
        }
        Comissionado emp1 = (Comissionado) emp;
        return emp1.getVendasRealizadas(dataIni, dataFim);
    }

    // retorna taxa de servico do sindicato//
    public String getTaxasServico(String id, String dataInicial, String dataFinal)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        LocalDate dataIni = converterData(dataInicial, 1);
        LocalDate dataFim = converterData(dataFinal, 2);

        if (dataFim.isBefore(dataIni)) {
            throw new DataException();
        }
        Empregado emp = empregados.get(id);
        if (emp.getMembroSindicato() == null) {
            throw new EmpregadoNaoEhDoTipoException("sindicalizado.");
        }
        return emp.getMembroSindicato().getTaxaServico(dataIni, dataFim);
    }

    // retorna horas normais ou extras trabalhadas em um intervalo de dias
    public String getHorasTrabalhadas(String id, String dataInicial, String dataFinal, boolean normais)
            throws EmpregadoNaoEhDoTipoException, DataException, DataInvalidaException {
        LocalDate datainicial = converterData(dataInicial, 1);
        LocalDate datafinal = converterData(dataFinal, 2);

        if (datafinal.isBefore(datainicial)) {
            throw new DataException();
        }
        Empregado emp = empregados.get(id);
        if (emp.getTipo().equals("horista")) {
            Horista emp1 = (Horista) emp;
            if (normais) {
                return emp1.getHorasTrabalhadas(datainicial, datafinal);
            } else {
                return emp1.getHorasExtrasTrabalhadas(datainicial, datafinal);
            }
        }
        throw new EmpregadoNaoEhDoTipoException("horista.");
    }
    public LinkedHashMap<String, Empregado> getEmpregados() {
        return empregados;
    }

    // buscar atributos //
    public String getAtributoEmpregado(String id, String atributo)
            throws NaoPodeSerNuloException, AtributoNaoExisteException, EmpregadoNaoEhDoTipoException, EmpregadoNaoRecebeEmBanco {

        if (id.trim().isEmpty()) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);

        }
        try {
            Empregado emp = empregados.get(id);
            return emp.getAtributo(atributo);
        } catch (NullPointerException e) {
            throw new AtributoNaoExisteException("Empregado");
        }
    }

    public String getEmpregadoPorNome(String nome, int indice) throws NaoHaEmpregadoComEsseNomeException {
        int cont = 1;
        for (String chave : empregados.keySet()) {
            Empregado emp = empregados.get(chave);
            if (emp.getNome().equals(nome)) {
                if (cont == indice) {
                    return chave;
                }
                cont++;
            }
        }
        throw new NaoHaEmpregadoComEsseNomeException();
    }

    public Empregado buscarEmpregado(String id) {
        return empregados.get(id);
    }

    public Empregado getMembroSindicato(String idsindicato) {
        return membrosSindicato.get(idsindicato);
    }
/////////////////////////////////////////////////////////////////////////////////////////////

    public void removerEmpregado(String id) throws NaoPodeSerNuloException, AtributoNaoExisteException {
        if (id.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        if (!empregados.containsKey(id)) {
            throw new AtributoNaoExisteException("Empregado");
        }
        Empregado emp = empregados.get(id);
        invoker.executeCommand(new Command() {
            // @Override
            public void execute() {
                empregados.remove(id);
            }
            @Override
            public void undo() {
                empregados.put(id, emp);
            }
            public void redo() {
            }
        });
    }

    // chama classe que calcula a folha de pagamento
    public String totalFolha(String data) throws DataInvalidaException {
        LocalDate dt = converterData(data, 0);
        GerarFolhaPagamento calc = new GerarFolhaPagamento();
        try {
            return calc.calcularEmpregados(dt, empregados, ag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0,00";
    }

    // criar tipo de agenda de pagamento
    public void criarAgendaPagamentos(String tipo) throws DescricaoAgendaInvalida {
        ag.criarTipoAgenda(tipo);
    }


    public void removeEmp(String id) {
        empregados.remove(id);
    }

    public void rodaFolha() {
    }

    // converte string em localdate//
    public LocalDate converterData(String data, int c) throws DataInvalidaException {
        String[] partes = data.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        LocalDate Data;
        try {
            Data = LocalDate.of(ano, mes, dia);
            return Data;
        } catch (java.time.DateTimeException e) {
            if (c == 0) {
                throw new DataInvalidaException(" ");
            } else if (c == 1) {
                throw new DataInvalidaException(" inicial ");
            } else if (c == 2) {
                throw new DataInvalidaException(" final ");
            }
        }
        return null;
    }

    public void encerrarSistema() throws IOException {
        estadoEmpregados = new LinkedHashMap<>(empregados);
        invoker.executeCommand(new Command() {
             @Override
            public void execute() {
             try {
                SalvarDadosDoArquivo sd = new SalvarDadosDoArquivo(getEmpregados(), ag.listaTiposdePagamentos);
            } catch (IOException e) {
                e.printStackTrace();
            }
                empregados.clear();
                membrosSindicato.clear();
                ag.limparAgenda();
                ag = new AgendaPagamentos();
                invoker.limparLista();
            }
          
            @Override
            public void undo() {
                empregados.putAll(estadoEmpregados);
                invoker.desfazL();
                
            }
            public void redo() {
               
            }
        });
    }
}
