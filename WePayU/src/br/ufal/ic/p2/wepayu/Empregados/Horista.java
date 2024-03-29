package br.ufal.ic.p2.wepayu.Empregados;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import br.ufal.ic.p2.wepayu.Exception.atributos.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoEhDoTipoException;
import br.ufal.ic.p2.wepayu.Exception.empregado.EmpregadoNaoRecebeEmBanco;

public class Horista extends Empregado {
    private Map<LocalDate, String> HorasNormaisTrabalhadas;
    private Map<LocalDate, String> HorasExtrasTrabalhadas;
    
    public Horista(String id, String nome, String endereco, String salario) {
        super(id, nome, endereco, salario);
        this.HorasExtrasTrabalhadas = new HashMap<>();
        this.HorasNormaisTrabalhadas = new HashMap<>();
        super.setAgendaPagamento("semanal 5");
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
        return "horista";
    }

    @Override
    public String getAtributo(String atributo) throws EmpregadoNaoEhDoTipoException, AtributoNaoExisteException, EmpregadoNaoRecebeEmBanco {
        if (atributo.equals("tipo")) {
            return "horista";
        } else if (atributo.equals("comissao")) {
            throw new EmpregadoNaoEhDoTipoException("comissionado.");
        }

        return super.getAtributo(atributo);

    }

    @Override
    public LocalDate getUltimoPagamento() {
        return super.getUltimoPagamento();
    }

    @Override
    public void setUltimoPagamento(LocalDate ultimoPagamento) {
        super.setUltimoPagamento(ultimoPagamento);
    }

    public void setHorasNormaisTrabalhadas(LocalDate data, String horas) {
        double valorDouble = Double.parseDouble(horas.replace(",", "."));
        if (valorDouble <= 0) {
            throw new IllegalArgumentException("Horas devem ser positivas.");
        }
        if (valorDouble > 8) {
            Double excedente = valorDouble - 8;
            String numeroString = String.valueOf(excedente).replace('.', ',');
            setHorasExtrasTrabalhadas(data, numeroString);

            this.HorasNormaisTrabalhadas.put(data, "8");
        } else {
            this.HorasNormaisTrabalhadas.put(data, horas);
        }
    }

    public void setHorasExtrasTrabalhadas(LocalDate data, String horas) {
        this.HorasExtrasTrabalhadas.put(data, horas);
    }

    public void removeHorasTrabalhadas(LocalDate data){
        if(HorasNormaisTrabalhadas.get(data) != null){
            HorasNormaisTrabalhadas.remove(data);
        }
        if(HorasExtrasTrabalhadas.get(data) != null){
            HorasExtrasTrabalhadas.remove(data);
        }
    }

    public String getHorasTrabalhadas(LocalDate dataInicial, LocalDate dataFinal) {
        double total = 0;
        double valorDouble = 0;
        String valor;
        String totalString;

        if (this.HorasNormaisTrabalhadas != null) {
            for (LocalDate data = dataInicial; data.isBefore(dataFinal); data = data.plusDays(1)) {
                valor = HorasNormaisTrabalhadas.get(data);
                if (valor != null) {
                    valorDouble = Double.parseDouble(valor.replace(",", "."));
                    total += valorDouble;
                }
            }
        }

        if (total == (int) total) {
            totalString = String.valueOf((int) total);
        } else {
            totalString = String.valueOf(total).replace('.', ',');
        }

        return totalString;

    }

    public Map<LocalDate, String> ListaHorasNormais() {
        return this.HorasNormaisTrabalhadas;
    }

    public String getHorasExtrasTrabalhadas(LocalDate dataInicial, LocalDate dataFinal) {
        double total = 0.0;
        double valorDouble = 0;
        String valor;
        String totalString;

        if (this.HorasExtrasTrabalhadas != null) {
            for (LocalDate data = dataInicial; data.isBefore(dataFinal); data = data.plusDays(1)) {
                valor = HorasExtrasTrabalhadas.get(data);
                if (valor != null) {
                    valorDouble = Double.parseDouble(valor.replace(",", "."));
                    total += valorDouble;
                }
            }
        }
        if (total == (int) total) {
            totalString = String.valueOf((int) total);
        } else {
            totalString = String.valueOf(total).replace('.', ',');
        }

        return totalString;

    }

    public Map<LocalDate, String> ListaHorasExtras() {
        return this.HorasExtrasTrabalhadas;
    }

}
