package br.ufal.ic.p2.wepayu.Empregados;

import java.time.LocalDate;

import br.ufal.ic.p2.wepayu.Empregados.metodoPagamento.MetodoPagamento;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoEhDoTipoException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoRecebeEmBanco;

public class Empregado {
    protected String id;
    protected String nome;
    protected String endereco;
    protected String salario;
    protected LocalDate ultimoPagamento = null;
    protected Double descontosDevendo;
    public MembroSindicato membroSindicato;
    private MetodoPagamento metodoPagamento = new MetodoPagamento();
    private String TipoDeAgendaPagamento;

    public Empregado(String id, String Nome, String endereco, String salario) {
        this.id = id;
        this.nome = Nome;
        this.endereco = endereco;
        this.salario = salario;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getSalario() {
        return salario;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public MembroSindicato getMembroSindicato() {
        return membroSindicato;
    }

    public void setMembroSindicato(MembroSindicato membroSindicato) {
        this.membroSindicato = membroSindicato;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getTipo(){
        return "";
    }

    public LocalDate getUltimoPagamento() {
        return this.ultimoPagamento;
    }

    public void setUltimoPagamento(LocalDate ultimoPagamento) {
        this.ultimoPagamento = ultimoPagamento;
    }
    
    public String getAgendaPagamento() {
        return TipoDeAgendaPagamento;
    }

    public void setAgendaPagamento(String agendaPagamento) {
        TipoDeAgendaPagamento = agendaPagamento;
    }

    public void setDescontosDevendo(Double descontosDevendo) {
        this.descontosDevendo = descontosDevendo;
    }

    public Double getDescontosDevendo() {
        return descontosDevendo;
    }

    // buscar atributo //
    public String getAtributo(String atributo) throws EmpregadoNaoEhDoTipoException, AtributoNaoExisteException, EmpregadoNaoRecebeEmBanco {

        if (atributo.equals("nome")) {
            return getNome();
        } else if (atributo.equals("endereco")) {
            return getEndereco();
        } else if (atributo.equals("salario")) {
            return getSalario();
        }else if(atributo.equals("sindicalizado")){
            if (membroSindicato != null) {
                return "true";
            }else{
                return "false";
            }
        }
        else if (atributo.equals("idSindicato")) {
            if (membroSindicato != null) {
                return membroSindicato.getIdSindicato();
            } else {
                throw new EmpregadoNaoEhDoTipoException("sindicalizado.");
            }
        } else if (atributo.equals("taxaSindical")) {
            if (membroSindicato != null) {
                return membroSindicato.getTaxaSindical();
            } else {
                throw new EmpregadoNaoEhDoTipoException("sindicalizado.");
            }
        } else if (atributo.equals("metodoPagamento")) {
            if (metodoPagamento != null) {
                return metodoPagamento.getTipo();
            } else {
                return "emMaos";
            }
        } else if (atributo.equals("agencia")) {
            if (metodoPagamento != null && metodoPagamento.getBanco() != null) {
                return metodoPagamento.getBanco().getAgencia();
            } else {
                throw new EmpregadoNaoRecebeEmBanco();
            }
        } else if (atributo.equals("banco")) {
            if (metodoPagamento != null && metodoPagamento.getBanco() != null) {
                return metodoPagamento.getBanco().getNomeBanco();
            } else {
                throw new EmpregadoNaoRecebeEmBanco();
            }

        } else if (atributo.equals("contaCorrente")) {

            if (metodoPagamento != null && metodoPagamento.getBanco() != null) {
                return metodoPagamento.getBanco().getContaCorrente();
            } else {
                throw new EmpregadoNaoRecebeEmBanco();
            }
        }else if(atributo.equals("agendaPagamento")){
            return getAgendaPagamento();
        }
        throw new AtributoNaoExisteException("Atributo");

    }
}