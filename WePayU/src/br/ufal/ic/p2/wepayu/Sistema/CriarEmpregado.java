package br.ufal.ic.p2.wepayu.Sistema;

import br.ufal.ic.p2.wepayu.Empregados.Assalariado;
import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Empregados.Horista;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Comissionado;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoDeveSerNaoNegativoException;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoDeveSerNumericoException;
import br.ufal.ic.p2.wepayu.Exception.atributos.NaoPodeSerNuloException;
import br.ufal.ic.p2.wepayu.Exception.atributos.TipoException;
import br.ufal.ic.p2.wepayu.folhaPagamento.AgendaPagamentos;


public class CriarEmpregado {
    public AgendaPagamentos ag = new AgendaPagamentos();
    Sistema sistema = new Sistema();
    private int gerarId = 100;

    // criar empregado comissionado //
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws NaoPodeSerNuloException, AtributoDeveSerNumericoException, AtributoDeveSerNaoNegativoException, TipoException {

        if ((nome == null || nome.isEmpty())) {
            throw new NaoPodeSerNuloException("Nome", false);
        }
        if ((endereco == null || endereco.isEmpty())) {
            throw new NaoPodeSerNuloException("Endereco", false);
        }
        if ((salario == null || salario.isEmpty())) {
            throw new NaoPodeSerNuloException("Salario", false);
        }

        if (tipo.equals("assalariado") || tipo.equals("horista")) {
            throw new  TipoException(false);
        }

        if (comissao == null || comissao.equals("")) {
            throw new NaoPodeSerNuloException("Comissao", true);
        }

        Double Comissao;
        try {
            Comissao = Double.parseDouble(comissao.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new AtributoDeveSerNumericoException("Comissao", true);
        }
        if (Comissao <= 0) {
            throw new AtributoDeveSerNaoNegativoException("Comissao", true);
        }

        Double Salario;
        try {
            Salario = Double.parseDouble(salario.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new AtributoDeveSerNumericoException("Salario", false);
        }
        if (Salario <= 0) {
            throw new AtributoDeveSerNaoNegativoException("Salario", false);
        }

        String s = formatarNumero(salario);
        String c = formatarNumero(comissao);
        gerarId = gerarId + 1;
        int id = gerarId;
        Empregado emp = new Comissionado(String.valueOf(id), nome, endereco, s, c);

        Invoker invoker = new Invoker();
        invoker.executeCommand(new Command() {
            @Override
            public void execute() {
              //  Empregado emp = new Comissionado(String.valueOf(gerarId), nome, endereco, s, c);
                sistema.adicionarEmpregado(emp, String.valueOf(id));
                ag.agendaEmp(emp, "semanal 2 5");
            }

            @Override
            public void undo() {
                sistema.removeEmp(emp.getId());
            }

            @Override
            public void redo() {
                sistema.adicionarEmpregado(emp, String.valueOf(id));
            }
        });

        return String.valueOf(id);

    }

    // Criar empregado assalariado ou horista //
    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws NaoPodeSerNuloException, AtributoDeveSerNumericoException, AtributoDeveSerNaoNegativoException, TipoException {

        if ((nome == null || nome.isEmpty())) {
            throw new NaoPodeSerNuloException("Nome", false);
        }
        if ((endereco == null || endereco.isEmpty())) {
            throw new NaoPodeSerNuloException("Endereco", false);
        }
        if ((salario == null || salario.isEmpty())) {
            throw new NaoPodeSerNuloException("Salario", false);
        }
        Double Salario;
        try {
            Salario = Double.parseDouble(salario.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new AtributoDeveSerNumericoException("Salario", false);
        }
        if (Salario <= 0) {
            throw new AtributoDeveSerNaoNegativoException("Salario", false);
        }

        if (tipo.equals("comissionado")) {
            throw new TipoException(false);
        }

        String s = formatarNumero(salario);

        gerarId = gerarId + 1;
        int id = gerarId;
        Empregado emp;

        if (tipo.equals("horista")) {
            emp = new Horista(String.valueOf(id), nome, endereco, s);
            ag.agendaEmp(emp, "semanal 5");

        } else if (tipo.equals("assalariado")) {
            emp = new Assalariado(String.valueOf(id), nome, endereco, salario);
            ag.agendaEmp(emp, "mensal $");

        } else {
            throw new  TipoException(true);
        }

        Invoker invoker = new Invoker();
        invoker.executeCommand(new Command() {
            @Override
            public void execute() {
                sistema.adicionarEmpregado(emp, String.valueOf(id));
            }

            @Override
            public void undo() {
                sistema.removeEmp(emp.getId());
            }

            @Override
            public void redo() {
                sistema.adicionarEmpregado(emp, String.valueOf(id));
            }
        });

        return String.valueOf(id);

    }

    public static String formatarNumero(String numero) {
        if (numero.contains(",")) {
            // Se a string ja contem uma virgula, retorna a propria string
            return numero;
        } else {
            // Se não contem virgula, adiciona ",00" ao final da string
            return numero + ",00";
        }
    }
}
