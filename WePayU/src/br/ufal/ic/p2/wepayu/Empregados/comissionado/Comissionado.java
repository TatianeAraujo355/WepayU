package br.ufal.ic.p2.wepayu.Empregados.comissionado;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoEhDoTipoException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoRecebeEmBanco;

public class Comissionado extends Empregado {
    private String comissao;
    private List<Venda> vendasRealizadas;

    public Comissionado(String id, String nome, String endereco, String salario, String comissao) {
        super(id, nome, endereco, salario);
        this.comissao = comissao;
        this.vendasRealizadas = new ArrayList<>();
        super.setAgendaPagamento("semanal 2 5");

    }

    public String getComissao() {
        return comissao;
    }

    public void setComissao(String comissao) {
        this.comissao = comissao;
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
        return "comissionado";
    }

    public void adicionarVenda(Venda novaVenda) {
        if (this.vendasRealizadas == null) {
            this.vendasRealizadas = new ArrayList<>();
        }
        vendasRealizadas.add(novaVenda);
    }

    public void removervenda(){
        if (!vendasRealizadas.isEmpty()) {
            vendasRealizadas.remove(vendasRealizadas.size() - 1);
        } 
    }

    @Override
    public String getAtributo(String atributo) throws EmpregadoNaoEhDoTipoException, AtributoNaoExisteException, EmpregadoNaoRecebeEmBanco {

        if (atributo.equals("tipo")) {
            return "comissionado";
        } else if (atributo.equals("comissao")) {
            return getComissao();
        }
        return super.getAtributo(atributo);
    }

    public String getVendasRealizadas(LocalDate dataInicial, LocalDate dataFinal) {
        double total = 0;
        LocalDate dataVenda;
        Double valorVenda;

        if (vendasRealizadas != null) {
            for (Venda venda : vendasRealizadas) {
                dataVenda = venda.getData();
                valorVenda = Double.parseDouble(venda.getValor().replace(",", "."));
                // Verifica se a data da venda está dentro do intervalo especificado
                if (!dataVenda.isBefore(dataInicial) && dataVenda.isBefore(dataFinal)) {
                    total += valorVenda;
                }
            }

            String totalString = String.format("%.2f", total);
            totalString = totalString.replace('.', ',');

            return totalString;
        }

        return "0,0";

    }

    @Override
    public void setUltimoPagamento(LocalDate ultimoPagamento) {
        super.setUltimoPagamento(ultimoPagamento);
    }

    @Override
    public LocalDate getUltimoPagamento() {
        return super.getUltimoPagamento();
    }
    public List<Venda> listaVendas() {
        return this.vendasRealizadas;
    }

}
