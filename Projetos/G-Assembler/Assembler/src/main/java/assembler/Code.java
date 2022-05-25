/**
 * Curso: Elementos de Sistemas
 * Arquivo: Code.java
 */

package assembler;

import java.util.ArrayList;

/**
 * Traduz mnemônicos da linguagem assembly para códigos binários da arquitetura Z0.
 */
public class Code {

    /**
     * Retorna o código binário do(s) registrador(es) que vão receber o valor da instrução.
     * @param  mnemnonic vetor de mnemônicos "instrução" a ser analisada.
     * @return Opcode (String de 4 bits) com código em linguagem de máquina para a instrução.
     */
    public static String dest(String[] mnemnonic) {
        // A   -> 1 -> 0001
        // D   -> 2 -> 0010
        // (A) -> 4 -> 0100

        int destination = 0;
        ArrayList<String> destinationMnemonics = new ArrayList<>();

        String command = mnemnonic[0];
        if (command.equals("movw") && mnemnonic.length == 4) {
            // Destino pode ser as duas últimas coisas caso hajam 3 argumentos no movw
            destinationMnemonics.add(mnemnonic[2]);
            destinationMnemonics.add(mnemnonic[3]);
        } else {
            destinationMnemonics.add(mnemnonic[mnemnonic.length-1]);
        }

        for (String element : destinationMnemonics) {
            switch (element) {
                case "%A":
                    destination += 1;
                    break;
                case "%D":
                    destination += 2;
                    break;
                case "(%A)":
                    destination += 4;
                    break;
            }
        }

        String bin = Integer.toBinaryString(destination);
    	return String.format("%4s", bin).replace(' ', '0');
    }

    /**
     * Retorna o código binário do mnemônico para realizar uma operação de cálculo.
     * @param  mnemnonic vetor de mnemônicos "instrução" a ser analisada.
     * @return Opcode (String de 7 bits) com código em linguagem de máquina para a instrução.
     */
    public static String comp(String[] mnemnonic) {
        String command = mnemnonic[0];

        String binary = "0001100";

        if(command.equals("movw")) {

            if((mnemnonic[1].equals("%A"))) {
                binary = "0110000";
            } else if (mnemnonic[1].equals("%D")) {
                binary = "0001100";
            } else if (mnemnonic[1].equals("(%A)")) {
                binary = "1110000";
            }
        }

        if (command.equals("addw")) {
            if ((mnemnonic[1].equals("%A")) && (mnemnonic[2].equals("%D")) ||(mnemnonic[1].equals("%D")) && (mnemnonic[2].equals("%A")) ) {
                binary = "0000010";
            } else if ((mnemnonic[1].equals("(%A)")) && (mnemnonic[2].equals("%D")) || (mnemnonic[1].equals("%D")) && (mnemnonic[2].equals("(%A)"))) {
                binary = "1000010";
            } else if(mnemnonic[1].equals("$1")) {
                binary = "1110111";
            }
        }
        if (command.equals("subw")) {
            if (mnemnonic[1].equals("%D") && mnemnonic[2].equals("(%A)")) {
                binary = "1010011";
            } else if (mnemnonic[1].equals("%D") && mnemnonic[2].equals("%A")) {
                binary = "0010011";
            } else if (mnemnonic[1].equals("%A") && mnemnonic[2].equals("%D")) {
                binary = "0000111";
            } else if (mnemnonic[1].equals("(%A)") && mnemnonic[2].equals("%D")) {
                binary = "1000111";
            } else if (mnemnonic[1].equals("%D") && mnemnonic[2].equals("$1")) {
                binary = "0001110";
            } else if (mnemnonic[1].equals("%A") && mnemnonic[2].equals("$1")) {
                binary = "0110010";
            } else if (mnemnonic[1].equals("(%A)") && mnemnonic[2].equals("$1")) {
                binary = "1110010";
            }
        }
        if (command.equals("rsubw")) {
            if ((mnemnonic[2].equals("%A")) || (mnemnonic[1].equals("%A"))) {
                binary = "0000111";
            } else if ((mnemnonic[2].equals("(%A)")) || (mnemnonic[1].equals("(%A)"))) {
                binary = "1000111";
            }
        }
        if (command.equals("incw")) {
            if (mnemnonic[1].equals("%D")) {
                binary = "0011111";
            } else if (mnemnonic[1].equals("(%A)")) {
                binary = "1110111";
            } else if (mnemnonic[1].equals("%A")) {
                binary = "0110111";
            }
        }
        if (command.equals("decw")) {
            if (mnemnonic[1].equals("%D")) {
                binary = "0001110";
            } else if (mnemnonic[1].equals("(%A)")) {
                binary = "1110010";
            } else if (mnemnonic[1].equals("%A")) {
                binary = "0110010";
            }
        }
        if (command.equals("notw")) {
            if (mnemnonic[1].equals("%D")) {
                binary = "0001101";
            } else if (mnemnonic[1].equals("(%A)")) {
                binary = "1110001";
            } else if (mnemnonic[1].equals("%A")) {
                binary = "0110001";
            }
        }
        if (command.equals("negw")) {
            if (mnemnonic[1].equals("%D")) {
                binary = "0001111";
            } else if (mnemnonic[1].equals("(%A)")) {
                binary = "1110011";
            } else if (mnemnonic[1].equals("%A")) {
                binary = "0110011";
            }
        }
        if (command.equals("andw")) {
            if ((mnemnonic[1].equals("%A") && mnemnonic[2].equals("%D")) || (mnemnonic[2].equals("%A") && mnemnonic[1].equals("%D"))) {
                binary = "0000000";
            } else if ((mnemnonic[1].equals("(%A)") && mnemnonic[2].equals("%D")) || (mnemnonic[2].equals("(%A)") && mnemnonic[1].equals("%D"))) {
                binary = "1000000";
            }
        }
        if (command.equals("orw")) {
            if (mnemnonic[1].equals("%A")) {
                binary = "0010101";
            } else if (mnemnonic[1].equals("(%A)")) {
                binary = "1010101";
            } else if (mnemnonic[1].equals("%D")) {
                binary = "0010101";
            }
        }


        return "00" + binary;
    }
    /**
     * Retorna o código binário do mnemônico para realizar uma operação de jump (salto).
     * @param  mnemnonic vetor de mnemônicos "instrução" a ser analisada.
     * @return Opcode (String de 3 bits) com código em linguagem de máquina para a instrução.
     */
    public static String jump(String[] mnemnonic) {
        switch (mnemnonic[0]) {
            case "jmp":
                return "111";
            case "je":
                return "010";
            case "jne":
                return "101";
            case "jg":
                return "001";
            case "jge":
                return "011";
            case "jl":
                return "100";
            case "jle":
                return "110";
            default:
                return "000";
        }
    }

    /**
     * Retorna o código binário de um valor decimal armazenado numa String.
     * @param  symbol valor numérico decimal armazenado em uma String.
     * @return Valor em binário (String de 15 bits) representado com 0s e 1s.
     */
    public static String toBinary(String symbol) {
        // Transformar o número decimal contido na string symbol em binário
        String bin = Integer.toBinaryString(Integer.parseInt(symbol));

        // Adicionar padding de zeros à esquerda string pois queremos sempre 16 bits
    	return String.format("%16s", bin).replace(' ', '0');
    }

}
