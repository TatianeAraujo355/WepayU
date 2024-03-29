package br.ufal.ic.p2.wepayu.Sistema;

import br.ufal.ic.p2.wepayu.Empregados.Assalariado;
import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Empregados.Horista;
import br.ufal.ic.p2.wepayu.Empregados.MembroSindicato;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Comissionado;
import br.ufal.ic.p2.wepayu.Empregados.metodoPagamento.Banco;
import br.ufal.ic.p2.wepayu.Empregados.metodoPagamento.MetodoPagamento;
import br.ufal.ic.p2.wepayu.Exception.atributos.NaoPodeSerNuloException;

public class AlterarEmpregado {
    Sistema sistema = new Sistema();
    Invoker invoker = new Invoker();

    // alterar tipo empregado para comissionado
    public void alteraEmpregado(Empregado emp, String atributo, String tipo, String valor) {

        valor = formatarNumero(valor);
        Empregado anterior = emp;

        final Empregado emp1 = converteTipo(emp, tipo, valor);
        invoker.executeCommand(new Command() {
            // @Override
            public void execute() {

                sistema.adicionarEmpregado(emp1, emp1.getId());
            }

            @Override
            public void undo() {
                sistema.adicionarEmpregado(anterior, anterior.getId());
            }

            public void redo() {
            }

        });

    }

    // Altera atributo unico em empregado //
    public void alteraEmpregado(Empregado emp, String atribut, String valor1) throws NaoPodeSerNuloException {
        Empregado anterior = emp;
        MetodoPagamento mpag = emp.getMetodoPagamento();
        String vAnt = emp.getMetodoPagamento().getTipo();

        if ((valor1 == null || valor1.isEmpty()) && !atribut.equals("comissao")) {
            atribut = Character.toUpperCase(atribut.charAt(0)) + atribut.substring(1);
            throw new NaoPodeSerNuloException(atribut, false);
        }
        final String atributo = atribut;

        invoker.executeCommand(new Command() {
            // @Override
            public void execute() {
                if (atributo.equals("nome")) {
                    emp.setNome(valor1);
                } else if (atributo.equals("endereco")) {
                    emp.setEndereco(valor1);
                } else if (atributo.equals("tipo")) {
                    Empregado emp1 = converteTipo(emp, valor1, null); // comissao = nulo
                    // arrumar para instancia correta
                    sistema.adicionarEmpregado(emp1, emp1.getId());

                } else if (atributo.equals("sindicalizado"))

                {
                    if (valor1.equals("true") || valor1.equals("false")) {
                        if (valor1.equals("false")) {
                            emp.membroSindicato = null;
                        }
                    } else {
                        throw new IllegalArgumentException("Valor deve ser true ou false.");
                    }
                } else if (atributo.equals("salario")) {
                    Double SalarioD;
                    try {
                        SalarioD = Double.parseDouble(valor1.replace(",", "."));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Salario deve ser numerico.");
                    }
                    if (SalarioD <= 0) {
                        throw new IllegalArgumentException("Salario deve ser nao-negativo.");
                    }
                    String Salario = formatarNumero(valor1);
                    emp.setSalario(Salario);

                } else if (atributo.equals("metodoPagamento")) {
                    if (!valor1.equals("emMaos") && !valor1.equals("banco") && !valor1.equals("correios")) {
                        throw new IllegalArgumentException("Metodo de pagamento invalido.");
                    }
                    emp.getMetodoPagamento().setTipo(valor1);
                } else if (atributo.equals("comissao")) {

                    if (valor1 == null || valor1.equals("")) {
                        throw new IllegalArgumentException("Comissao nao pode ser nula.");
                    }
                    Double comissao;
                    try {
                        comissao = Double.parseDouble(valor1.replace(",", "."));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Comissao deve ser numerica.");
                    }
                    if (comissao <= 0) {
                        throw new IllegalArgumentException("Comissao deve ser nao-negativa.");
                    }

                    if (emp.getTipo().equals("comissionado")) {
                        Comissionado emp1 = (Comissionado) emp;
                        emp1.setComissao(valor1);
                    } else {
                        throw new IllegalArgumentException("Empregado nao eh comissionado.");
                    }

                } else if (atributo.equals("agendaPagamento")) {
                    sistema.setAgendaPagamento(emp, valor1);
                    emp.setAgendaPagamento(valor1);

                } else {
                    throw new IllegalArgumentException("Atributo nao existe.");
                }

            }

            @Override
            public void undo() {
                anterior.getMetodoPagamento().setTipo(vAnt);
                sistema.adicionarEmpregado(anterior, anterior.getId());
            }

            public void redo() {
            }
        });

    }

