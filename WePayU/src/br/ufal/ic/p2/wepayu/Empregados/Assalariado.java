package br.ufal.ic.p2.wepayu.Empregados;

import java.time.LocalDate;

import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoEhDoTipoException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoRecebeEmBanco;

public class Assalariado extends Empregado {

    public Assalariado(String id, String nome, String endereco, String salario) {
        super(id, nome, endereco, salario);
        super.setAgendaPagamento("mensal $");
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public String getNome() {
        return super.getNome();
    }

    @Override
    public String getEndereco() {
        return super.getEndereco();
    }

    @Override
    public String getTipo() {
        return "assalariado";
    }
    @Override
    public LocalDate getUltimoPagamento() {
        return super.getUltimoPagamento();
    }

    @Override
    public void setUltimoPagamento(LocalDate ultimoPagamento) {
        super.setUltimoPagamento(ultimoPagamento);
    }

    @Override
    public void setDescontosDevendo(Double descontosDevendo) {
        super.setDescontosDevendo(descontosDevendo);
    }

    @Override
    public Double getDescontosDevendo() {
        return super.getDescontosDevendo();
    }

    public String getAtributo(String atributo) throws EmpregadoNaoEhDoTipoException, AtributoNaoExisteException, EmpregadoNaoRecebeEmBanco {
        if (atributo.equals("tipo")) {
            return "assalariado";
        } else if (atributo.equals("comissao")) {
            throw new IllegalArgumentException("Empregado nao eh comissionado.");
        }
        return super.getAtributo(atributo);
    }

}