    // Adicionar sindicato do empregado //
    public void alteraEmpregado(Empregado emp, String atributo, boolean valor, String idSindicato,
            String taxaSindical) throws NaoPodeSerNuloException {

        if (emp.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do empregado", true);
        }
        if (idSindicato.equals("")) {
            throw new NaoPodeSerNuloException("Identificacao do sindicato", true);

        }

        if (taxaSindical == null || taxaSindical.equals("")) {
            throw new IllegalArgumentException("Taxa sindical nao pode ser nula.");
        }
        Double Taxa;
        try {
            Taxa = Double.parseDouble(taxaSindical.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Taxa sindical deve ser numerica.");
        }
        if (Taxa <= 0) {
            throw new IllegalArgumentException("Taxa sindical deve ser nao-negativa.");
        }
        taxaSindical = formatarNumero(taxaSindical);

        if (atributo.equals("sindicalizado")) {
            if (valor == true) {

                if (idSindicato != null && taxaSindical != null) {
                    if (sistema.getMembroSindicato(idSindicato) != null) {
                        throw new IllegalArgumentException(
                                "Ha outro empregado com esta identificacao de sindicato");
                    }
                    MembroSindicato antigo = emp.getMembroSindicato();
                    MembroSindicato membro = new MembroSindicato(idSindicato, taxaSindical);
                    invoker.executeCommand(new Command() {
                        // @Override
                        public void execute() {
                            emp.setMembroSindicato(membro);
                            sistema.setMembroSindicato(idSindicato, emp);
                        }

                        @Override
                        public void undo() {
                            emp.setMembroSindicato(null);
                            sistema.setMembroSindicato(idSindicato, null);
                        }

                        public void redo() {
                        }
                    });
                }
            }
        }

    }

    // Alterar metodo de pagamento para receber em banco //
    public void alteraEmpregado(Empregado emp, String atributo, String tipo, String banco, String agencia,
            String contacorrente) throws NaoPodeSerNuloException {
        if (banco.equals("")) {
            throw new NaoPodeSerNuloException("Banco", false);
        }
        if (agencia.equals("")) {
            throw new NaoPodeSerNuloException("Agencia", false);
        }
        if (contacorrente.equals("")) {
            throw new NaoPodeSerNuloException("Conta corrente", false);
        }

        if (tipo.equals("banco")) {
            MetodoPagamento met = new MetodoPagamento();
            Banco bank = new Banco(banco, agencia, contacorrente);
            MetodoPagamento anterior = emp.getMetodoPagamento();

            invoker.executeCommand(new Command() {
                // @Override
                public void execute() {
                    met.setBanco(bank);
                    emp.setMetodoPagamento(met);
                }

                @Override
                public void undo() {
                    emp.setMetodoPagamento(anterior);
                }

                public void redo() {
                }
            });
        }

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

    // converter tipo de empregado
    public Empregado converteTipo(Empregado emp, String empDestino, String valor) {
        if (valor != null) {
            if (empDestino.equals("comissionado")) {
                Comissionado empregado = new Comissionado(emp.getId(), emp.getNome(), emp.getEndereco(),
                        emp.getSalario(), valor);
                return empregado;
            } else if (empDestino.equals("assalariado")) {
                Assalariado empregado = new Assalariado(emp.getId(), emp.getNome(), emp.getEndereco(), valor);
                return empregado;
            }
            if (empDestino.equals("horista")) {
                Horista empregado = new Horista(emp.getId(), emp.getNome(), emp.getEndereco(), valor);
                return empregado;
            }
        }
        if (empDestino.equals("assalariado")) {
            Assalariado empregado = new Assalariado(emp.getId(), emp.getNome(), emp.getEndereco(),
                    "0");
            return empregado;
        }
        if (empDestino.equals("horista")) {
            Horista empregado = new Horista(emp.getId(), emp.getNome(), emp.getEndereco(), "0");
            return empregado;
        }
        throw new IllegalArgumentException("Tipo invalido.");
    }

}
